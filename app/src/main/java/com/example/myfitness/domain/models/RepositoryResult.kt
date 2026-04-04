package com.example.myfitness.domain.models

sealed class RepositoryResult {
    data class Success(val data: String) : RepositoryResult()
    data class Error(val error: Throwable) : RepositoryResult()
    object Loading : RepositoryResult()
}