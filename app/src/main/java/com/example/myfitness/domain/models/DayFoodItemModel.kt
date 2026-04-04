package com.example.myfitness.domain.models

data class DayFoodItemModel(
    val id: Int,
    val userId: Int,
    val date: Int,
    val calories: Float,
    val protein: Float,
    val fats: Float,
    val carbohydrates: Float,
    val breakfast: List<FoodModel>,
    val lunch: List<FoodModel>,
    val dinner: List<FoodModel>,
    val snacks: List<FoodModel>
)
