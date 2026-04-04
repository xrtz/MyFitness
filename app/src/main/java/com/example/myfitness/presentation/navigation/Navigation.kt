package com.example.myfitness.presentation.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myfitness.presentation.screens.HomeScreen


@Composable
fun Navigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController() // DI
    GlobalNavigation.navController = navController
    val firstpage = "home"
    NavHost(navController = navController,
        startDestination = firstpage
    ) {
        composable("home") {
            HomeScreen(modifier, navController)
        }

    }
}


@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Log.i("123", "loadingScreen")

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

object GlobalNavigation {
    lateinit var navController: NavHostController
}