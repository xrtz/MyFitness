package com.example.myfitness.data.storage.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_items")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dayId: Int,
    val name: String,
    val weight: Float,
    val calories: Int,
    val typeOfMeal: String,
    val protein: Float,
    val fats: Float,
    val carbohydrates: Float
)
