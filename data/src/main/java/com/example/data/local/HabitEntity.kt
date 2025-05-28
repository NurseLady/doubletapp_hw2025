package com.example.data.local

import android.graphics.Color.valueOf
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.domain.Habit
import com.google.gson.Gson
import java.io.Serializable
import java.util.UUID

@Entity(tableName = "habits")
@TypeConverters(Converters::class)
data class HabitEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    var serverId: String? = null,
    var title: String = "Привычка",
    var description: String = " ",
    var priority: Int = 0,
    var type: Int = 0,
    var frequency: Int? = null,
    var done_dates: List<Int> = listOf(),
    var count: Int? = null,
    var color: Int = valueOf(127F, 127F, 127F, 255F).toArgb(),
    var date: Long = System.currentTimeMillis(),
    var isDeleted: Boolean = false,
    var isSynced: Boolean = false,
    var isNew: Boolean = true,
) : Serializable

fun HabitEntity.toHabit(): Habit = Habit(
    id = this.id,
    serverId = this.serverId,
    title = this.title,
    description = this.description,
    priority = this.priority,
    type = this.type,
    frequency = this.frequency,
    done_dates = this.done_dates,
    count = this.count,
    color = this.color,
    date = this.date,
    isDeleted = this.isDeleted,
    isSynced = this.isSynced,
    isNew = this.isNew
)

fun Habit.toEntity(): HabitEntity = HabitEntity(
    id = this.id,
    serverId = this.serverId,
    title = this.title,
    description = this.description,
    priority = this.priority,
    type = this.type,
    frequency = this.frequency,
    done_dates = done_dates,
    count = this.count,
    color = this.color,
    date = this.date,
    isDeleted = this.isDeleted,
    isSynced = this.isSynced,
    isNew = this.isNew
)

class Converters {
    @TypeConverter
    fun fromListInt(value: List<Int>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toListInt(value: String): List<Int> {
        return try {
            Gson().fromJson(value, Array<Int>::class.java).toList()
        } catch (e: Exception) {
            listOf()
        }
    }
}