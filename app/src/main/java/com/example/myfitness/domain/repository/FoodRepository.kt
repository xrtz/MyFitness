package com.example.myfitness.domain.repository

import com.example.myfitness.domain.models.DateModel
import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.models.FoodModel
import com.example.myfitness.domain.models.RepositoryResult
import com.example.myfitness.domain.models.UserModel

interface FoodRepository {
    fun updateDayFoodItems(day: DayFoodItemModel): RepositoryResult

    fun getDayFoodItems(date: DateModel): DayFoodItemModel
    fun getUserModel(userModel: UserModel): RepositoryResult
    fun updateUserModel(userModel: UserModel): RepositoryResult

}