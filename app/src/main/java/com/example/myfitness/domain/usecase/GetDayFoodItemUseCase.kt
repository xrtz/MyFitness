package com.example.myfitness.domain.usecase

import com.example.myfitness.domain.models.DateModel
import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.repository.FoodRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetDayFoodItemUseCase @Inject constructor(
    private val repository: FoodRepository
) {
    suspend fun execute(date: DateModel): DayFoodItemModel {
        return withContext(Dispatchers.IO) { repository.getDayFoodItems(date) }
    }
}
