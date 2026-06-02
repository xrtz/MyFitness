package com.example.myfitness.repository

import com.example.myfitness.MainDispatcherRule
import com.example.myfitness.data.datasource.remote.RemoteFoodDataSource
import com.example.myfitness.data.repository.FoodRepositoryImpl
import com.example.myfitness.data.storage.Storage
import com.example.myfitness.domain.models.DateModel
import com.example.myfitness.emptyTestDay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class FoodRepositoryImplTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val storage: Storage = mock()
    private val remote: RemoteFoodDataSource = mock()
    private val repository = FoodRepositoryImpl(storage, remote)

    @Test
    fun `getDayFoodItems делегирует в storage`() {
        val date = DateModel(100)
        val expected = emptyTestDay()
        whenever(storage.getDayFoodItems(date)).thenReturn(expected)

        val result = repository.getDayFoodItems(date)

        assertEquals(expected, result)
        verify(storage).getDayFoodItems(date)
    }

    @Test
    fun `updateDayFoodItems сохраняет с isSynced=false`() {
        val day = emptyTestDay()

        repository.updateDayFoodItems(day)

        verify(storage).updateDayFoodItems(day, false)
    }

    @Test
    fun `syncDayToServer возвращает серверный день и сохраняет с isSynced=true`() = runTest {
        val day = emptyTestDay()
        val serverDay = day.copy(calories = 700f)
        whenever(remote.saveDay(day)).thenReturn(serverDay)

        val result = repository.syncDayToServer(day)

        assertEquals(serverDay, result)
        verify(storage).updateDayFoodItems(serverDay, true)
    }

    @Test
    fun `syncDayToServer возвращает null и сохраняет с isSynced=false при ошибке сети`() = runTest {
        val day = emptyTestDay()
        whenever(remote.saveDay(any())).thenThrow(RuntimeException("Нет сети"))

        val result = repository.syncDayToServer(day)

        assertNull(result)
        verify(storage).updateDayFoodItems(day, false)
    }

    @Test
    fun `loadDayFromServer сохраняет с isSynced=true при успехе`() = runTest {
        val day = emptyTestDay()
        whenever(remote.getDay(100)).thenReturn(day)

        val result = repository.loadDayFromServer(100)

        assertEquals(day, result)
        verify(storage).updateDayFoodItems(day, true)
    }

    @Test
    fun `loadDayFromServer возвращает null при ошибке`() = runTest {
        whenever(remote.getDay(any())).thenThrow(RuntimeException())

        val result = repository.loadDayFromServer(100)

        assertNull(result)
    }

    @Test
    fun `getPendingSyncDays возвращает несинхронизированные дни из storage`() {
        val unsynced = listOf(emptyTestDay())
        whenever(storage.getUnsyncedDays()).thenReturn(unsynced)

        val result = repository.getPendingSyncDays()

        assertEquals(unsynced, result)
    }
}
