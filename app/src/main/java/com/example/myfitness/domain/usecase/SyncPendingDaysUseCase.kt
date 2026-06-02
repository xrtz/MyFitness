package com.example.myfitness.domain.usecase

import com.example.myfitness.domain.repository.FoodRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SyncPendingDaysUseCase @Inject constructor(
    private val repository: FoodRepository
) {
    suspend fun execute() {
        val pending = withContext(Dispatchers.IO) { repository.getPendingSyncDays() }
        pending.forEach { day -> repository.syncDayToServer(day) }
    }
}
