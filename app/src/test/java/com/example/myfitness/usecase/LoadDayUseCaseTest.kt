package com.example.myfitness.usecase

import com.example.myfitness.MainDispatcherRule
import com.example.myfitness.domain.models.DateModel
import com.example.myfitness.domain.repository.FoodRepository
import com.example.myfitness.domain.usecase.LoadDayUseCase
import com.example.myfitness.emptyTestDay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class LoadDayUseCaseTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository: FoodRepository = mock()
    private val useCase = LoadDayUseCase(repository)

    @Test
    fun `сначала эмитит локальные данные затем серверные`() = runTest {
        val epochDay = 19900
        val localDay = emptyTestDay(date = epochDay)
        val remoteDay = localDay.copy(calories = 500f)

        whenever(repository.getDayFoodItems(DateModel(epochDay))).thenReturn(localDay)
        whenever(repository.loadDayFromServer(epochDay)).thenReturn(remoteDay)

        val emissions = useCase.execute(epochDay).toList()

        assertEquals(2, emissions.size)
        assertEquals(localDay, emissions[0])
        assertEquals(remoteDay, emissions[1])
    }

    @Test
    fun `эмитит только локальные данные если сервер недоступен`() = runTest {
        val epochDay = 19900
        val localDay = emptyTestDay(date = epochDay)

        whenever(repository.getDayFoodItems(DateModel(epochDay))).thenReturn(localDay)
        whenever(repository.loadDayFromServer(epochDay)).thenReturn(null)

        val emissions = useCase.execute(epochDay).toList()

        assertEquals(1, emissions.size)
        assertEquals(localDay, emissions[0])
    }

    @Test
    fun `локальные данные всегда эмитятся первыми без ожидания сети`() = runTest {
        val epochDay = 19900
        val localDay = emptyTestDay(date = epochDay)
        val results = mutableListOf<Any>()

        whenever(repository.getDayFoodItems(DateModel(epochDay))).thenReturn(localDay)
        whenever(repository.loadDayFromServer(epochDay)).thenAnswer {
            results.add("server_called")
            null
        }

        useCase.execute(epochDay).collect { day ->
            results.add(day)
        }

        assertEquals(localDay, results[0])
        assertEquals("server_called", results[1])
    }
}
