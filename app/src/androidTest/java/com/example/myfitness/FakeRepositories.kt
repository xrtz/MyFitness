package com.example.myfitness

import com.example.myfitness.domain.models.DateModel
import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.models.FoodModel
import com.example.myfitness.domain.models.UserModel
import com.example.myfitness.domain.repository.FoodRepository
import com.example.myfitness.domain.repository.UserRepository

fun uiTestDay(
    date: Int = 19900,
    calories: Float = 450f,
    protein: Float = 30f,
    fats: Float = 15f,
    carbs: Float = 60f,
    breakfast: List<FoodModel> = listOf(
        uiTestFood(
            id = 1,
            name = "Овсянка",
            calories = 250,
            typeOfMeal = "breakfast"
        )
    ),
    lunch: List<FoodModel> = listOf(
        uiTestFood(
            id = 2,
            name = "Курица",
            calories = 200,
            typeOfMeal = "lunch"
        )
    )
) = DayFoodItemModel(
    id = 1,
    userId = 0,
    date = date,
    calories = calories,
    protein = protein,
    fats = fats,
    carbohydrates = carbs,
    breakfast = breakfast,
    lunch = lunch,
    dinner = emptyList(),
    snacks = emptyList()
)

fun uiTestFood(
    id: Int = 1,
    name: String = "Тест",
    calories: Int = 100,
    typeOfMeal: String = "breakfast"
) = FoodModel(id, name, 100f, calories, typeOfMeal, 5f, 3f, 10f)

class FakeFoodRepository(
    private val day: DayFoodItemModel = uiTestDay()
) : FoodRepository {
    override fun getDayFoodItems(date: DateModel): DayFoodItemModel = day
    override fun updateDayFoodItems(day: DayFoodItemModel) {}
    override fun getPendingSyncDays(): List<DayFoodItemModel> = emptyList()
    override suspend fun loadDayFromServer(epochDay: Int): DayFoodItemModel? = null
    override suspend fun syncDayToServer(day: DayFoodItemModel): DayFoodItemModel = day
}

class FakeUserRepository : UserRepository {
    override suspend fun login(email: String, password: String): String = "fake_token"
    override suspend fun register(
        name: String, email: String, password: String,
        gender: Int, weight: Float, height: Float, target: String
    ): String = "fake_token"

    override suspend fun getProfile(): UserModel =
        UserModel(1, "Тест", 1, "test@test.com", 70f, 175f, "поддержание")

    override suspend fun updateProfile(user: UserModel): UserModel = user
}
