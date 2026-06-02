package com.example.myfitness.domain.usecase

import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.models.FoodModel
import com.example.myfitness.domain.repository.FoodRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddFoodItemUseCase @Inject constructor(
    private val repository: FoodRepository
) {
    suspend fun execute(food: FoodModel, day: DayFoodItemModel): DayFoodItemModel {
        val updatedDay = day.copy(
            calories = day.calories + food.calories,
            protein = day.protein + food.protein,
            fats = day.fats + food.fats,
            carbohydrates = day.carbohydrates + food.carbohydrates,
            breakfast = if (food.typeOfMeal == "breakfast") day.breakfast + food else day.breakfast,
            lunch = if (food.typeOfMeal == "lunch") day.lunch + food else day.lunch,
            dinner = if (food.typeOfMeal == "dinner") day.dinner + food else day.dinner,
            snacks = if (food.typeOfMeal == "snacks") day.snacks + food else day.snacks
        )
        withContext(Dispatchers.IO) { repository.updateDayFoodItems(updatedDay) }
        return updatedDay
    }
}
