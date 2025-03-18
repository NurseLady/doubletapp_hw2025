package com.doubletapp_hw.screens

sealed class Routes (val route: String){
    data object Home: Routes("home")
    data object Info: Routes("info")
    data object HabitEdit: Routes("edit")
}