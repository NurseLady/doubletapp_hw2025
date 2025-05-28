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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.doubletapp_hw.screens.Routes
import com.doubletapp_hw.screens.edit.HabitEditScreen
import com.doubletapp_hw.screens.home.HomeScreen
import com.doubletapp_hw.screens.info.InfoScreen
import com.doubletapp_hw.ui.theme.Dobletapp_hwTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

val LocalNavController = compositionLocalOf<NavController> {
    error("No NavController found! Did you forget to provide NavController?")
}

@HiltAndroidApp
class HabitApplication : Application()

@AndroidEntryPoint
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
            composable(
                route = "habit_edit/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                HabitEditScreen()
            }
        }
    }
}
