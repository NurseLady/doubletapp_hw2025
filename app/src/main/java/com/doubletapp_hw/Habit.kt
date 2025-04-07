package com.doubletapp_hw

import androidx.compose.ui.graphics.Color
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

data class Habit(
    val id: String = UUID.randomUUID().toString(),
    var name: String = "Привычка",
    var description: String = "",
    var priority: HabitPriority = HabitPriority.LOW,
    var type: HabitType = HabitType.POSITIVE,
    var frequency: String = "",
    var period: String = "",
    var color: Color = Color(127, 127,127, 255),
    var lastEdited: LocalDateTime = LocalDateTime.now()
): Serializable
