package com.example.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface HabitApiService {
    @GET("habit")
    suspend fun getHabits(@Header("Authorization") token: String): Response<List<NetworkHabit>>

    @PUT("habit")
    suspend fun putHabit(
        @Header("Authorization") token: String,
        @Body habit: NetworkHabit
    ): Response<HabitUID>

    @HTTP(method = "DELETE", path = "habit", hasBody = true)
    suspend fun deleteHabit(
        @Header("Authorization") token: String,
        @Body habitUID: HabitUID
    ): Response<Unit>

    @POST("habit_done")
    suspend fun markHabitDone(
        @Header("Authorization") token: String,
        @Body habitDone: HabitDone
    ): Response<Unit>
}