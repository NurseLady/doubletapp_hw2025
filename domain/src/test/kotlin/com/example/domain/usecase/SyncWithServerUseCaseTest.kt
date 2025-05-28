package com.example.domain.usecase

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
class SyncWithServerUseCaseTest {
    private lateinit var repository: HabitRepository
    private lateinit var syncWithServerUseCase: SyncWithServerUseCase

    @Before
    fun setup() {
        repository = mockk()
        syncWithServerUseCase = SyncWithServerUseCase(repository)
    }

    @Test
    fun `syncWithServerUseCase должен вызвать syncWithServer`() = runTest {
        coEvery { repository.syncWithServer() } just Runs

        syncWithServerUseCase()

        coVerify { repository.syncWithServer() }
    }
}