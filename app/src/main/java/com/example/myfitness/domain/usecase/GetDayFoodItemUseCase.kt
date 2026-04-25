package com.example.myfitness.domain.usecase

import com.example.myfitness.domain.models.DateModel
import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.repository.FoodRepository
import javax.inject.Inject

class GetDayFoodItemUseCase @Inject constructor(
    private val repository: FoodRepository
) {
    fun execute(date: DateModel): DayFoodItemModel {
        return repository.getDayFoodItems(date)
    }
}