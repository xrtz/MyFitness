package com.example.myfitness.data.repository

import com.example.myfitness.data.datasource.remote.RemoteFoodDataSource
import com.example.myfitness.data.storage.Storage
import com.example.myfitness.domain.models.DateModel
import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.repository.FoodRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FoodRepositoryImpl @Inject constructor(
    private val storage: Storage,
    private val remoteDataSource: RemoteFoodDataSource
) : FoodRepository {

    override fun getDayFoodItems(date: DateModel): DayFoodItemModel =
        storage.getDayFoodItems(date)

    override fun updateDayFoodItems(day: DayFoodItemModel) =
        storage.updateDayFoodItems(day, isSynced = false)

    override fun getPendingSyncDays(): List<DayFoodItemModel> =
        storage.getUnsyncedDays()

    override suspend fun loadDayFromServer(epochDay: Int): DayFoodItemModel? {
        return try {
            val day = remoteDataSource.getDay(epochDay)
            day?.let {
                withContext(Dispatchers.IO) {
                    storage.updateDayFoodItems(
                        it,
                        isSynced = true
                    )
                }
            }
            day
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun syncDayToServer(day: DayFoodItemModel): DayFoodItemModel? {
        return try {
            val synced = remoteDataSource.saveDay(day)
            withContext(Dispatchers.IO) { storage.updateDayFoodItems(synced, isSynced = true) }
            synced
        } catch (_: Exception) {
            withContext(Dispatchers.IO) { storage.updateDayFoodItems(day, isSynced = false) }
            null
        }
    }
}
