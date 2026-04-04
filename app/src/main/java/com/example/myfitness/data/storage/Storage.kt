package com.example.myfitness.data.storage

import com.example.myfitness.domain.models.DateModel
import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.models.FoodModel
import com.example.myfitness.domain.models.RepositoryResult
import com.example.myfitness.domain.models.UserModel

interface Storage {
    fun updateDayFoodItems(day: DayFoodItemModel): RepositoryResult
    fun getDayFoodItems(date: DateModel): DayFoodItemModel

    fun getUserModel(userModel: UserModel): RepositoryResult
    fun updateUserModel(userModel: UserModel): RepositoryResult

}