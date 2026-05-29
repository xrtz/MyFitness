package com.example.myfitness.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myfitness.domain.repository.FoodRepository
import com.example.myfitness.domain.repository.UserRepository
import com.example.myfitness.domain.usecase.AddFoodItemUseCase
import com.example.myfitness.domain.usecase.ChangeFoodItemUseCase
import com.example.myfitness.domain.usecase.DeleteFoodItemUseCase
import com.example.myfitness.domain.usecase.GetCaloriesGoalUseCase
import com.example.myfitness.domain.usecase.GetUserProfileUseCase
import com.example.myfitness.domain.usecase.LoadDayUseCase
import com.example.myfitness.domain.usecase.LoginUseCase
import com.example.myfitness.domain.usecase.RegisterUseCase
import com.example.myfitness.domain.usecase.SyncDayUseCase
import com.example.myfitness.domain.usecase.UpdateUserProfileUseCase
import com.example.myfitness.presentation.viewmodel.AuthViewModel
import com.example.myfitness.presentation.viewmodel.FoodDetailViewModel
import com.example.myfitness.presentation.viewmodel.HomeViewModel
import com.example.myfitness.presentation.viewmodel.ProfileViewModel
import javax.inject.Inject

class ViewModelFactory @Inject constructor(
    private val foodRepository : FoodRepository,
    private val userRepository : UserRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(AuthViewModel::class.java) ->
            AuthViewModel(
                loginUseCase    = LoginUseCase(userRepository),
                registerUseCase = RegisterUseCase(userRepository)
            ) as T

        modelClass.isAssignableFrom(HomeViewModel::class.java) ->
            HomeViewModel(
                loadDayUseCase         = LoadDayUseCase(foodRepository),
                deleteFoodItemUseCase  = DeleteFoodItemUseCase(foodRepository),
                syncDayUseCase         = SyncDayUseCase(foodRepository),
                getCaloriesGoalUseCase = GetCaloriesGoalUseCase(userRepository)
            ) as T

        modelClass.isAssignableFrom(FoodDetailViewModel::class.java) ->
            FoodDetailViewModel(
                addFoodItemUseCase    = AddFoodItemUseCase(foodRepository),
                changeFoodItemUseCase = ChangeFoodItemUseCase(foodRepository),
                syncDayUseCase        = SyncDayUseCase(foodRepository)
            ) as T

        modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
            ProfileViewModel(
                getUserProfileUseCase    = GetUserProfileUseCase(userRepository),
                updateUserProfileUseCase = UpdateUserProfileUseCase(userRepository)
            ) as T

        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
