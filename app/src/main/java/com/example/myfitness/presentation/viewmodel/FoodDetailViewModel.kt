package com.example.myfitness.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.models.FoodModel
import com.example.myfitness.domain.usecase.AddFoodItemUseCase
import com.example.myfitness.domain.usecase.ChangeFoodItemUseCase
import com.example.myfitness.domain.usecase.SyncDayUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class FoodDetailState(
    val name: String = "",
    val weight: String = "",
    val calories: String = "",
    val protein: String = "",
    val fats: String = "",
    val carbohydrates: String = "",
    val isSaved: Boolean = false,
    val error: String? = null
)

class FoodDetailViewModel(
    private val addFoodItemUseCase: AddFoodItemUseCase,
    private val changeFoodItemUseCase: ChangeFoodItemUseCase,
    private val syncDayUseCase: SyncDayUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FoodDetailState())
    val state: StateFlow<FoodDetailState> = _state

    fun onNameChange(v: String) {
        _state.value = _state.value.copy(name = v, error = null)
    }

    fun onWeightChange(v: String) {
        _state.value = _state.value.copy(weight = v, error = null)
    }

    fun onCaloriesChange(v: String) {
        _state.value = _state.value.copy(calories = v, error = null)
    }

    fun onProteinChange(v: String) {
        _state.value = _state.value.copy(protein = v, error = null)
    }

    fun onFatsChange(v: String) {
        _state.value = _state.value.copy(fats = v, error = null)
    }

    fun onCarbohydratesChange(v: String) {
        _state.value = _state.value.copy(carbohydrates = v, error = null)
    }

    fun loadFood(food: FoodModel) {
        _state.value = FoodDetailState(
            name = food.name,
            weight = food.weight.toString(),
            calories = food.calories.toString(),
            protein = food.protein.toString(),
            fats = food.fats.toString(),
            carbohydrates = food.carbohydrates.toString()
        )
    }

    fun resetSaved() {
        _state.value = _state.value.copy(isSaved = false)
    }

    fun resetForm() {
        _state.value = FoodDetailState()
    }

    fun save(
        typeOfMeal: String,
        currentDay: DayFoodItemModel,
        foodId: Int?,
        onSaved: (DayFoodItemModel) -> Unit
    ) {
        val s = _state.value
        if (s.name.isBlank()) {
            _state.value = s.copy(error = "Введите название")
            return
        }

        val food = FoodModel(
            id = foodId ?: 0,
            name = s.name,
            weight = s.weight.toFloatOrNull() ?: 0f,
            calories = s.calories.toIntOrNull() ?: 0,
            typeOfMeal = typeOfMeal,
            protein = s.protein.toFloatOrNull() ?: 0f,
            fats = s.fats.toFloatOrNull() ?: 0f,
            carbohydrates = s.carbohydrates.toFloatOrNull() ?: 0f
        )

        viewModelScope.launch {
            try {
                val updatedDay = if (foodId == null) {
                    addFoodItemUseCase.execute(food, currentDay)
                } else {
                    changeFoodItemUseCase.execute(food, currentDay)
                }
                val syncedDay = syncDayUseCase.execute(updatedDay)
                _state.value = _state.value.copy(isSaved = true)
                onSaved(syncedDay)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }
}
