package com.example.myfitness.domain.usecase

import com.example.myfitness.domain.models.UserModel
import com.example.myfitness.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend fun execute(user: UserModel): UserModel {
        return repository.updateProfile(user)
    }
}
