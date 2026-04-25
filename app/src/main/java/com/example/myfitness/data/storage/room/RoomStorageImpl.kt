package com.example.myfitness.data.storage.room

import android.util.Log
import com.example.myfitness.data.storage.Storage
import com.example.myfitness.data.storage.room.dao.FoodDao
import com.example.myfitness.data.storage.room.dao.UserDao
import com.example.myfitness.data.storage.room.entity.DayFoodEntity
import com.example.myfitness.data.storage.room.entity.FoodEntity
import com.example.myfitness.data.storage.room.entity.UserEntity
import com.example.myfitness.domain.models.DateModel
import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.models.FoodModel
import com.example.myfitness.domain.models.RepositoryResult
import com.example.myfitness.domain.models.UserModel
import javax.inject.Inject

class RoomStorageImpl @Inject constructor(
    private val foodDao : FoodDao,
    private val userDao : UserDao
) : Storage {

    override fun getDayFoodItems(date: DateModel): DayFoodItemModel {
        Log.d("ROOM", "getDayFoodItems date=${date.day}")
        val dayEntity = foodDao.getDayFood(date.day) ?: return emptyDay(date.day)
        val allFood   = foodDao.getFoodByDay(dayEntity.id)
        Log.d("ROOM", "found day id=${dayEntity.id}, foods=${allFood.size}")

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

    override fun updateDayFoodItems(day: DayFoodItemModel): RepositoryResult {
        return try {
            Log.d("ROOM", "updateDayFoodItems day.date=${day.date} day.id=${day.id}")

            val existingDay = foodDao.getDayFood(day.date)

            val resolvedDayId: Int = if (existingDay == null) {
                val newEntity = DayFoodEntity(
                    id            = 0,
                    userId        = day.userId,
                    date          = day.date,
                    calories      = day.calories,
                    protein       = day.protein,
                    fats          = day.fats,
                    carbohydrates = day.carbohydrates
                )
                val newId = foodDao.insertOrUpdateDay(newEntity).toInt()
                Log.d("ROOM", "Inserted new day, id=$newId")
                newId
            } else {
                foodDao.insertOrUpdateDay(
                    existingDay.copy(
                        calories      = day.calories,
                        protein       = day.protein,
                        fats          = day.fats,
                        carbohydrates = day.carbohydrates
                    )
                )
                Log.d("ROOM", "Updated existing day, id=${existingDay.id}")
                existingDay.id
            }

            foodDao.deleteAllFoodByDayId(resolvedDayId)
            Log.d("ROOM", "Deleted all old foods for dayId=$resolvedDayId")

            val allFood = day.breakfast + day.lunch + day.dinner + day.snacks
            Log.d("ROOM", "Inserting ${allFood.size} foods")
            allFood.forEach { food ->
                foodDao.insertFood(food.toEntity(dayId = resolvedDayId, forceNewId = true))
            }

            RepositoryResult.Success("OK dayId=$resolvedDayId")
        } catch (e: Exception) {
            Log.e("ROOM", "updateDayFoodItems error", e)
            RepositoryResult.Error(e)
        }
    }

    override fun getUserModel(userModel: UserModel): RepositoryResult {
        return try {
            if (userDao.getUser() != null) RepositoryResult.Success("User found")
            else RepositoryResult.Error(Exception("User not found"))
        } catch (e: Exception) { RepositoryResult.Error(e) }
    }

    override fun updateUserModel(userModel: UserModel): RepositoryResult {
        return try {
            val entity   = userModel.toEntity()
            val existing = userDao.getUser()
            if (existing == null) userDao.insertUser(entity)
            else userDao.updateUser(entity.copy(id = existing.id))
            RepositoryResult.Success("User updated")
        } catch (e: Exception) { RepositoryResult.Error(e) }
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

    private fun UserModel.toEntity() = UserEntity(
        id     = id,
        name   = name,
        gender = gender,
        gmail  = gmail,
        weight = weight,
        height = height,
        target = target
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