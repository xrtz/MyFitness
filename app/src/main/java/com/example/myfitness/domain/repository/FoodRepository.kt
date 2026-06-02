package com.example.myfitness.domain.repository

import com.example.myfitness.domain.models.DateModel
import com.example.myfitness.domain.models.DayFoodItemModel

interface FoodRepository {
    fun getDayFoodItems(date: DateModel): DayFoodItemModel
    fun updateDayFoodItems(day: DayFoodItemModel)
    fun getPendingSyncDays(): List<DayFoodItemModel>
    suspend fun loadDayFromServer(epochDay: Int): DayFoodItemModel?
    suspend fun syncDayToServer(day: DayFoodItemModel): DayFoodItemModel?
}
