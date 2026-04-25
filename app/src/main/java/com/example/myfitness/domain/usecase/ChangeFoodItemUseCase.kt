package com.example.myfitness.domain.usecase

import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.models.FoodModel
import com.example.myfitness.domain.models.RepositoryResult
import com.example.myfitness.domain.repository.FoodRepository
import javax.inject.Inject

class ChangeFoodItemUseCase @Inject constructor(
    private val repository: FoodRepository
) {
    fun execute(food: FoodModel, day: DayFoodItemModel): RepositoryResult {
        val foodOld = when (food.typeOfMeal) {
            "breakfast" -> day.breakfast.find { it.id == food.id }
            "lunch" -> day.lunch.find { it.id == food.id }
            "dinner" -> day.dinner.find { it.id == food.id }
            "snacks" -> day.snacks.find { it.id == food.id }
            else -> null
        }
        if (foodOld == null) {
            return RepositoryResult.Error(Exception("Food item with id ${food.id} not found in ${food.typeOfMeal}"))
        }

        val updatedBreakfast = if (food.typeOfMeal == "breakfast") updateList(day.breakfast, foodOld, food) else day.breakfast
        val updatedLunch = if (food.typeOfMeal == "lunch") updateList(day.lunch, foodOld, food) else day.lunch
        val updatedDinner = if (food.typeOfMeal == "dinner") updateList(day.dinner, foodOld, food) else day.dinner
        val updatedSnacks = if (food.typeOfMeal == "snacks") updateList(day.snacks, foodOld, food) else day.snacks

        val dayCopy = day.copy(
            calories = day.calories + food.calories - foodOld.calories,
            protein = day.protein + food.protein - foodOld.protein,
            fats = day.fats + food.fats - foodOld.fats,
            carbohydrates = day.carbohydrates + food.carbohydrates - foodOld.carbohydrates,
            breakfast = updatedBreakfast,
            lunch = updatedLunch,
            dinner = updatedDinner,
            snacks = updatedSnacks
        )

        return repository.updateDayFoodItems(dayCopy)
    }

    private fun updateList(
        list: List<FoodModel>,
        oldItem: FoodModel,
        newItem: FoodModel
    ): List<FoodModel> {
        val index = list.indexOfFirst { it.id == oldItem.id }
        return if (index != -1) {
            list.toMutableList().apply { this[index] = newItem }
        } else {
            list
        }
    }
}