package com.example.myfitness.domain.usecase

import com.example.myfitness.data.repository.FoodRepositoryImpl
import com.example.myfitness.domain.models.DateModel
import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.models.FoodModel
import com.example.myfitness.domain.models.RepositoryResult

class DeleteFoodItemUseCase {
    fun execute(food: FoodModel, day: DayFoodItemModel): RepositoryResult {
        val repo = FoodRepositoryImpl() // DI

        val updatedBreakfast = when (food.typeOfMeal) {
            "breakfast" -> updateList(day.breakfast, food)
            else -> day.breakfast
        }

        val updatedLunch = when (food.typeOfMeal) {
            "lunch" -> updateList(day.lunch, food)
            else -> day.lunch
        }

        val updatedDinner = when (food.typeOfMeal) {
            "dinner" -> updateList(day.dinner, food)
            else -> day.dinner
        }

        val updatedSnacks = when (food.typeOfMeal) {
            "snacks" -> updateList(day.snacks, food)
            else -> day.snacks
        }

        val dayCopy = day.copy(
            calories = day.calories - food.calories,
            protein = day.protein - food.protein,
            fats = day.fats - food.fats,
            carbohydrates = day.carbohydrates - food.carbohydrates,
            breakfast = updatedBreakfast,
            lunch = updatedLunch,
            dinner = updatedDinner,
            snacks = updatedSnacks
        )

        return repo.updateDayFoodItems(dayCopy)
    }

    private fun updateList(list: List<FoodModel>, oldItem: FoodModel): List<FoodModel> {
        val index = list.indexOfFirst { it.id == oldItem.id }
        return if (index != -1) {
            list.toMutableList().apply { this.minusElement(oldItem)}
        } else list
    }
}