package com.example.myfitness.domain.usecase

import com.example.myfitness.domain.repository.UserRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend fun execute(email: String, password: String): String {
        return repository.login(email, password)
    }
}
