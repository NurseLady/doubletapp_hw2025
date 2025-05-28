package com.example.domain.usecase

import com.example.domain.Habit
import com.example.domain.HabitRepository
import javax.inject.Inject

class MarkHabitDoneUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit, onMessageDone: (String) -> Unit) {
        onMessageDone(generateToastMessage(habit))
        repository.markHabitDone(habit)
    }

    private fun generateToastMessage(habit: Habit): String {
        val (currentCount, targetCount) = calculateCompletionStats(habit)

        return when (habit.type) {
            1 -> {
                if (currentCount < targetCount) "Можете выполнить ещё ${targetCount - currentCount} раз"
                else "Хватит это делать"
            }

            0 -> {
                if (currentCount < targetCount) "Стоит выполнить ещё ${targetCount - currentCount} раз"
                else "You are breathtaking!"
            }

            else -> "Неизвестный тип привычки"
        }
    }

    private fun calculateCompletionStats(habit: Habit): Pair<Int, Int> {
        val frequencyDays = habit.frequency ?: return 0 to 0
        if (frequencyDays <= 0) return 0 to 0

        val periodSeconds = frequencyDays * 86400
        val currentTimeSec = (System.currentTimeMillis() / 1000).toInt()
        val periodStart = currentTimeSec - periodSeconds

        val currentCount = habit.done_dates.count { it in periodStart..currentTimeSec } + 1
        val targetCount = habit.count ?: 0

        return currentCount.coerceAtLeast(0) to targetCount.coerceAtLeast(0)
    }
}