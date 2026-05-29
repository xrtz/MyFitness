package com.example.myfitness.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfitness.domain.models.UserModel
import com.example.myfitness.domain.usecase.GetUserProfileUseCase
import com.example.myfitness.domain.usecase.UpdateUserProfileUseCase
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
    private val getUserProfileUseCase    : GetUserProfileUseCase,
    private val updateUserProfileUseCase : UpdateUserProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state

    init { loadProfile() }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                val user = getUserProfileUseCase.execute()
                _state.value = ProfileState(
                    name   = user.name,
                    gender = user.gender.toString(),
                    email  = user.gmail,
                    weight = if (user.weight > 0) user.weight.toString() else "",
                    height = if (user.height > 0) user.height.toString() else "",
                    target = user.target
                )
            } catch (_: Exception) { }
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
                updateUserProfileUseCase.execute(
                    UserModel(
                        id     = 0,
                        name   = s.name,
                        gender = s.gender.toIntOrNull() ?: 0,
                        gmail  = s.email,
                        weight = s.weight.toFloatOrNull() ?: 0f,
                        height = s.height.toFloatOrNull() ?: 0f,
                        target = s.target
                    )
                )
                _state.value = _state.value.copy(isSaved = true, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error     = e.message ?: "Нет подключения к серверу",
                    isLoading = false
                )
            }
        }
    }
}
