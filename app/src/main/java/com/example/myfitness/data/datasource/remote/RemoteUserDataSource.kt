package com.example.myfitness.data.datasource.remote

import com.example.myfitness.domain.models.UserModel

interface RemoteUserDataSource {
    suspend fun login(email: String, password: String): Pair<String, String>
    suspend fun register(
        name: String,
        email: String,
        password: String,
        gender: Int,
        weight: Float,
        height: Float,
        target: String
    ): Pair<String, String>

    suspend fun getProfile(): UserModel
    suspend fun updateProfile(user: UserModel): UserModel
}
