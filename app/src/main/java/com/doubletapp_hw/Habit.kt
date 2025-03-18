package com.doubletapp_hw

import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat.getString
import java.io.Serializable
import java.util.UUID

data class Habit(
    val id: String = UUID.randomUUID().toString(),
    var name: String = "Привычка",
    var description: String = "",
    var priority: Int = 0,
    var type: String = "Положительная",
    var frequency: String = "",
    var period: String = "",
    var color: Color = Color(127, 127,127, 255)
): Serializable
