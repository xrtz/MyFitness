package com.example.myfitness.domain.usecase

import com.example.myfitness.data.repository.FoodRepositoryImpl
import com.example.myfitness.domain.models.DateModel
import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.models.FoodModel
import com.example.myfitness.domain.repository.FoodRepository

class GetDayFoodItemUseCase {
    fun execute(date: DateModel): DayFoodItemModel {
        val repo = FoodRepositoryImpl() // DI
//        return repo.getDayFoodItems(date)
        return DayFoodItemModel(1,
            2,
            2025,
            234f,
            123f,
            123f,
            43f,
            listOf(FoodModel(1, "", 2f, 3, "", 2f, 3f, 3f)),
            listOf(FoodModel(1, "", 2f, 3, "", 2f, 3f, 3f)),
            listOf(FoodModel(1, "", 2f, 3, "", 2f, 3f, 3f)),
            listOf(FoodModel(1, "", 2f, 3, "", 2f, 3f, 3f)),
            )
    }
}