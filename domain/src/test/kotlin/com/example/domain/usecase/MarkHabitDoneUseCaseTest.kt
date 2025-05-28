package com.example.domain.usecase

import com.example.domain.Habit
import com.example.domain.HabitRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class MarkHabitDoneUseCaseTest {

    private lateinit var repository: HabitRepository
    private lateinit var markHabitDoneUseCase: MarkHabitDoneUseCase

    @Before
    fun setup() {
        repository = mockk()
        markHabitDoneUseCase = MarkHabitDoneUseCase(repository)
    }

    @Test
    fun `markHabitDoneUseCase должен вызвать markHabitDone и onMessageDone`() = runTest {
        val testHabit = Habit(
            type = 0,
            count = 5,
            frequency = 7,
            done_dates = listOf()
        )
        coEvery { repository.markHabitDone(testHabit) } just Runs
        var messageReceived = ""

        markHabitDoneUseCase(testHabit) { message ->
            messageReceived = message
        }

        coVerify { repository.markHabitDone(testHabit) }
        assert(messageReceived.isNotEmpty())
    }

    @Test
    fun `generateToastMessage сообщение для хорошей привычки`() = runTest {
        val testHabit = Habit(
            type = 0,
            count = 5,
            frequency = 7,
            done_dates = listOf()
        )
        coEvery { repository.markHabitDone(testHabit) } just Runs
        var messageReceived = ""

        markHabitDoneUseCase(testHabit) { message ->
            messageReceived = message
        }

        assert(messageReceived == "Стоит выполнить ещё 4 раз")
    }

    @Test
    fun `generateToastMessage сообщение для плохой привычки`() = runTest {
        val testHabit = Habit(
            type = 1,
            count = 3,
            frequency = 7,
            done_dates = listOf()
        )
        coEvery { repository.markHabitDone(testHabit) } just Runs
        var messageReceived = ""

        markHabitDoneUseCase(testHabit) { message ->
            messageReceived = message
        }

        assert(messageReceived == "Можете выполнить ещё 2 раз")
    }

    @Test
    fun `generateToastMessage сообщение для перевыполненной хорошей привычки`() = runTest {
        val currentTime = (System.currentTimeMillis() / 1000).toInt()
        val testHabit = Habit(
            type = 0,
            count = 2,
            frequency = 7,
            done_dates = listOf(currentTime - 1000, currentTime - 2000)
        )
        coEvery { repository.markHabitDone(testHabit) } just Runs
        var messageReceived = ""

        markHabitDoneUseCase(testHabit) { message ->
            messageReceived = message
        }

        assert(messageReceived == "You are breathtaking!")
    }

    @Test
    fun `generateToastMessage сообщение для перевыполненной плохой привычки`() = runTest {

        val currentTime = (System.currentTimeMillis() / 1000).toInt()
        val testHabit = Habit(
            type = 1,
            count = 1,
            frequency = 7,
            done_dates = listOf(currentTime - 1000)
        )
        coEvery { repository.markHabitDone(testHabit) } just Runs
        var messageReceived = ""

        markHabitDoneUseCase(testHabit) { message ->
            messageReceived = message
        }
        assert(messageReceived == "Хватит это делать")
    }

    @Test
    fun `generateToastMessage сообщение Неизвестный тип привычки`() = runTest {
        val testHabit = Habit(type = 99)
        coEvery { repository.markHabitDone(testHabit) } just Runs
        var messageReceived = ""

        markHabitDoneUseCase(testHabit) { message ->
            messageReceived = message
        }

        assert(messageReceived == "Неизвестный тип привычки")
    }

    @Test
    fun `calculateCompletionStats подсчёт выполнений за указанный период`() = runTest {
        val currentTime = (System.currentTimeMillis() / 1000).toInt()
        val testHabit = Habit(
            count = 5,
            frequency = 1,
            done_dates = listOf(
                currentTime - 1000,
                currentTime - 2000,
                currentTime - 90000
            )
        )
        coEvery { repository.markHabitDone(testHabit) } just Runs
        var messageReceived = ""

        markHabitDoneUseCase(testHabit) { message ->
            messageReceived = message
        }

        assert(messageReceived.contains("2"))
    }

    @Test
    fun `calculateCompletionStats обработка frequency == null`() = runTest {
        val testHabit = Habit(
            type = 0,
            count = 5,
            frequency = null,
            done_dates = listOf(1, 2, 3)
        )
        coEvery { repository.markHabitDone(testHabit) } just Runs
        var messageReceived = ""

        markHabitDoneUseCase(testHabit) { message ->
            messageReceived = message
        }
        assert(messageReceived == "You are breathtaking!")
    }

    @Test
    fun `calculateCompletionStats обработка frequency == 0`() = runTest {
        val testHabit = Habit(
            type = 0,
            count = 5,
            frequency = 0,
            done_dates = listOf(1, 2, 3)
        )
        coEvery { repository.markHabitDone(testHabit) } just Runs
        var messageReceived = ""

        markHabitDoneUseCase(testHabit) { message ->
            messageReceived = message
        }

        assert(messageReceived == "You are breathtaking!")
    }
}