package com.example.myfitness.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfitness.data.remote.ApiService
import com.example.myfitness.data.remote.dto.UserRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProfileState(
    val name      : String  = "",
    val gender    : String  = "1",
    val email     : String  = "",
    val weight    : String  = "",
    val height    : String  = "",
    val target    : String  = "",
    val isSaved   : Boolean = false,
    val isLoading : Boolean = false,
    val error     : String? = null
)

class ProfileViewModel(
    private val apiService: ApiService
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state

    init { loadFromServer() }

    private fun loadFromServer() {
        viewModelScope.launch {
            try {
                val response = apiService.getMe()
                if (response.isSuccessful) {
                    val user = response.body()!!
                    _state.value = ProfileState(
                        name   = user.name,
                        gender = user.gender.toString(),
                        email  = user.email,
                        weight = if (user.weight > 0) user.weight.toString() else "",
                        height = if (user.height > 0) user.height.toString() else "",
                        target = user.target
                    )
                }
            } catch (e: Exception) {
                // Нет интернета — поля пустые
            }
        }
    }

    fun onNameChange(v: String)   { _state.value = _state.value.copy(name = v,   isSaved = false) }
    fun onGenderChange(v: String) { _state.value = _state.value.copy(gender = v, isSaved = false) }
    fun onEmailChange(v: String)  { _state.value = _state.value.copy(email = v,  isSaved = false) }
    fun onWeightChange(v: String) { _state.value = _state.value.copy(weight = v, isSaved = false) }
    fun onHeightChange(v: String) { _state.value = _state.value.copy(height = v, isSaved = false) }
    fun onTargetChange(v: String) { _state.value = _state.value.copy(target = v, isSaved = false) }

    fun save() {
        val s = _state.value
        if (s.name.isBlank()) {
            _state.value = s.copy(error = "Введите имя")
            return
        }
        viewModelScope.launch {
            _state.value = s.copy(isLoading = true, error = null)
            try {
                val response = apiService.updateMe(
                    UserRequest(
                        name   = s.name,
                        gender = s.gender.toIntOrNull() ?: 0,
                        email  = s.email,
                        weight = s.weight.toFloatOrNull() ?: 0f,
                        height = s.height.toFloatOrNull() ?: 0f,
                        target = s.target
                    )
                )
                if (response.isSuccessful) {
                    _state.value = _state.value.copy(isSaved = true, isLoading = false)
                } else {
                    _state.value = _state.value.copy(
                        error = "Ошибка ${response.code()}",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Нет подключения к серверу",
                    isLoading = false
                )
            }
        }
    }
}