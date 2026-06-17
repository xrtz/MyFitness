package com.example.myfitness.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfitness.domain.function.Util
import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.models.FoodModel
import com.example.myfitness.domain.usecase.DeleteFoodItemUseCase
import com.example.myfitness.domain.usecase.GetCaloriesGoalUseCase
import com.example.myfitness.domain.usecase.LoadDayUseCase
import com.example.myfitness.domain.usecase.SyncDayUseCase
import com.example.myfitness.domain.usecase.SyncPendingDaysUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class HomeViewModel(
    private val loadDayUseCase: LoadDayUseCase,
    private val deleteFoodItemUseCase: DeleteFoodItemUseCase,
    private val syncDayUseCase: SyncDayUseCase,
    private val getCaloriesGoalUseCase: GetCaloriesGoalUseCase,
    private val syncPendingDaysUseCase: SyncPendingDaysUseCase
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _dayFoodItem = MutableStateFlow<DayFoodItemModel?>(null)
    val dayFoodItem: StateFlow<DayFoodItemModel?> = _dayFoodItem

    private val _caloriesGoal = MutableStateFlow(2000f)
    val caloriesGoal: StateFlow<Float> = _caloriesGoal

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var loadJob: Job? = null

    init {
        viewModelScope.launch {
            try { syncPendingDaysUseCase.execute() } catch (_: Exception) {}
            loadDay(LocalDate.now())
        }
        loadUserGoal()
    }

    fun loadUserGoal() {
        viewModelScope.launch {
            try {
                _caloriesGoal.value = getCaloriesGoalUseCase.execute()
            } catch (_: Exception) {
            }
        }
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        loadDay(date)
    }

    fun reloadCurrentDay() {
        loadDay(_selectedDate.value)
    }

    fun syncPendingDays() {
        viewModelScope.launch {
            try {
                syncPendingDaysUseCase.execute()
                reloadCurrentDay()
            } catch (_: Exception) {
            }
        }
    }

    fun updateDayState(day: DayFoodItemModel) {
        _dayFoodItem.value = day
    }

    private fun loadDay(date: LocalDate) {
        val epochDay = Util.dayToModel(date).day
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _isLoading.value = true
            try {
                loadDayUseCase.execute(epochDay).collect { day ->
                    _dayFoodItem.value = day
                }
            } catch (_: Exception) {
                if (_dayFoodItem.value == null) {
                    _dayFoodItem.value = emptyDay(epochDay)
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteFood(food: FoodModel, day: DayFoodItemModel) {
        viewModelScope.launch {
            try {
                val updatedDay = deleteFoodItemUseCase.execute(food, day)
                _dayFoodItem.value = updatedDay
                val syncedDay = syncDayUseCase.execute(updatedDay)
                _dayFoodItem.value = syncedDay
            } catch (_: Exception) {
            }
        }
    }

    fun getStartOfWeek(date: LocalDate): LocalDate =
        date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    private fun emptyDay(epochDay: Int) = DayFoodItemModel(
        id = 0,
        userId = 0,
        date = epochDay,
        calories = 0f,
        protein = 0f,
        fats = 0f,
        carbohydrates = 0f,
        breakfast = emptyList(),
        lunch = emptyList(),
        dinner = emptyList(),
        snacks = emptyList()
    )
}
