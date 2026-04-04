package com.example.myfitness.presentation.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myfitness.data.repository.FoodRepositoryImpl
import com.example.myfitness.domain.function.Util
import com.example.myfitness.domain.repository.FoodRepository
import com.example.myfitness.domain.usecase.GetDayFoodItemUseCase
import com.example.myfitness.presentation.components.DayFoodItemView
import com.example.myfitness.presentation.components.WeekView
import com.example.myfitness.presentation.viewmodel.CalendarViewModel

@Composable
fun HomePage(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        val viewModel: CalendarViewModel = viewModel()
        val foodRepo: FoodRepository = FoodRepositoryImpl()
        val utilit: Util = Util
        val foodUseCase: GetDayFoodItemUseCase = GetDayFoodItemUseCase()
        val selectedDate by viewModel.selectedDate.collectAsState()
        WeekView(viewModel)
        Text(text = selectedDate.toString())
        DayFoodItemView(selectedDate, foodUseCase, utilit)
    }
}