package com.example.myfitness.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("gender") val gender: Int = 0,
    @SerializedName("weight") val weight: Float = 0f,
    @SerializedName("height") val height: Float = 0f,
    @SerializedName("target") val target: String = ""
)

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: UserResponse
)

data class UserRequest(
    @SerializedName("name") val name: String,
    @SerializedName("gender") val gender: Int,
    @SerializedName("email") val email: String,
    @SerializedName("weight") val weight: Float,
    @SerializedName("height") val height: Float,
    @SerializedName("target") val target: String
)

data class UserResponse(
    @SerializedName("firebaseUid") val firebaseUid: String,
    @SerializedName("name") val name: String,
    @SerializedName("gender") val gender: Int,
    @SerializedName("email") val email: String,
    @SerializedName("weight") val weight: Float,
    @SerializedName("height") val height: Float,
    @SerializedName("target") val target: String
)

data class FoodItemDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("name") val name: String,
    @SerializedName("weight") val weight: Float,
    @SerializedName("calories") val calories: Int,
    @SerializedName("typeOfMeal") val typeOfMeal: String,
    @SerializedName("protein") val protein: Float,
    @SerializedName("fats") val fats: Float,
    @SerializedName("carbohydrates") val carbohydrates: Float
)

data class DayFoodRequest(
    @SerializedName("date") val date: Int,
    @SerializedName("calories") val calories: Float,
    @SerializedName("protein") val protein: Float,
    @SerializedName("fats") val fats: Float,
    @SerializedName("carbohydrates") val carbohydrates: Float,
    @SerializedName("foodItems") val foodItems: List<FoodItemDto>
)

data class DayFoodResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("date") val date: Int,
    @SerializedName("calories") val calories: Float,
    @SerializedName("protein") val protein: Float,
    @SerializedName("fats") val fats: Float,
    @SerializedName("carbohydrates") val carbohydrates: Float,
    @SerializedName("breakfast") val breakfast: List<FoodItemDto>,
    @SerializedName("lunch") val lunch: List<FoodItemDto>,
    @SerializedName("dinner") val dinner: List<FoodItemDto>,
    @SerializedName("snacks") val snacks: List<FoodItemDto>
)
