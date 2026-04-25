package com.example.myfitness.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfitness.data.remote.ApiService
import com.example.myfitness.data.remote.TokenProvider
import com.example.myfitness.data.remote.dto.UserRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    object Idle    : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val apiService: ApiService
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    // ── Login ──────────────────────────────────────────────────

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = AuthState.Error("Заполните все поля")
            return
        }
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                val user = auth.currentUser ?: throw Exception("Не удалось получить пользователя")

                // Получаем и кешируем токен — после этого все запросы будут авторизованы
                val token = user.getIdToken(false).await().token
                TokenProvider.token = token
                Log.d("AUTH_VM", "Login OK uid=${user.uid} token=${token?.take(20)}...")

                // Проверяем что пользователь есть в нашей БД
                // Если нет (например первый вход после удаления из БД) — создаём
                ensureUserExists(user.email ?: email)

                _state.value = AuthState.Success
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _state.value = AuthState.Error("Неверный email или пароль")
            } catch (e: Exception) {
                Log.e("AUTH_VM", "Login error", e)
                _state.value = AuthState.Error(e.message ?: "Ошибка входа")
            }
        }
    }

    // ── Register ───────────────────────────────────────────────

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
                // 1. Создаём пользователя в Firebase
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user ?: throw Exception("Firebase не вернул пользователя")
                Log.d("AUTH_VM", "Firebase register OK uid=${user.uid}")

                // 2. Получаем и кешируем токен СРАЗУ — до любых запросов к серверу
                val token = user.getIdToken(false).await().token
                    ?: throw Exception("Не удалось получить токен")
                TokenProvider.token = token
                Log.d("AUTH_VM", "Token cached: ${token.take(20)}...")

                // 3. Регистрируем в нашей БД
                try {
                    val response = apiService.register(
                        UserRequest(
                            name   = name,
                            gender = gender,
                            email  = email,
                            weight = weight.toFloatOrNull() ?: 0f,
                            height = height.toFloatOrNull() ?: 0f,
                            target = target
                        )
                    )
                    if (response.isSuccessful) {
                        Log.d("AUTH_VM", "Server register OK")
                    } else {
                        Log.w("AUTH_VM", "Server register failed ${response.code()} — will retry on next login")
                    }
                } catch (e: Exception) {
                    // Сервер недоступен — не критично, войти можно
                    // При следующем логине ensureUserExists создаст запись
                    Log.w("AUTH_VM", "Server register error (offline?): ${e.message}")
                }

                _state.value = AuthState.Success
            } catch (e: FirebaseAuthWeakPasswordException) {
                _state.value = AuthState.Error("Пароль слишком простой (минимум 6 символов)")
            } catch (e: FirebaseAuthUserCollisionException) {
                _state.value = AuthState.Error("Email уже зарегистрирован")
            } catch (e: Exception) {
                Log.e("AUTH_VM", "Register error", e)
                _state.value = AuthState.Error(e.message ?: "Ошибка регистрации")
            }
        }
    }

    // ── Helpers ────────────────────────────────────────────────

    /**
     * Вызывается при логине — если пользователя нет в нашей БД (например
     * он был удалён или сервер не ответил при регистрации) — создаём запись.
     */
    private suspend fun ensureUserExists(email: String) {
        try {
            val response = apiService.getMe()
            if (response.code() == 404 || response.code() == 500) {
                // Пользователя нет — создаём с минимальными данными
                apiService.register(
                    UserRequest(
                        name   = email.substringBefore("@"),
                        gender = 0,
                        email  = email,
                        weight = 0f,
                        height = 0f,
                        target = "maintain"
                    )
                )
                Log.d("AUTH_VM", "Auto-created user on login")
            }
        } catch (e: Exception) {
            Log.w("AUTH_VM", "ensureUserExists failed (offline?): ${e.message}")
        }
    }

    fun isLoggedIn(): Boolean {
        // Если уже залогинен — обновляем токен из Firebase кеша
        val user = auth.currentUser
        if (user != null && TokenProvider.token == null) {
            viewModelScope.launch {
                try {
                    val token = user.getIdToken(false).await().token
                    TokenProvider.token = token
                    Log.d("AUTH_VM", "Token restored on app start")
                } catch (e: Exception) {
                    Log.w("AUTH_VM", "Token restore failed: ${e.message}")
                }
            }
        }
        return user != null
    }

    fun resetState() {
        _state.value = AuthState.Idle
    }
}