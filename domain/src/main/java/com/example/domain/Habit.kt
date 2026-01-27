package com.example.domain

import java.util.UUID

data class Habit(
    val id: String = UUID.randomUUID().toString(),
    var serverId: String? = null,
    var title: String = "Привычка",
    var description: String = " ",
    var priority: Int = 0,
    var type: Int = 0,
    var count: Int? = null,
    var frequency: Int? = null,
    var done_dates: List<Int> = listOf(),
    var color: Int = 0,
    var date: Long = System.currentTimeMillis(),
    var isDeleted: Boolean = false,
    var isSynced: Boolean = false,
    var isNew: Boolean = true,
)
