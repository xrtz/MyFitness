package com.example.myfitness.data.storage.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myfitness.data.storage.room.dao.FoodDao
import com.example.myfitness.data.storage.room.dao.UserDao
import com.example.myfitness.data.storage.room.entity.DayFoodEntity
import com.example.myfitness.data.storage.room.entity.FoodEntity
import com.example.myfitness.data.storage.room.entity.UserEntity

@Database(
    entities = [
        FoodEntity::class,
        DayFoodEntity::class,
        UserEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun userDao(): UserDao
}
