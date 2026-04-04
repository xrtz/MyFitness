package com.example.myfitness.data.storage

import com.example.myfitness.domain.models.DateModel
import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.models.FoodModel
import com.example.myfitness.domain.models.RepositoryResult
import com.example.myfitness.domain.models.UserModel

class StorageImpl: Storage {
    override fun updateDayFoodItems(day: DayFoodItemModel): RepositoryResult {
        TODO("Not yet implemented")
    }

    override fun getDayFoodItems(date: DateModel): DayFoodItemModel {
        TODO("Not yet implemented")
    }

    override fun getUserModel(userModel: UserModel): RepositoryResult {
        TODO("Not yet implemented")
    }

    override fun updateUserModel(userModel: UserModel): RepositoryResult {
        TODO("Not yet implemented")
    }

}