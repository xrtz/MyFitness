package com.example.myfitness.data.storage.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myfitness.data.storage.room.entity.DayFoodEntity
import com.example.myfitness.data.storage.room.entity.FoodEntity

@Dao
interface FoodDao {


    @Query("SELECT * FROM day_food WHERE date = :date AND userKey = :userKey LIMIT 1")
    fun getDayFood(date: Int, userKey: String): DayFoodEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateDay(day: DayFoodEntity): Long


    @Query("SELECT * FROM food_items WHERE dayId = :dayId")
    fun getFoodByDay(dayId: Int): List<FoodEntity>

    @Query("SELECT * FROM food_items WHERE id = :id LIMIT 1")
    fun getFoodById(id: Int): FoodEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFood(food: FoodEntity): Long

    @Update
    fun updateFood(food: FoodEntity)

    @Delete
    fun deleteFood(food: FoodEntity)

    @Query("DELETE FROM food_items WHERE id = :id")
    fun deleteFoodById(id: Int)

    @Query("DELETE FROM food_items WHERE dayId = :dayId")
    fun deleteAllFoodByDayId(dayId: Int)
}