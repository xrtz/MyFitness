package com.example.myfitness.domain.usecase

import com.example.myfitness.domain.models.DateModel
import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.repository.FoodRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoadDayUseCase @Inject constructor(
    private val repository: FoodRepository
) {
    fun execute(epochDay: Int): Flow<DayFoodItemModel> = flow {
        val local = withContext(Dispatchers.IO) { repository.getDayFoodItems(DateModel(epochDay)) }
        emit(local)
        val remote = repository.loadDayFromServer(epochDay)
        if (remote != null) emit(remote)
    }
}
