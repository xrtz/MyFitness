package com.example.myfitness.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfitness.data.remote.TokenProvider
import com.example.myfitness.domain.usecase.LoginUseCase
import com.example.myfitness.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle    : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val loginUseCase    : LoginUseCase,
    private val registerUseCase : RegisterUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = AuthState.Error("Заполните все поля")
            return
        }
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                loginUseCase.execute(email, password)
                _state.value = AuthState.Success
            } catch (e: Exception) {
                val msg = when {
                    e.message?.contains("401") == true || e.message?.contains("404") == true ->
                        "Неверный email или пароль"
                    else -> e.message ?: "Ошибка подключения"
                }
                _state.value = AuthState.Error(msg)
            }
        }
    }

    fun register(
        email    : String,
        password : String,
        name     : String,
        weight   : String,
        height   : String,
        gender   : Int,
        target   : String
    ) {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            _state.value = AuthState.Error("Заполните обязательные поля")
            return
        }
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                registerUseCase.execute(
                    name     = name,
                    email    = email,
                    password = password,
                    gender   = gender,
                    weight   = weight.toFloatOrNull() ?: 0f,
                    height   = height.toFloatOrNull() ?: 0f,
                    target   = target
                )
                _state.value = AuthState.Success
            } catch (e: Exception) {
                val msg = when {
                    e.message?.contains("409") == true -> "Email уже зарегистрирован"
                    else -> e.message ?: "Ошибка регистрации"
                }
                _state.value = AuthState.Error(msg)
            }
        }
    }

    fun isLoggedIn() = TokenProvider.token != null

    fun logout() = TokenProvider.clear()

    fun resetState() {
        _state.value = AuthState.Idle
    }
}
