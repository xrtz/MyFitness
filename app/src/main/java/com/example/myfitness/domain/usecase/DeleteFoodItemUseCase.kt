package com.example.myfitness.domain.usecase

import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.models.FoodModel
import com.example.myfitness.domain.repository.FoodRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteFoodItemUseCase @Inject constructor(
    private val repository: FoodRepository
) {
    suspend fun execute(food: FoodModel, day: DayFoodItemModel): DayFoodItemModel {
        val updatedBreakfast =
            if (food.typeOfMeal == "breakfast") day.breakfast.filter { it.id != food.id } else day.breakfast
        val updatedLunch =
            if (food.typeOfMeal == "lunch") day.lunch.filter { it.id != food.id } else day.lunch
        val updatedDinner =
            if (food.typeOfMeal == "dinner") day.dinner.filter { it.id != food.id } else day.dinner
        val updatedSnacks =
            if (food.typeOfMeal == "snacks") day.snacks.filter { it.id != food.id } else day.snacks

        val allRemaining = updatedBreakfast + updatedLunch + updatedDinner + updatedSnacks

        val updatedDay = day.copy(
            calories = allRemaining.sumOf { it.calories }.toFloat(),
            protein = allRemaining.sumOf { it.protein.toDouble() }.toFloat(),
            fats = allRemaining.sumOf { it.fats.toDouble() }.toFloat(),
            carbohydrates = allRemaining.sumOf { it.carbohydrates.toDouble() }.toFloat(),
            breakfast = updatedBreakfast,
            lunch = updatedLunch,
            dinner = updatedDinner,
            snacks = updatedSnacks
        )

        withContext(Dispatchers.IO) { repository.updateDayFoodItems(updatedDay) }
        return updatedDay
    }
}
