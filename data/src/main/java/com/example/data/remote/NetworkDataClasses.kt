package com.example.data.remote

import com.example.data.local.HabitEntity
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

fun HabitEntity.toNetworkModel(): NetworkHabit = NetworkHabit(
    color = this.color,
    count = this.count ?: 0,
    date = (this.date / 1000).toInt(),
    description = if (this.description == "") " " else this.description,
    done_dates = this.done_dates,
    frequency = this.frequency ?: 0,
    priority = this.priority,
    title = this.title,
    type = this.type,
    uid = if (this.isNew) null else this.id
)

fun NetworkHabit.toLocalModel(): HabitEntity = HabitEntity(
    id = this.uid ?: UUID.randomUUID().toString(),
    serverId = this.uid,
    title = this.title,
    description = this.description,
    done_dates = this.done_dates,
    priority = this.priority,
    type = this.type,
    frequency = this.frequency,
    count = this.count,
    color = this.color,
    date = this.date.toLong() * 1000
)