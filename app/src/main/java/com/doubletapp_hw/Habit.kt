package com.doubletapp_hw

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.doubletapp_hw.enums.HabitPriority
import com.doubletapp_hw.enums.HabitType
import java.io.Serializable
import java.util.UUID


@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    var serverId: String? = null,
    var title: String = "Привычка",
    var description: String = " ",
    var priority: HabitPriority = HabitPriority.LOW,
    var type: HabitType = HabitType.POSITIVE,
    var frequency: Int? = null,
    var period: String = "",
    var color: Int = Color(127, 127, 127, 255).toArgb(),
    var date: Long = System.currentTimeMillis(),
    var isDeleted: Boolean = false,
    var isSynced: Boolean = false,
    var isNew: Boolean = true,
) : Serializable