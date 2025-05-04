package com.doubletapp_hw

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import com.doubletapp_hw.apiUsage.ErrorResponse
import com.doubletapp_hw.apiUsage.HabitApiService
import com.doubletapp_hw.apiUsage.HabitUID
import com.doubletapp_hw.apiUsage.toLocalModel
import com.doubletapp_hw.apiUsage.toNetworkModel
import com.doubletapp_hw.db.HabitDao
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.delay
import retrofit2.Response

class HabitRepository(
    private val habitDao: HabitDao,
    private val api: HabitApiService,
    private val token: String,
    private val context: Context, //Только для тоста
) {
    private companion object {
        const val MAX_API_RETRIES = 5  // Максимальное количество попыток
        const val RETRY_DELAY_MS = 5000L  // Задержка между попытками
    }

    val habits: LiveData<List<Habit>> = habitDao.getAllHabits()

    suspend fun getHabitById(id: String): Habit? {
        return habitDao.getHabitById(id)
    }

    suspend fun saveHabit(habit: Habit) {
        habit.isSynced = false
        habitDao.insertHabit(habit)
        //После сохранения локально сразу пытаемся отправить на сервер
        putHabitsToServer(listOf(habit))
    }

    suspend fun deleteHabit(habit: Habit) {
        habit.isDeleted = true
        habit.isSynced = false
        habitDao.insertHabit(habit)
        deleteHabitsOnServer(listOf(habit))
    }

    suspend fun syncWithServer() {
        var serverHabits: List<Habit>? = null
        var attempts = 0

        //Пытаемся получить привычки с сервера
        while (serverHabits == null && attempts < MAX_API_RETRIES) {
            attempts++
            try {
                val response = api.getHabits(token)
                if (response.isSuccessful) {
                    serverHabits = response.body()?.map { it.toLocalModel() } ?: listOf()
                } else {
                    logApiError("GET habits failed", response)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (serverHabits == null) delay(RETRY_DELAY_MS)
        }

        if (serverHabits == null) {
            Log.e("Sync", "Failed after $MAX_API_RETRIES attempts")
            return
        }

        //Получаем локальные привычки и объединяем их с привычками с сервера
        val localHabits = habits.value ?: listOf()
        val mergedHabits = mergeHabits(localHabits, serverHabits)

        //Обновляем привычки в локальной бд
        habitDao.insertHabits(mergedHabits)

        //Засылаем на сервер новые и не синхронизированные привычки
        putHabitsToServer(mergedHabits.filter { it.isNew || !it.isSynced })
        //Удаляем недоудалённое
        deleteHabitsOnServer(habitDao.getHabitsMarkedForDeletion())
    }

    private fun mergeHabits(local: List<Habit>, remote: List<Habit>): List<Habit> {
        val habitMap = mutableMapOf<String, Habit>()

        //Удалённые не рассматриваем
        local.forEach { habit ->
            if (!habit.isDeleted) {
                habitMap[habit.serverId ?: habit.id] = habit
            }
        }

        remote.forEach { remoteHabit ->
            val localHabit = habitMap[remoteHabit.id]
            when {
                //Если с сервера прилетела привычка которой нет локально или синхронизированная,
                //то перезаписываем привычку с сервера
                localHabit == null || localHabit.isSynced -> {
                    habitMap[remoteHabit.id] = remoteHabit.copy(isSynced = true, isNew = false)
                }
                //Если локально привычка не синхронизирована, то локальное изменение побеждает
                else -> {
                    habitMap[remoteHabit.id] = localHabit.copy(isSynced = false, isNew = false)
                }
            }
        }

        return habitMap.values.toList()
    }

    private suspend fun putHabitsToServer(habits: List<Habit>) {
        //Отладочный тост
        Toast.makeText(
            context,
            "Обновление данных...",
            Toast.LENGTH_SHORT
        ).show()

        for (habit in habits) {
            Log.d("!!!", habit.toString())
            Log.d("!!!", habit.toNetworkModel().toString())

            var success = false
            var attempts = 0
            while (!success && attempts < MAX_API_RETRIES) {
                attempts++
                try {
                    val response = api.putHabit(token, habit.toNetworkModel())
                    //Если успешно заслали привычку на сервер
                    if (response.isSuccessful) {
                        val uid = response.body()?.uid
                        //Если привычка была новой локально
                        if (habit.isNew && uid != null) {
                            deleteHabit(habit.copy(isSynced = true)) //Удаляем привычку с локальным id
                            habitDao.insertHabit(
                                habit.copy(
                                    id = uid,
                                    isSynced = true,
                                    isNew = false
                                )
                            )//Добавляем её с id от сервера как не новую
                        } else if (!habit.isSynced) { //Если не новая, то просто помечаем как синхронизированную
                            habitDao.insertHabit(habit.copy(isSynced = true))
                        }
                        success = true
                    } else {
                        logApiError("PUT habit failed", response)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (!success) delay(RETRY_DELAY_MS)
            }
        }
    }

    private suspend fun deleteHabitsOnServer(habits: List<Habit>) {
        var c = 0
        for (habit in habits) {
            var success = false
            var attempts = 0
            c++
            while (!success && attempts < MAX_API_RETRIES) {
                attempts++
                Log.d("del", "$habits $c $attempts")
                if (habit.isNew) {
                    habitDao.deleteHabit(habit)
                    success = true
                    break
                }
                try {
                    val response = api.deleteHabit(token, HabitUID(habit.id))
                    if (response.isSuccessful) {
                        habitDao.deleteHabit(habit)
                        success = true
                    } else {
                        val errorMessage = response.errorBody()?.string()
                        val parsed = parseError(errorMessage)
                        //Если привычки с таким id уже нет на сервере, то локально тоже удалим
                        if (parsed.message.contains("No habit with id")) {
                            habitDao.deleteHabit(habit)
                            success = true
                        }
                        logApiError("DELETE habit failed", response)
                        logApiError("del", response)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (!success) delay(RETRY_DELAY_MS)
            }
        }
    }

    private fun logApiError(tag: String, response: Response<*>) {
        val errorMessage = response.errorBody()?.string()
        val parsed = parseError(errorMessage)
        Log.e(
            tag,
            "code=${response.code()}, message=${response.message()}, server=${parsed.message}"
        )
    }

    private fun parseError(errorBody: String?): ErrorResponse {
        val gson = Gson()
        return try {
            errorBody?.let {
                gson.fromJson(it, ErrorResponse::class.java)
            } ?: ErrorResponse(-1, "Unknown error")
        } catch (e: JsonSyntaxException) {
            ErrorResponse(-1, "Failed to parse error: ${e.message}")
        } catch (e: Exception) {
            ErrorResponse(-1, "An unexpected error occurred: ${e.message}")
        }
    }
}