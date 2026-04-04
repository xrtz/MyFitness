package com.example.myfitness.data.repository

import com.example.myfitness.data.storage.StorageImpl
import com.example.myfitness.domain.models.DateModel
import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.models.FoodModel
import com.example.myfitness.domain.models.RepositoryResult
import com.example.myfitness.domain.models.UserModel
import com.example.myfitness.domain.repository.FoodRepository

class FoodRepositoryImpl: FoodRepository {
    val storage = StorageImpl() // DI
    override fun updateDayFoodItems(day: DayFoodItemModel): RepositoryResult{
        return storage.updateDayFoodItems(day)
    }
    override fun getDayFoodItems(date: DateModel) : DayFoodItemModel {
        return storage.getDayFoodItems(date)
    }

    override fun getUserModel(userModel: UserModel): RepositoryResult {
        return storage.getUserModel(userModel)
    }

    override fun updateUserModel(userModel: UserModel): RepositoryResult {
        return storage.updateUserModel(userModel)
    }


}