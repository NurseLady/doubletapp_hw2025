package com.doubletapp_hw

import android.content.Context
import android.os.Handler
import android.os.Looper
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
import kotlinx.coroutines.delay
import retrofit2.Response

class HabitRepository(
    private val habitDao: HabitDao,
    private val api: HabitApiService,
    private val token: String,
    private val context: Context, //Только для тоста
) {
    private companion object {
        const val MAX_API_RETRIES = 5
        const val RETRY_DELAY_MS = 5000L
        const val TAG = "HabitSync"
    }

    val habits: LiveData<List<Habit>> = habitDao.getAllHabits()

    suspend fun getHabitById(id: String): Habit? {
        return habitDao.getHabitById(id)
    }

    suspend fun saveHabit(habit: Habit) {
        habit.isSynced = false
        habitDao.insertHabit(habit)
        putHabitsToServer(listOf(habit))
    }

    suspend fun deleteHabit(habit: Habit) {
        habit.isDeleted = true
        habit.isSynced = false
        habitDao.insertHabit(habit)
        deleteHabitsOnServer(listOf(habit))
    }


    suspend fun syncWithServer() {
        // Получаем привычки с сервера с повторными попытками
        val serverHabits = fetchServerHabitsWithRetry() ?: run {
            Log.e(TAG, "Failed to fetch habits after $MAX_API_RETRIES attempts")
            return
        }

        // Синхронизируем с локальными данными
        val localHabits = habits.value.orEmpty()
        val mergedHabits = mergeHabits(localHabits, serverHabits)

        // Обновляем локальную базу данных
        habitDao.insertHabits(mergedHabits)

        // Синхронизируем изменения с сервером
        val unsyncedHabits = mergedHabits.filter { it.isNew || !it.isSynced }
        if (unsyncedHabits.isNotEmpty()) {
            putHabitsToServer(unsyncedHabits)
        }

        // Очищаем удаленные привычки
        val habitsToDelete = habitDao.getHabitsMarkedForDeletion()
        if (habitsToDelete.isNotEmpty()) {
            deleteHabitsOnServer(habitsToDelete)
        }
    }

    private suspend fun fetchServerHabitsWithRetry(): List<Habit>? {
        repeat(MAX_API_RETRIES) { attempt ->
            try {
                val response = api.getHabits(token)
                when {
                    response.isSuccessful -> {
                        return response.body()?.map { it.toLocalModel() }.orEmpty()
                    }

                    else -> {
                        logApiError("GET habits failed", response)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Fetch habits attempt $attempt failed", e)
            }

            if (attempt < MAX_API_RETRIES - 1) {
                delay(RETRY_DELAY_MS)
            }
        }
        return null
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
        showToast(context, "Обновление данных...")

        habits.forEach { habit ->
            Log.v(TAG, "Syncing habit: $habit")
            Log.v(TAG, "Network model: ${habit.toNetworkModel()}")

            runWithRetry(
                operation = { attempt ->
                    val response = api.putHabit(token, habit.toNetworkModel())

                    if (response.isSuccessful) {
                        handleSuccessfulPut(habit, response.body()?.uid)
                        Result.success(Unit)
                    } else {
                        logApiError("PUT habit failed", response)
                        Result.failure(RuntimeException("PUT failed with code ${response.code()}"))
                    }
                },
                onFailure = { e -> Log.e(TAG, "Failed to sync habit ${habit.id}", e) }
            )
        }
    }

    private suspend fun handleSuccessfulPut(habit: Habit, uid: String?) {
        when {
            habit.isNew && uid != null -> {
                // Обновляем привычку с серверным ID
                habitDao.updateHabit(habit, uid)
            }

            !habit.isSynced -> {
                // Просто помечаем как синхронизированную
                habitDao.insertHabit(habit.copy(isSynced = true))
            }
        }
    }


    private suspend fun deleteHabitsOnServer(habits: List<Habit>) {
        habits.forEach { habit ->
            if (habit.isNew) {
                habitDao.deleteHabit(habit)
                return@forEach
            }

            runWithRetry(
                operation = { attempt ->
                    val response = api.deleteHabit(token, HabitUID(habit.id))

                    when {
                        response.isSuccessful -> {
                            habitDao.deleteHabit(habit)
                            Result.success(Unit)
                        }

                        response.code() == 404 -> {
                            // Привычка уже удалена на сервере
                            habitDao.deleteHabit(habit)
                            Result.success(Unit)
                        }

                        else -> {
                            val error = parseError(response.errorBody()?.string())
                            logApiError("DELETE failed", response, error)
                            Result.failure(RuntimeException("API error: ${error.message}"))
                        }
                    }
                },
                onFailure = { e -> Log.e(TAG, "Failed to delete habit ${habit.id}", e) }
            )
        }
    }

    private suspend fun <T> runWithRetry(
        maxRetries: Int = MAX_API_RETRIES,
        delayMs: Long = RETRY_DELAY_MS,
        operation: suspend (attempt: Int) -> Result<T>,
        onFailure: (Throwable) -> Unit = {}
    ): Result<T> {
        repeat(maxRetries) { attempt ->
            val result = operation(attempt)
            if (result.isSuccess) return result

            if (attempt < maxRetries - 1) delay(delayMs)
        }

        return operation(maxRetries - 1).also {
            if (it.isFailure) onFailure(it.exceptionOrNull() ?: Exception("Unknown error"))
        }
    }

    private fun showToast(context: Context, message: String) {
        with(Toast.makeText(context, message, Toast.LENGTH_SHORT)) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                show()
            } else {
                Handler(Looper.getMainLooper()).post { show() }
            }
        }
    }

    private fun logApiError(tag: String, response: Response<*>, error: ErrorResponse? = null) {
        val errorMessage = error?.message ?: response.message()
        Log.e(
            TAG,
            "$tag: code=${response.code()}, message=$errorMessage"
        )
    }

    private fun parseError(errorBody: String?): ErrorResponse {
        return try {
            errorBody?.let { Gson().fromJson(it, ErrorResponse::class.java) }
                ?: ErrorResponse(-1, "Unknown error")
        } catch (e: Exception) {
            ErrorResponse(-1, "Failed to parse error: ${e.message ?: "Unknown parsing error"}")
        }
    }
}