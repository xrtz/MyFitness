package com.example.myfitness.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfitness.data.remote.ApiService
import com.example.myfitness.data.remote.TokenProvider
import com.example.myfitness.data.remote.dto.LoginRequest
import com.example.myfitness.data.remote.dto.RegisterRequest
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
    private val apiService: ApiService
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
                val response = apiService.login(LoginRequest(email, password))
                when {
                    response.isSuccessful -> {
                        TokenProvider.save(response.body()!!.token)
                        Log.d("AUTH_VM", "Login OK")
                        _state.value = AuthState.Success
                    }
                    response.code() == 401 || response.code() == 404 ->
                        _state.value = AuthState.Error("Неверный email или пароль")
                    else ->
                        _state.value = AuthState.Error("Ошибка сервера (${response.code()})")
                }
            } catch (e: Exception) {
                Log.e("AUTH_VM", "Login error", e)
                _state.value = AuthState.Error(e.message ?: "Ошибка подключения")
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
                val response = apiService.register(
                    RegisterRequest(
                        name     = name,
                        email    = email,
                        password = password,
                        gender   = gender,
                        weight   = weight.toFloatOrNull() ?: 0f,
                        height   = height.toFloatOrNull() ?: 0f,
                        target   = target
                    )
                )
                when {
                    response.isSuccessful -> {
                        TokenProvider.save(response.body()!!.token)
                        Log.d("AUTH_VM", "Register OK")
                        _state.value = AuthState.Success
                    }
                    response.code() == 409 ->
                        _state.value = AuthState.Error("Email уже зарегистрирован")
                    else ->
                        _state.value = AuthState.Error("Ошибка регистрации (${response.code()})")
                }
            } catch (e: Exception) {
                Log.e("AUTH_VM", "Register error", e)
                _state.value = AuthState.Error(e.message ?: "Ошибка подключения")
            }
        }
    }

    fun isLoggedIn() = TokenProvider.token != null

    fun logout() = TokenProvider.clear()

    fun resetState() {
        _state.value = AuthState.Idle
    }
}
