package com.example.myfitness.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfitness.data.remote.ApiService
import com.example.myfitness.data.remote.dto.DayFoodRequest
import com.example.myfitness.data.remote.dto.DayFoodResponse
import com.example.myfitness.data.remote.dto.FoodItemDto
import com.example.myfitness.domain.function.Util
import com.example.myfitness.domain.models.DateModel
import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.models.FoodModel
import com.example.myfitness.domain.repository.FoodRepository
import com.example.myfitness.domain.usecase.AddFoodItemUseCase
import com.example.myfitness.domain.usecase.DeleteFoodItemUseCase
import com.example.myfitness.domain.usecase.GetDayFoodItemUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class HomeViewModel(
    private val getDayFoodItemUseCase : GetDayFoodItemUseCase,
    private val addFoodItemUseCase    : AddFoodItemUseCase,
    private val deleteFoodItemUseCase : DeleteFoodItemUseCase,
    private val apiService            : ApiService,
    private val foodRepository        : FoodRepository
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

    init {
        loadDay(LocalDate.now())
        loadUserGoal()
    }

    fun loadUserGoal() {
        viewModelScope.launch {
            try {
                val response = apiService.getMe()
                if (response.isSuccessful) {
                    val user = response.body() ?: return@launch
                    val bmr = Util.howMuchNeedCalories(
                        weight = user.weight.coerceAtLeast(1f),
                        height = user.height.coerceAtLeast(1f),
                        age    = 25,
                        gender = user.gender
                    )
                    _caloriesGoal.value = when {
                        user.target.contains("похудение", ignoreCase = true) -> bmr - 500f
                        user.target.contains("набор",     ignoreCase = true) -> bmr + 300f
                        else                                                  -> bmr
                    }.coerceAtLeast(1200f)
                }
            } catch (_: Exception) { }
        }
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        loadDay(date)
    }

    fun reloadCurrentDay() {
        loadDay(_selectedDate.value)
    }

    private fun loadDay(date: LocalDate) {
        val epochDay = Util.dayToModel(date).day

        viewModelScope.launch {
            _isLoading.value = true

            // 1. Сначала показываем кеш из Room
            withContext(Dispatchers.IO) {
                val cached = foodRepository.getDayFoodItems(DateModel(epochDay))
                val hasFood = cached.breakfast.isNotEmpty() || cached.lunch.isNotEmpty() ||
                              cached.dinner.isNotEmpty()    || cached.snacks.isNotEmpty()
                if (hasFood) {
                    _dayFoodItem.value = cached
                } else if (_dayFoodItem.value?.date != epochDay) {
                    _dayFoodItem.value = emptyDay(epochDay)
                }
            }

            // 2. Пытаемся получить свежие данные с сервера
            try {
                val response = apiService.getDay(epochDay)
                if (response.isSuccessful && response.body() != null) {
                    val dayFromApi = response.body()!!.toDomain(epochDay)
                    _dayFoodItem.value = dayFromApi
                    // Сохраняем в Room кеш
                    withContext(Dispatchers.IO) {
                        foodRepository.updateDayFoodItems(dayFromApi)
                    }
                }
            } catch (e: Exception) {
                Log.d("HOME_VM", "API unavailable, showing cache: ${e.message}")
                if (_dayFoodItem.value == null) {
                    _dayFoodItem.value = emptyDay(epochDay)
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun syncDayToServer(day: DayFoodItemModel) {
        viewModelScope.launch {
            try {
                val allItems = (day.breakfast + day.lunch + day.dinner + day.snacks)
                    .map { it.toDto() }

                Log.d("HOME_VM", "syncDayToServer: items=${allItems.size}")

                val response = apiService.saveDay(
                    DayFoodRequest(
                        date          = day.date,
                        calories      = day.calories,
                        protein       = day.protein,
                        fats          = day.fats,
                        carbohydrates = day.carbohydrates,
                        foodItems     = allItems
                    )
                )

                if (response.isSuccessful) {
                    response.body()?.let { serverDay ->
                        val domainDay = serverDay.toDomain(day.date)
                        _dayFoodItem.value = domainDay
                        // Сохраняем ответ сервера в Room (с актуальными ID)
                        withContext(Dispatchers.IO) {
                            foodRepository.updateDayFoodItems(domainDay)
                        }
                    }
                } else {
                    // Сервер недоступен — сохраняем текущее состояние в Room
                    withContext(Dispatchers.IO) {
                        foodRepository.updateDayFoodItems(day)
                    }
                }
            } catch (e: Exception) {
                Log.e("HOME_VM", "sync error ${e.message}")
                // Нет интернета — сохраняем в Room для офлайн доступа
                withContext(Dispatchers.IO) {
                    foodRepository.updateDayFoodItems(day)
                }
            }
        }
    }

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

    private fun DayFoodResponse.toDomain(epochDay: Int) = DayFoodItemModel(
        id = id.toInt(),
        userId = 0,
        date = epochDay,
        calories = calories,
        protein = protein,
        fats = fats,
        carbohydrates = carbohydrates,
        breakfast = breakfast.map { it.toDomain() },
        lunch = lunch.map { it.toDomain() },
        dinner = dinner.map { it.toDomain() },
        snacks = snacks.map { it.toDomain() }
    )

    private fun FoodItemDto.toDomain() = FoodModel(
        id = id.toInt(),
        name = name,
        weight = weight,
        calories = calories,
        typeOfMeal = typeOfMeal,
        protein = protein,
        fats = fats,
        carbohydrates = carbohydrates
    )

    fun deleteFood(food: FoodModel, day: DayFoodItemModel) {
        val updatedBreakfast = if (food.typeOfMeal == "breakfast") day.breakfast.filter { it.id != food.id } else day.breakfast
        val updatedLunch     = if (food.typeOfMeal == "lunch")     day.lunch.filter     { it.id != food.id } else day.lunch
        val updatedDinner    = if (food.typeOfMeal == "dinner")    day.dinner.filter    { it.id != food.id } else day.dinner
        val updatedSnacks    = if (food.typeOfMeal == "snacks")    day.snacks.filter    { it.id != food.id } else day.snacks
        val allRemaining     = updatedBreakfast + updatedLunch + updatedDinner + updatedSnacks
        val updatedDay = day.copy(
            calories      = allRemaining.sumOf { it.calories }.toFloat(),
            protein       = allRemaining.sumOf { it.protein.toDouble() }.toFloat(),
            fats          = allRemaining.sumOf { it.fats.toDouble() }.toFloat(),
            carbohydrates = allRemaining.sumOf { it.carbohydrates.toDouble() }.toFloat(),
            breakfast     = updatedBreakfast,
            lunch         = updatedLunch,
            dinner        = updatedDinner,
            snacks        = updatedSnacks
        )
        _dayFoodItem.value = updatedDay
        syncDayToServer(updatedDay)
    }

    fun getStartOfWeek(date: LocalDate): LocalDate = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    private fun FoodModel.toDto() = FoodItemDto(
        id = id.toLong(),
        name = name,
        weight = weight,
        calories = calories,
        typeOfMeal = typeOfMeal,
        protein = protein,
        fats = fats,
        carbohydrates = carbohydrates
    )
}
