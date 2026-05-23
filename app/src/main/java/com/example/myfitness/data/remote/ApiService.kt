package com.example.myfitness.data.remote

import com.example.myfitness.data.remote.dto.AuthResponse
import com.example.myfitness.data.remote.dto.DayFoodRequest
import com.example.myfitness.data.remote.dto.DayFoodResponse
import com.example.myfitness.data.remote.dto.LoginRequest
import com.example.myfitness.data.remote.dto.RegisterRequest
import com.example.myfitness.data.remote.dto.UserRequest
import com.example.myfitness.data.remote.dto.UserResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("api/users/me")
    suspend fun getMe(): Response<UserResponse>

    @PUT("api/users/me")
    suspend fun updateMe(@Body request: UserRequest): Response<UserResponse>

    @GET("api/days/{date}")
    suspend fun getDay(@Path("date") epochDay: Int): Response<DayFoodResponse>

    @PUT("api/days")
    suspend fun saveDay(@Body request: DayFoodRequest): Response<DayFoodResponse>
}
