package com.example.myfitness.domain.models

data class UserModel(
    val id: Int,
    val name: String,
    val gender: Int,
    val gmail: String,
    val weight: Float,
    val height: Float,
    val target: String
)
