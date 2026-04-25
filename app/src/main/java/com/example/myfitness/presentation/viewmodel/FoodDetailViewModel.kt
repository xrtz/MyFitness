package com.example.myfitness.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.models.FoodModel
import com.example.myfitness.domain.models.RepositoryResult
import com.example.myfitness.domain.usecase.AddFoodItemUseCase
import com.example.myfitness.domain.usecase.ChangeFoodItemUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class FoodDetailState(
    val name          : String  = "",
    val weight        : String  = "",
    val calories      : String  = "",
    val protein       : String  = "",
    val fats          : String  = "",
    val carbohydrates : String  = "",
    val isSaved       : Boolean = false,
    val error         : String? = null
)

class FoodDetailViewModel(
    private val addFoodItemUseCase    : AddFoodItemUseCase,
    private val changeFoodItemUseCase : ChangeFoodItemUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FoodDetailState())
    val state: StateFlow<FoodDetailState> = _state

    fun onNameChange(v: String)          { _state.value = _state.value.copy(name = v,          error = null) }
    fun onWeightChange(v: String)        { _state.value = _state.value.copy(weight = v,        error = null) }
    fun onCaloriesChange(v: String)      { _state.value = _state.value.copy(calories = v,      error = null) }
    fun onProteinChange(v: String)       { _state.value = _state.value.copy(protein = v,       error = null) }
    fun onFatsChange(v: String)          { _state.value = _state.value.copy(fats = v,          error = null) }
    fun onCarbohydratesChange(v: String) { _state.value = _state.value.copy(carbohydrates = v, error = null) }

    fun loadFood(food: FoodModel) {
        _state.value = FoodDetailState(
            name          = food.name,
            weight        = food.weight.toString(),
            calories      = food.calories.toString(),
            protein       = food.protein.toString(),
            fats          = food.fats.toString(),
            carbohydrates = food.carbohydrates.toString()
        )
    }

    fun resetSaved() {
        _state.value = _state.value.copy(isSaved = false)
    }

    /**
     * Сохраняет еду и вызывает onSaved(dayCopy) с обновлённым днём.
     * dayCopy содержит актуальный список еды — передаём его напрямую
     * в HomeViewModel.syncDayToServer() чтобы избежать race condition.
     */
    fun save(
        typeOfMeal : String,
        currentDay : DayFoodItemModel,
        foodId     : Int?,
        onSaved    : (dayCopy: DayFoodItemModel) -> Unit
    ) {
        val s = _state.value
        if (s.name.isBlank()) {
            _state.value = s.copy(error = "Введите название")
            return
        }

        val food = FoodModel(
            id            = foodId ?: 0,
            name          = s.name,
            weight        = s.weight.toFloatOrNull() ?: 0f,
            calories      = s.calories.toIntOrNull() ?: 0,
            typeOfMeal    = typeOfMeal,
            protein       = s.protein.toFloatOrNull() ?: 0f,
            fats          = s.fats.toFloatOrNull() ?: 0f,
            carbohydrates = s.carbohydrates.toFloatOrNull() ?: 0f
        )

        Log.d("DETAIL_VM", "save: typeOfMeal=$typeOfMeal, currentDay.date=${currentDay.date}, currentDay.id=${currentDay.id}")

        val result = if (foodId == null) {
            addFoodItemUseCase.execute(food, currentDay)
        } else {
            changeFoodItemUseCase.execute(food, currentDay)
        }

        when (result) {
            is RepositoryResult.Success -> {
                // dayCopy — это обновлённый день с новой едой внутри
                val dayCopy = result.data
                Log.d("DETAIL_VM", "save success, dayCopy.breakfast.size=${dayCopy.breakfast.size}")
                _state.value = _state.value.copy(isSaved = true)
                // Передаём dayCopy наружу — FoodDetailPage вызовет syncDayToServer(dayCopy)
                onSaved(dayCopy)
            }
            is RepositoryResult.Error -> {
                _state.value = _state.value.copy(error = result.error.message)
            }
            else -> {}
        }
    }
}