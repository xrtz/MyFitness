package com.example.myfitness.data.storage.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val gender: Int,
    val gmail: String,
    val weight: Float,
    val height: Float,
    val target: String
)
