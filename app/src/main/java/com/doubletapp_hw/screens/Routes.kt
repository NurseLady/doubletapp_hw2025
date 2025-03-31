package com.doubletapp_hw.screens

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Edit
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import com.doubletapp_hw.R

@Serializable
sealed class Routes(
    @Transient val icon: ImageVector = Icons.Default.Home,
    val title: Int
) {
    @Serializable
    object Home : Routes(Icons.Default.Home, R.string.home)

    @Serializable
    object Info : Routes(Icons.Default.Info, R.string.info)

    @Serializable
    data class HabitEdit(val id: String) : Routes(Icons.Default.Edit, R.string.edit_habit)
}
