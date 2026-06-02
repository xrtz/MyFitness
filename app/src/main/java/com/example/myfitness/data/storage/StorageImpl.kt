package com.example.myfitness.data.storage

import com.example.myfitness.domain.models.DateModel
import com.example.myfitness.domain.models.DayFoodItemModel

class StorageImpl : Storage {
    override fun getDayFoodItems(date: DateModel): DayFoodItemModel {
        TODO("Not yet implemented")
    }

    override fun updateDayFoodItems(day: DayFoodItemModel, isSynced: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getUnsyncedDays(): List<DayFoodItemModel> {
        TODO("Not yet implemented")
    }
}
