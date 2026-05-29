package com.example.myfitness.data.storage.room

import com.example.myfitness.data.remote.TokenProvider
import com.example.myfitness.data.storage.Storage
import com.example.myfitness.data.storage.room.dao.FoodDao
import com.example.myfitness.data.storage.room.entity.DayFoodEntity
import com.example.myfitness.data.storage.room.entity.FoodEntity
import com.example.myfitness.domain.models.DateModel
import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.models.FoodModel
import javax.inject.Inject

class RoomStorageImpl @Inject constructor(
    private val foodDao: FoodDao
) : Storage {

    private fun currentUserKey() = TokenProvider.userId ?: ""

    override fun getDayFoodItems(date: DateModel): DayFoodItemModel {
        val userKey   = currentUserKey()
        val dayEntity = foodDao.getDayFood(date.day, userKey) ?: return emptyDay(date.day)
        val allFood   = foodDao.getFoodByDay(dayEntity.id)

        return DayFoodItemModel(
            id            = dayEntity.id,
            userId        = dayEntity.userId,
            date          = dayEntity.date,
            calories      = dayEntity.calories,
            protein       = dayEntity.protein,
            fats          = dayEntity.fats,
            carbohydrates = dayEntity.carbohydrates,
            breakfast     = allFood.filter { it.typeOfMeal == "breakfast" }.map { it.toModel() },
            lunch         = allFood.filter { it.typeOfMeal == "lunch"     }.map { it.toModel() },
            dinner        = allFood.filter { it.typeOfMeal == "dinner"    }.map { it.toModel() },
            snacks        = allFood.filter { it.typeOfMeal == "snacks"    }.map { it.toModel() }
        )
    }

    override fun updateDayFoodItems(day: DayFoodItemModel) {
        val userKey     = currentUserKey()
        val existingDay = foodDao.getDayFood(day.date, userKey)

        val resolvedDayId: Int = if (existingDay == null) {
            foodDao.insertOrUpdateDay(
                DayFoodEntity(
                    id            = 0,
                    userId        = day.userId,
                    userKey       = userKey,
                    date          = day.date,
                    calories      = day.calories,
                    protein       = day.protein,
                    fats          = day.fats,
                    carbohydrates = day.carbohydrates
                )
            ).toInt()
        } else {
            foodDao.insertOrUpdateDay(
                existingDay.copy(
                    calories      = day.calories,
                    protein       = day.protein,
                    fats          = day.fats,
                    carbohydrates = day.carbohydrates,
                    userKey       = userKey
                )
            )
            existingDay.id
        }

        foodDao.deleteAllFoodByDayId(resolvedDayId)
        val allFood = day.breakfast + day.lunch + day.dinner + day.snacks
        allFood.forEach { food ->
            foodDao.insertFood(food.toEntity(dayId = resolvedDayId, forceNewId = true))
        }
    }

    private fun FoodEntity.toModel() = FoodModel(
        id            = id,
        name          = name,
        weight        = weight,
        calories      = calories,
        typeOfMeal    = typeOfMeal,
        protein       = protein,
        fats          = fats,
        carbohydrates = carbohydrates
    )

    private fun FoodModel.toEntity(dayId: Int, forceNewId: Boolean = false) = FoodEntity(
        id            = if (forceNewId) 0 else id,
        dayId         = dayId,
        name          = name,
        weight        = weight,
        calories      = calories,
        typeOfMeal    = typeOfMeal,
        protein       = protein,
        fats          = fats,
        carbohydrates = carbohydrates
    )

    private fun emptyDay(date: Int) = DayFoodItemModel(
        id            = 0,
        userId        = 0,
        date          = date,
        calories      = 0f,
        protein       = 0f,
        fats          = 0f,
        carbohydrates = 0f,
        breakfast     = emptyList(),
        lunch         = emptyList(),
        dinner        = emptyList(),
        snacks        = emptyList()
    )
}
