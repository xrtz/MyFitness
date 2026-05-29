package com.example.myfitness.data.datasource.remote

import com.example.myfitness.data.remote.ApiService
import com.example.myfitness.data.remote.dto.DayFoodRequest
import com.example.myfitness.data.remote.dto.DayFoodResponse
import com.example.myfitness.data.remote.dto.FoodItemDto
import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.models.FoodModel
import javax.inject.Inject

class ApiRemoteFoodDataSource @Inject constructor(
    private val apiService: ApiService
) : RemoteFoodDataSource {

    override suspend fun getDay(epochDay: Int): DayFoodItemModel? {
        val response = apiService.getDay(epochDay)
        return if (response.isSuccessful) response.body()?.toDomain(epochDay) else null
    }

    override suspend fun saveDay(day: DayFoodItemModel): DayFoodItemModel {
        val response = apiService.saveDay(day.toRequest())
        return if (response.isSuccessful) response.body()?.toDomain(day.date) ?: day else day
    }

    private fun DayFoodResponse.toDomain(epochDay: Int) = DayFoodItemModel(
        id            = id.toInt(),
        userId        = 0,
        date          = epochDay,
        calories      = calories,
        protein       = protein,
        fats          = fats,
        carbohydrates = carbohydrates,
        breakfast     = breakfast.map { it.toDomain() },
        lunch         = lunch.map     { it.toDomain() },
        dinner        = dinner.map    { it.toDomain() },
        snacks        = snacks.map    { it.toDomain() }
    )

    private fun FoodItemDto.toDomain() = FoodModel(
        id            = id.toInt(),
        name          = name,
        weight        = weight,
        calories      = calories,
        typeOfMeal    = typeOfMeal,
        protein       = protein,
        fats          = fats,
        carbohydrates = carbohydrates
    )

    private fun DayFoodItemModel.toRequest() = DayFoodRequest(
        date          = date,
        calories      = calories,
        protein       = protein,
        fats          = fats,
        carbohydrates = carbohydrates,
        foodItems     = (breakfast + lunch + dinner + snacks).map { it.toDto() }
    )

    private fun FoodModel.toDto() = FoodItemDto(
        id            = id.toLong(),
        name          = name,
        weight        = weight,
        calories      = calories,
        typeOfMeal    = typeOfMeal,
        protein       = protein,
        fats          = fats,
        carbohydrates = carbohydrates
    )
}
