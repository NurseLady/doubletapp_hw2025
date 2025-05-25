package com.example.data

import android.content.Context
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.local.HabitDao
import com.example.data.remote.HabitApiService
import com.example.domain.HabitRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideHabitDao(db: AppDatabase): HabitDao = db.habitDao()

    @Provides
    @Singleton
    fun provideHabitDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "habit-db")
            .fallbackToDestructiveMigration(false)
            .build()

    @Provides
    @Singleton
    fun provideHabitApiService(): HabitApiService {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://droid-test-server.doubletapp.ru/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HabitApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideHabitRepository(
        dao: HabitDao,
        api: HabitApiService,
        @Named("auth_token") token: String
    ): HabitRepository = HabitRepositoryImp(dao, api, token)
}