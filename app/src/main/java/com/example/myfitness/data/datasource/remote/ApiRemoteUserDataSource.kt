package com.example.myfitness.data.datasource.remote

import com.example.myfitness.data.remote.ApiService
import com.example.myfitness.data.remote.dto.LoginRequest
import com.example.myfitness.data.remote.dto.RegisterRequest
import com.example.myfitness.data.remote.dto.UserRequest
import com.example.myfitness.data.remote.dto.UserResponse
import com.example.myfitness.domain.models.UserModel
import javax.inject.Inject

class ApiRemoteUserDataSource @Inject constructor(
    private val apiService: ApiService
) : RemoteUserDataSource {

    override suspend fun login(email: String, password: String): Pair<String, String> {
        val response = apiService.login(LoginRequest(email, password))
        if (response.isSuccessful) {
            val body = response.body()!!
            return Pair(body.token, body.user.firebaseUid)
        }
        throw Exception("Login failed: ${response.code()}")
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        gender: Int,
        weight: Float,
        height: Float,
        target: String
    ): Pair<String, String> {
        val response = apiService.register(
            RegisterRequest(name, email, password, gender, weight, height, target)
        )
        if (response.isSuccessful) {
            val body = response.body()!!
            return Pair(body.token, body.user.firebaseUid)
        }
        throw Exception("Register failed: ${response.code()}")
    }

    override suspend fun getProfile(): UserModel {
        val response = apiService.getMe()
        if (response.isSuccessful) return response.body()!!.toDomain()
        throw Exception("Get profile failed: ${response.code()}")
    }

    override suspend fun updateProfile(user: UserModel): UserModel {
        val response = apiService.updateMe(
            UserRequest(
                name = user.name,
                gender = user.gender,
                email = user.gmail,
                weight = user.weight,
                height = user.height,
                target = user.target
            )
        )
        if (response.isSuccessful) return response.body()!!.toDomain()
        throw Exception("Update profile failed: ${response.code()}")
    }

    private fun UserResponse.toDomain() = UserModel(
        id = 0,
        name = name,
        gender = gender,
        gmail = email,
        weight = weight,
        height = height,
        target = target
    )
}
