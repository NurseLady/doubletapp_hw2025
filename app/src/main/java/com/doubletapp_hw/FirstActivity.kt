package com.doubletapp_hw

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.doubletapp_hw.apiUsage.HabitApiService
import com.doubletapp_hw.apiUsage.RetrofitClient
import com.doubletapp_hw.db.AppDatabase
import com.doubletapp_hw.screens.Routes
import com.doubletapp_hw.screens.edit.HabitEditScreen
import com.doubletapp_hw.screens.home.HomeScreen
import com.doubletapp_hw.screens.info.InfoScreen
import com.doubletapp_hw.ui.theme.Dobletapp_hwTheme

val LocalNavController = compositionLocalOf<NavController> {
    error("No NavController found! Did you forget to provide NavController?")
}

class HabitApplication : Application() {
    lateinit var database: AppDatabase
        private set
    lateinit var habitRepository: HabitRepository
        private set
    lateinit var api: HabitApiService
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)
        api = RetrofitClient.habitApi
        habitRepository = HabitRepository(
            database.habitDao(),
            api = api,
            token = BuildConfig.API_TOKEN,
            context = applicationContext
        )
    }
}

class FirstActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            Dobletapp_hwTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppNav()
                }
            }
        }
    }
}

@Composable
fun AppNav() {
    val navController = rememberNavController()

    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(
            navController = navController,
            startDestination = Routes.Home
        ) {
            composable<Routes.Home> {
                HomeScreen()
            }
            composable<Routes.Info> {
                InfoScreen()
            }
            composable<Routes.HabitEdit> { backStackEntry ->
                val edit: Routes.HabitEdit = backStackEntry.toRoute()
                HabitEditScreen(edit.id)
            }
        }
    }
}
