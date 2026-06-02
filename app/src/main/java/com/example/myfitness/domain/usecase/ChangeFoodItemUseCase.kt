package com.example.myfitness.domain.usecase

import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.models.FoodModel
import com.example.myfitness.domain.repository.FoodRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChangeFoodItemUseCase @Inject constructor(
    private val repository: FoodRepository
) {
    suspend fun execute(food: FoodModel, day: DayFoodItemModel): DayFoodItemModel {
        val foodOld = when (food.typeOfMeal) {
            "breakfast" -> day.breakfast.find { it.id == food.id }
            "lunch" -> day.lunch.find { it.id == food.id }
            "dinner" -> day.dinner.find { it.id == food.id }
            "snacks" -> day.snacks.find { it.id == food.id }
            else -> null
        } ?: throw IllegalArgumentException("Food item ${food.id} not found in ${food.typeOfMeal}")

        val updatedDay = day.copy(
            calories = day.calories + food.calories - foodOld.calories,
            protein = day.protein + food.protein - foodOld.protein,
            fats = day.fats + food.fats - foodOld.fats,
            carbohydrates = day.carbohydrates + food.carbohydrates - foodOld.carbohydrates,
            breakfast = if (food.typeOfMeal == "breakfast") replaceIn(
                day.breakfast,
                foodOld,
                food
            ) else day.breakfast,
            lunch = if (food.typeOfMeal == "lunch") replaceIn(
                day.lunch,
                foodOld,
                food
            ) else day.lunch,
            dinner = if (food.typeOfMeal == "dinner") replaceIn(
                day.dinner,
                foodOld,
                food
            ) else day.dinner,
            snacks = if (food.typeOfMeal == "snacks") replaceIn(
                day.snacks,
                foodOld,
                food
            ) else day.snacks
        )

        withContext(Dispatchers.IO) { repository.updateDayFoodItems(updatedDay) }
        return updatedDay
    }

    private fun replaceIn(list: List<FoodModel>, old: FoodModel, new: FoodModel): List<FoodModel> {
        val index = list.indexOfFirst { it.id == old.id }
        return if (index != -1) list.toMutableList().apply { this[index] = new } else list
    }
}
