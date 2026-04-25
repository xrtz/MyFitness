package com.example.myfitness.domain.usecase

import android.util.Log
import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.models.FoodModel
import com.example.myfitness.domain.models.RepositoryResult
import com.example.myfitness.domain.repository.FoodRepository
import javax.inject.Inject

class AddFoodItemUseCase @Inject constructor(
    private val repository: FoodRepository
) {
    fun execute(food: FoodModel, day: DayFoodItemModel): RepositoryResult {
        Log.d("ADD_FOOD", "=== AddFoodItemUseCase ===")
        Log.d("ADD_FOOD", "food.name=${food.name}, food.typeOfMeal=${food.typeOfMeal}")
        Log.d("ADD_FOOD", "day.id=${day.id}, day.date=${day.date}, day.calories=${day.calories}")

        val dayCopy = day.copy(
            calories      = day.calories + food.calories,
            protein       = day.protein + food.protein,
            fats          = day.fats + food.fats,
            carbohydrates = day.carbohydrates + food.carbohydrates,
            breakfast     = if (food.typeOfMeal == "breakfast") day.breakfast + food else day.breakfast,
            lunch         = if (food.typeOfMeal == "lunch")     day.lunch     + food else day.lunch,
            dinner        = if (food.typeOfMeal == "dinner")    day.dinner    + food else day.dinner,
            snacks        = if (food.typeOfMeal == "snacks")    day.snacks    + food else day.snacks
        )

        Log.d("ADD_FOOD", "dayCopy.id=${dayCopy.id}, dayCopy.date=${dayCopy.date}")
        Log.d("ADD_FOOD", "dayCopy.breakfast.size=${dayCopy.breakfast.size}")

        val result = repository.updateDayFoodItems(dayCopy)
        Log.d("ADD_FOOD", "result=$result")
        return result
    }
}