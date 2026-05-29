package com.example.myfitness.domain.repository

import com.example.myfitness.domain.models.UserModel

interface UserRepository {
    suspend fun login(email: String, password: String): String
    suspend fun register(
        name     : String,
        email    : String,
        password : String,
        gender   : Int,
        weight   : Float,
        height   : Float,
        target   : String
    ): String
    suspend fun getProfile(): UserModel
    suspend fun updateProfile(user: UserModel): UserModel
}
