package com.example.myfitness.domain.usecase

import com.example.myfitness.domain.function.Util
import com.example.myfitness.domain.repository.UserRepository
import javax.inject.Inject

class GetCaloriesGoalUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend fun execute(): Float {
        val user = repository.getProfile()
        val bmr  = Util.howMuchNeedCalories(
            weight = user.weight.coerceAtLeast(1f),
            height = user.height.coerceAtLeast(1f),
            age    = 25,
            gender = user.gender
        )
        return when {
            user.target.contains("похудение", ignoreCase = true) -> bmr - 500f
            user.target.contains("набор",     ignoreCase = true) -> bmr + 300f
            else                                                  -> bmr
        }.coerceAtLeast(1200f)
    }
}
