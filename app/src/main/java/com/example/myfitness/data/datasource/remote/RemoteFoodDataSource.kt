package com.example.myfitness.data.datasource.remote

import com.example.myfitness.domain.models.DayFoodItemModel

interface RemoteFoodDataSource {
    suspend fun getDay(epochDay: Int): DayFoodItemModel?
    suspend fun saveDay(day: DayFoodItemModel): DayFoodItemModel
}
