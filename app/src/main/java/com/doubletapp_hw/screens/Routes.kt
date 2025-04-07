package com.doubletapp_hw.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector
import com.doubletapp_hw.R
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class Routes(
    @Transient val icon: ImageVector = Icons.Default.Home,
    val title: Int
) {
    @Serializable
    data object Home : Routes(Icons.Default.Home, R.string.home)

    @Serializable
    data object Info : Routes(Icons.Default.Info, R.string.info)

    @Serializable
    data class HabitEdit(val id: String) : Routes(Icons.Default.Edit, R.string.edit_habit)
}
