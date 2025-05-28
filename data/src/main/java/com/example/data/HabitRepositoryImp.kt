package com.example.data

import android.util.Log
import com.example.data.local.HabitDao
import com.example.data.local.HabitEntity
import com.example.data.local.toEntity
import com.example.data.local.toHabit
import com.example.data.remote.ErrorResponse
import com.example.data.remote.HabitApiService
import com.example.data.remote.HabitDone
import com.example.data.remote.HabitUID
import com.example.data.remote.toLocalModel
import com.example.data.remote.toNetworkModel
import com.example.domain.Habit
import com.example.domain.HabitRepository
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import retrofit2.Response
import java.net.SocketTimeoutException
import kotlin.coroutines.coroutineContext

class HabitRepositoryImp(
    private val habitDao: HabitDao,
    private val api: HabitApiService,
    private val token: String
) : HabitRepository {

    private companion object {
        const val MAX_API_RETRIES = 5
        const val RETRY_DELAY_MS = 3000L
        const val TAG = "HabitSync"
    }

    private val habits: Flow<List<HabitEntity>> = habitDao.getAllHabits()
        .distinctUntilChanged { old, new ->
            old.map { it.id } == new.map { it.id }
        }

    // --- Интерфейс ---
    override fun getHabits(): Flow<List<Habit>> =
        habitDao.getAllHabits().map { it.map(HabitEntity::toHabit) }

    override suspend fun getHabit(id: String): Habit? =
        habitDao.getHabitById(id)?.toHabit()

    override suspend fun markHabitDone(habit: Habit) {
        val currentDate = (System.currentTimeMillis() / 1000).toInt()
        val updatedDoneDates = habit.done_dates + currentDate

        val updatedHabit = habit.copy(
            done_dates = updatedDoneDates,
            isSynced = false
        ).also { habitDao.insertHabit(it.toEntity()) }

        syncDoneMarksWithServer(updatedHabit, currentDate)
    }

    override suspend fun addHabit(habit: Habit) {
        val entity = habit.toEntity()
        entity.isSynced = false
        habitDao.insertHabit(entity)
        syncHabitsWithServer(listOf(entity))
    }

    override suspend fun deleteHabit(habit: Habit) {
        val entity = habit.toEntity()
        entity.isDeleted = true
        entity.isSynced = false
        habitDao.insertHabit(entity)
        syncDeletionsWithServer(listOf(entity))
    }

    override suspend fun syncWithServer() {
        performServerSync()
    }

    // --- Синхронизация ---
    private suspend fun performServerSync() {
        val serverHabits = fetchHabitsFromServerWithRetry() ?: run {
            Log.e(TAG, "Failed to fetch habits after $MAX_API_RETRIES attempts")
            return
        }

        val localHabits = habits.first()
        val merged = mergeLocalAndRemoteHabits(localHabits, serverHabits)

        habitDao.insertHabits(merged)

        val unsynced = merged.filter { it.isNew || !it.isSynced }
        if (unsynced.isNotEmpty()) syncHabitsWithServer(unsynced)

        val toDelete = habitDao.getHabitsMarkedForDeletion()
        if (toDelete.isNotEmpty()) syncDeletionsWithServer(toDelete)
    }

    private suspend fun fetchHabitsFromServerWithRetry(): List<HabitEntity>? {
        repeat(MAX_API_RETRIES) { attempt ->
            if (!coroutineContext.isActive) return@repeat
            try {
                val response = api.getHabits(token)
                if (response.isSuccessful) {
                    return response.body()?.map { it.toLocalModel() } ?: emptyList()
                } else {
                    logApiError("GET habits failed", response)
                }
            } catch (e: SocketTimeoutException) {
                Log.w(TAG, "Socket timeout in fetchHabits (attempt $attempt)", e)
            } catch (e: Exception) {
                Log.e(TAG, "Fetch habits attempt $attempt failed", e)
            }
            if (attempt < MAX_API_RETRIES - 1) delay(RETRY_DELAY_MS)
        }
        return null
    }

    private fun mergeLocalAndRemoteHabits(
        local: List<HabitEntity>,
        remote: List<HabitEntity>
    ): List<HabitEntity> {
        val map = mutableMapOf<String, HabitEntity>()
        local.filterNot { it.isDeleted }
            .forEach { map[it.serverId ?: it.id] = it }
        remote.forEach { remoteHabit ->
            val localHabit = map[remoteHabit.id]
            when {
                localHabit == null || localHabit.isSynced ->
                    map[remoteHabit.id] = remoteHabit.copy(isSynced = true, isNew = false)

                else ->
                    map[remoteHabit.id] = localHabit.copy(isSynced = false, isNew = false)
            }
        }
        return map.values.toList()
    }

    // --- Синхронизация отдельных изменений ---
    private suspend fun syncHabitsWithServer(habits: List<HabitEntity>) {
        habits.forEach { habit ->
            runWithRetry(
                operation = { attempt ->
                    try {
                        val response = api.putHabit(token, habit.toNetworkModel())
                        if (response.isSuccessful) {
                            onSuccessfulPut(habit, response.body()?.uid)
                            Result.success(Unit)
                        } else {
                            logApiError("PUT habit failed", response)
                            Result.failure(RuntimeException("PUT failed with code ${response.code()}"))
                        }
                    } catch (e: SocketTimeoutException) {
                        Result.failure(e)
                    } catch (e: Exception) {
                        Result.failure(e)
                    }
                },
                onFailure = { e ->
                    Log.e(TAG, "Failed to sync habit ${habit.id}", e)
                    if (e is SocketTimeoutException) {
                        Log.w(TAG, "Network timeout for habit ${habit.id}")
                    }
                }
            )
        }
    }


    private suspend fun onSuccessfulPut(habit: HabitEntity, uid: String?) {
        when {
            habit.isNew && uid != null -> habitDao.updateHabit(habit, uid)
            !habit.isSynced -> habitDao.insertHabit(habit.copy(isSynced = true))
        }
    }

    private suspend fun syncDeletionsWithServer(habits: List<HabitEntity>) {
        habits.forEach { habit ->
            if (habit.isNew) {
                habitDao.deleteHabit(habit)
                return@forEach
            }

            runWithRetry(
                operation = { attempt ->
                    try {
                        val response = api.deleteHabit(token, HabitUID(habit.id))
                        when {
                            response.isSuccessful || response.code() == 400 -> {
                                habitDao.deleteHabit(habit)
                                Result.success(Unit)
                            }

                            else -> {
                                val error = parseError(response.errorBody()?.string())
                                logApiError("DELETE failed", response, error)
                                Result.failure(RuntimeException("API error: ${error.message}"))
                            }
                        }
                    } catch (e: SocketTimeoutException) {
                        Result.failure(e)
                    } catch (e: Exception) {
                        Result.failure(e)
                    }
                },
                onFailure = { e ->
                    Log.e(TAG, "Failed to delete habit ${habit.serverId}", e)
                    if (e is SocketTimeoutException) {
                        Log.w(TAG, "Deletion timeout for ${habit.serverId}")
                    }
                }
            )
        }
    }

    private suspend fun syncDoneMarksWithServer(habit: Habit, date: Int) {
        runWithRetry(
            operation = { attempt ->
                try {
                    api.markHabitDone(
                        token,
                        HabitDone(
                            date = date,
                            habit_uid = habit.serverId ?: habit.id
                        )
                    ).let { response ->
                        if (response.isSuccessful) {
                            habitDao.insertHabit(habit.copy(isSynced = true).toEntity())
                            Result.success(Unit)
                        } else {
                            logApiError("POST habit_done failed", response)
                            Result.failure(
                                RuntimeException(
                                    "HTTP ${response.code()}: ${response.errorBody()?.string()}"
                                )
                            )
                        }
                    }
                } catch (e: SocketTimeoutException) {
                    Result.failure(e)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            },
            onFailure = { e ->
                Log.e(TAG, "Habit ${habit.id} sync failed after retries", e)
                if (e is SocketTimeoutException) {
                    Log.w(TAG, "Timeout marking habit ${habit.id} done")
                }
            }
        )
    }

    // --- Штуки для повторных попыток и логов ---
    private suspend fun <T> runWithRetry(
        maxRetries: Int = MAX_API_RETRIES,
        delayMs: Long = RETRY_DELAY_MS,
        operation: suspend (attempt: Int) -> Result<T>,
        onFailure: (Throwable) -> Unit = {}
    ): Result<T> {
        repeat(maxRetries) { attempt ->
            if (!coroutineContext.isActive) return@repeat
            val result = operation(attempt)
            if (result.isSuccess) return result
            if (attempt < maxRetries - 1) delay(delayMs)
        }
        return operation(maxRetries - 1).also {
            if (it.isFailure) onFailure(
                it.exceptionOrNull() ?: Exception("Unknown error")
            )
        }
    }

    private fun logApiError(tag: String, response: Response<*>, error: ErrorResponse? = null) {
        val errorMessage = error?.message ?: response.message()
        Log.e(
            TAG,
            "$tag: code=${response.code()}, message=$errorMessage"
        )
    }

    private fun parseError(errorBody: String?): ErrorResponse =
        try {
            errorBody?.let { Gson().fromJson(it, ErrorResponse::class.java) }
                ?: ErrorResponse(-1, "Unknown error")
        } catch (e: Exception) {
            ErrorResponse(-1, "Failed to parse error: ${e.message ?: "Unknown parsing error"}")
        }
}