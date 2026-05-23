package com.example.myfitness.presentation.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myfitness.R
import com.example.myfitness.presentation.components.DayInfoView
import com.example.myfitness.presentation.components.TypeOfMealView
import com.example.myfitness.presentation.components.WeekView
import com.example.myfitness.presentation.viewmodel.HomeViewModel

@Composable
fun HomePage(
    modifier      : Modifier = Modifier,
    navController : NavController,
    homeViewModel : HomeViewModel
) {
    val selectedDate by homeViewModel.selectedDate.collectAsState()
    val dayFoodItem  by homeViewModel.dayFoodItem.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        WeekView(viewModel = homeViewModel)

        Text(
            text     = selectedDate.toString(),
            modifier = Modifier.padding(vertical = 4.dp)
        )

        dayFoodItem?.let { day ->
            DayInfoView(dayFoodItem = day)

            TypeOfMealView(
                name          = stringResource(R.string.breakfast),
                foods         = day.breakfast,
                typeOfMeal    = "breakfast",
                selectedDate  = selectedDate,
                onAddClick    = { navController.navigate("food_detail/breakfast") },
                onEditClick   = { food -> navController.navigate("food_detail/breakfast?foodId=${food.id}") },
                onDeleteClick = {  }
            )

            TypeOfMealView(
                name          = stringResource(R.string.lunch),
                foods         = day.lunch,
                typeOfMeal    = "lunch",
                selectedDate  = selectedDate,
                onAddClick    = { navController.navigate("food_detail/lunch") },
                onEditClick   = { food -> navController.navigate("food_detail/lunch?foodId=${food.id}") },
                onDeleteClick = {}
            )

            TypeOfMealView(
                name          = stringResource(R.string.dinner),
                foods         = day.dinner,
                typeOfMeal    = "dinner",
                selectedDate  = selectedDate,
                onAddClick    = { navController.navigate("food_detail/dinner") },
                onEditClick   = { food -> navController.navigate("food_detail/dinner?foodId=${food.id}") },
                onDeleteClick = {  }
            )

            TypeOfMealView(
                name          = stringResource(R.string.piece),
                foods         = day.snacks,
                typeOfMeal    = "snacks",
                selectedDate  = selectedDate,
                onAddClick    = { navController.navigate("food_detail/snacks") },
                onEditClick   = { food -> navController.navigate("food_detail/snacks?foodId=${food.id}") },
                onDeleteClick = {  }
            )
        }
    }
}