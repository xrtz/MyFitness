package com.example.myfitness.domain.function

import com.example.myfitness.domain.models.DateModel
import java.time.LocalDate

object Util {

    fun dayToModel(date: LocalDate): DateModel {
        return DateModel(date.toEpochDay().toInt())
    }

    fun howMuchNeedCalories(
        weight: Float,
        height: Float,
        age: Int,
        gender: Int,
        activityLevel: Float = 1.375f
    ): Float {
        val bmr = if (gender == 1) {
            10 * weight + 6.25 * height - 5 * age + 5
        } else {
            10 * weight + 6.25 * height - 5 * age - 161
        }
        return (bmr * activityLevel).toFloat()
    }
}