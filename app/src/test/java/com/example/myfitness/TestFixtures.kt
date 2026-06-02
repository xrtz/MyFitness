package com.example.myfitness

import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.models.FoodModel
import com.example.myfitness.domain.models.UserModel

fun testFood(
    id: Int = 1,
    name: String = "Гречка",
    weight: Float = 100f,
    calories: Int = 200,
    typeOfMeal: String = "breakfast",
    protein: Float = 10f,
    fats: Float = 5f,
    carbohydrates: Float = 30f
) = FoodModel(id, name, weight, calories, typeOfMeal, protein, fats, carbohydrates)

fun emptyTestDay(
    id: Int = 1,
    date: Int = 19900
) = DayFoodItemModel(
    id = id,
    userId = 0,
    date = date,
    calories = 0f,
    protein = 0f,
    fats = 0f,
    carbohydrates = 0f,
    breakfast = emptyList(),
    lunch = emptyList(),
    dinner = emptyList(),
    snacks = emptyList()
)

fun testUser(
    weight: Float = 70f,
    height: Float = 175f,
    gender: Int = 1,
    target: String = "поддержание"
) = UserModel(
    id = 1,
    name = "Тест",
    gender = gender,
    gmail = "test@test.com",
    weight = weight,
    height = height,
    target = target
)
