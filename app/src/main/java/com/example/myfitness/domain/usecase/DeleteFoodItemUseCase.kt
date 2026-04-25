package com.example.myfitness.domain.usecase

import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.models.FoodModel
import com.example.myfitness.domain.models.RepositoryResult
import com.example.myfitness.domain.repository.FoodRepository
import javax.inject.Inject

class DeleteFoodItemUseCase @Inject constructor(
    private val repository: FoodRepository
) {
    fun execute(food: FoodModel, day: DayFoodItemModel): RepositoryResult {
        // filter — правильный способ удалить элемент из списка
        val updatedBreakfast = if (food.typeOfMeal == "breakfast") day.breakfast.filter { it.id != food.id } else day.breakfast
        val updatedLunch     = if (food.typeOfMeal == "lunch")     day.lunch.filter     { it.id != food.id } else day.lunch
        val updatedDinner    = if (food.typeOfMeal == "dinner")    day.dinner.filter    { it.id != food.id } else day.dinner
        val updatedSnacks    = if (food.typeOfMeal == "snacks")    day.snacks.filter    { it.id != food.id } else day.snacks

        // Пересчитываем БЖУ с нуля по оставшимся продуктам — не вычитаем из старого,
        // чтобы избежать накопления ошибок при множественных удалениях
        val allRemaining = updatedBreakfast + updatedLunch + updatedDinner + updatedSnacks

        val dayCopy = day.copy(
            calories      = allRemaining.sumOf { it.calories }.toFloat(),
            protein       = allRemaining.sumOf { it.protein.toDouble() }.toFloat(),
            fats          = allRemaining.sumOf { it.fats.toDouble() }.toFloat(),
            carbohydrates = allRemaining.sumOf { it.carbohydrates.toDouble() }.toFloat(),
            breakfast     = updatedBreakfast,
            lunch         = updatedLunch,
            dinner        = updatedDinner,
            snacks        = updatedSnacks
        )

        return repository.updateDayFoodItems(dayCopy)
    }
}