package com.example.myfitness.data.storage.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "day_food")
data class DayFoodEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int = 0,
    val userKey: String = "",
    val date: Int,
    val calories: Float,
    val protein: Float,
    val fats: Float,
    val carbohydrates: Float,
    val isSynced: Boolean = false
)
