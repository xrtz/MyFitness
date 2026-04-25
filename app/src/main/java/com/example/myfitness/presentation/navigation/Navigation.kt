package com.example.myfitness.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myfitness.di.ViewModelFactory
import com.example.myfitness.presentation.pages.FoodDetailPage
import com.example.myfitness.presentation.screens.HomeScreen
import com.example.myfitness.presentation.viewmodel.FoodDetailViewModel
import com.example.myfitness.presentation.viewmodel.HomeViewModel

@Composable
fun Navigation(
    modifier         : Modifier = Modifier,
    viewModelFactory : ViewModelFactory
) {
    val navController = rememberNavController()
    GlobalNavigation.navController = navController

    val homeViewModel       : HomeViewModel       = viewModel(factory = viewModelFactory)
    val foodDetailViewModel : FoodDetailViewModel = viewModel(factory = viewModelFactory)

    NavHost(
        navController    = navController,
        startDestination = "home"
    ) {
        composable(route = "home") {
            HomeScreen(
                modifier         = modifier,
                navController    = navController,
                homeViewModel    = homeViewModel,
                viewModelFactory = viewModelFactory   // передаём для ProfilePage
            )
        }

        composable(
            route = "food_detail/{typeOfMeal}?foodId={foodId}",
            arguments = listOf(
                navArgument("typeOfMeal") { type = NavType.StringType },
                navArgument("foodId") {
                    type         = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val typeOfMeal = backStackEntry.arguments?.getString("typeOfMeal") ?: "breakfast"
            val foodId     = backStackEntry.arguments?.getInt("foodId").takeIf { it != -1 }

            FoodDetailPage(
                typeOfMeal          = typeOfMeal,
                foodId              = foodId,
                homeViewModel       = homeViewModel,
                foodDetailViewModel = foodDetailViewModel,
                onBack              = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

object GlobalNavigation {
    lateinit var navController: NavHostController
}