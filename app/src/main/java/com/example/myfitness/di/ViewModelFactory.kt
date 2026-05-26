package com.example.myfitness.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myfitness.data.remote.ApiService
import com.example.myfitness.domain.repository.FoodRepository
import com.example.myfitness.domain.usecase.AddFoodItemUseCase
import com.example.myfitness.domain.usecase.ChangeFoodItemUseCase
import com.example.myfitness.domain.usecase.DeleteFoodItemUseCase
import com.example.myfitness.domain.usecase.GetDayFoodItemUseCase
import com.example.myfitness.presentation.viewmodel.AuthViewModel
import com.example.myfitness.presentation.viewmodel.FoodDetailViewModel
import com.example.myfitness.presentation.viewmodel.HomeViewModel
import com.example.myfitness.presentation.viewmodel.ProfileViewModel
import javax.inject.Inject

class ViewModelFactory @Inject constructor(
    private val foodRepository : FoodRepository,
    private val apiService     : ApiService
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(AuthViewModel::class.java) ->
            AuthViewModel(apiService) as T

        modelClass.isAssignableFrom(HomeViewModel::class.java) ->
            HomeViewModel(
                getDayFoodItemUseCase = GetDayFoodItemUseCase(foodRepository),
                addFoodItemUseCase    = AddFoodItemUseCase(foodRepository),
                deleteFoodItemUseCase = DeleteFoodItemUseCase(foodRepository),
                apiService            = apiService,
                foodRepository        = foodRepository
            ) as T

        modelClass.isAssignableFrom(FoodDetailViewModel::class.java) ->
            FoodDetailViewModel(
                addFoodItemUseCase    = AddFoodItemUseCase(foodRepository),
                changeFoodItemUseCase = ChangeFoodItemUseCase(foodRepository)
            ) as T

        modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
            ProfileViewModel(apiService) as T

        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}