package com.example.myfitness.domain.usecase

import com.example.myfitness.domain.repository.UserRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend fun execute(
        name     : String,
        email    : String,
        password : String,
        gender   : Int,
        weight   : Float,
        height   : Float,
        target   : String
    ): String {
        return repository.register(name, email, password, gender, weight, height, target)
    }
}
