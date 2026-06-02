package com.example.myfitness.data.storage

import com.example.myfitness.domain.models.DateModel
import com.example.myfitness.domain.models.DayFoodItemModel

interface Storage {
    fun getDayFoodItems(date: DateModel): DayFoodItemModel
    fun updateDayFoodItems(day: DayFoodItemModel, isSynced: Boolean = false)
    fun getUnsyncedDays(): List<DayFoodItemModel>
}
