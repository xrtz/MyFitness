package com.example.myfitness.usecase

import com.example.myfitness.MainDispatcherRule
import com.example.myfitness.domain.repository.FoodRepository
import com.example.myfitness.domain.usecase.SyncDayUseCase
import com.example.myfitness.emptyTestDay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class SyncDayUseCaseTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository: FoodRepository = mock()
    private val useCase = SyncDayUseCase(repository)

    @Test
    fun `возвращает серверный день при успешной синхронизации`() = runTest {
        val local = emptyTestDay()
        val fromServer = local.copy(calories = 999f)
        whenever(repository.syncDayToServer(local)).thenReturn(fromServer)

        val result = useCase.execute(local)

        assertEquals(fromServer, result)
    }

    @Test
    fun `возвращает оригинальный день если синхронизация не удалась`() = runTest {
        val day = emptyTestDay()
        whenever(repository.syncDayToServer(day)).thenReturn(null)

        val result = useCase.execute(day)

        assertEquals(day, result)
    }
}
