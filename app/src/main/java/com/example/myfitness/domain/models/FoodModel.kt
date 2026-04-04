package com.example.myfitness.domain.models

data class FoodModel(
    val id: Int,
    val name: String,
    val weight: Float,
    val calories: Int,
    val typeOfMeal: String,
    val protein: Float,
    val fats: Float,
    val carbohydrates: Float
)
