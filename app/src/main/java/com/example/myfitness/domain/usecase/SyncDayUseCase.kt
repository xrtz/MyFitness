package com.example.myfitness.domain.usecase

import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.repository.FoodRepository
import javax.inject.Inject

class SyncDayUseCase @Inject constructor(
    private val repository: FoodRepository
) {
    suspend fun execute(day: DayFoodItemModel): DayFoodItemModel {
        return repository.syncDayToServer(day) ?: day
    }
}
