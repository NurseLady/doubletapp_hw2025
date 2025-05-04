package com.doubletapp_hw.apiUsage

import com.doubletapp_hw.Habit
import com.doubletapp_hw.enums.HabitPriority
import com.doubletapp_hw.enums.HabitType
import java.util.UUID

data class HabitUID(val uid: String)
data class HabitDone(val date: Int, val habit_uid: String)
data class ErrorResponse(val code: Int, val message: String)

data class NetworkHabit(
    val color: Int,
    val count: Int,
    val date: Int,
    val description: String,
    val done_dates: List<Int>,
    val frequency: Int,
    val priority: Int,
    val title: String,
    val type: Int,
    val uid: String?
)

fun Habit.toNetworkModel(): NetworkHabit = NetworkHabit(
    color = this.color,
    count = 0,
    date = (this.date / 1000).toInt(),
    description = if (this.description == "") " " else this.description,
    done_dates = listOf(),
    frequency = this.frequency ?: 0,
    priority = this.priority.ordinal,
    title = this.title,
    type = this.type.ordinal,
    uid = if (this.isNew) null else this.id
)

fun NetworkHabit.toLocalModel(): Habit = Habit(
    id = this.uid ?: UUID.randomUUID().toString(),
    serverId = this.uid,
    title = this.title,
    description = this.description,
    priority = HabitPriority.entries[this.priority],
    type = HabitType.entries[this.type],
    frequency = this.frequency,
    period = "",
    color = this.color,
    date = this.date.toLong() * 1000
)