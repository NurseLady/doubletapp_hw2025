package com.doubletapp_hw.apiUsage

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://droid-test-server.doubletapp.ru/api/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val habitApi: HabitApiService by lazy {
        retrofit.create(HabitApiService::class.java)
    }
}
