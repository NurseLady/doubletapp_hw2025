package com.doubletapp_hw

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID


@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    var name: String = "Привычка",
    var description: String = "",
    var priority: HabitPriority = HabitPriority.LOW,
    var type: HabitType = HabitType.POSITIVE,
    var frequency: String = "",
    var period: String = "",
    var color: Int = Color(127, 127, 127, 255).toArgb(),
    var lastEdited: LocalDateTime = LocalDateTime.now()
): Serializable