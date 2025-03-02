package com.doubletapp_hw

import androidx.compose.ui.graphics.Color
import java.io.Serializable
import java.util.UUID

data class Habit(
    val id: String = UUID.randomUUID().toString(),
    var name: String = "NewHabit",
    var description: String = "",
    var priority: Int = 0,
    var type: String = "",
    var frequency: String = "",
    var period: String = "",
    var color: Color = Color(127, 127,127, 255)
): Serializable
