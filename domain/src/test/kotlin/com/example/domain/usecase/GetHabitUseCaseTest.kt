package com.example.domain.usecase

import com.example.domain.Habit
import com.example.domain.HabitRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class GetHabitUseCaseTest {
    private lateinit var repository: HabitRepository
    private lateinit var getHabitUseCase: GetHabitUseCase

    @Before
    fun setup() {
        repository = mockk()
        getHabitUseCase = GetHabitUseCase(repository)
    }

    @Test
    fun `getHabitUseCase по id должен вернуть привычку из репозитория`() = runTest {
        val testId = "test-id"
        val testHabit = Habit(id = testId)
        coEvery { repository.getHabit(testId) } returns testHabit

        val result = getHabitUseCase(testId)

        assert(result == testHabit)
        coVerify { repository.getHabit(testId) }
    }

    @Test
    fun `getHabitUseCase должен вернуть null по id, если репозиторий вернул null`() = runTest {
        val testId = "non-existent-id"
        coEvery { repository.getHabit(testId) } returns null

        val result = getHabitUseCase(testId)

        assert(result == null)
        coVerify { repository.getHabit(testId) }
    }

    @Test
    fun `getHabitUseCase без аргументов должен вернуть flow из репозитория`() = runTest {
        val testHabits = listOf(Habit(), Habit(), Habit())
        every { repository.getHabits() } returns flowOf(testHabits)

        val resultFlow = getHabitUseCase()

        resultFlow.collect { result ->
            assert(result == testHabits)
        }
        verify { repository.getHabits() }
    }
}