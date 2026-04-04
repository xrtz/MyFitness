package com.example.myfitness.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myfitness.R
import com.example.myfitness.domain.function.Util
import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.repository.FoodRepository
import com.example.myfitness.domain.usecase.GetDayFoodItemUseCase
import java.time.LocalDate

@Composable
fun DayFoodItemView(date: LocalDate, foodUseCase: GetDayFoodItemUseCase, utilit: Util) {
    val dayFoodItem: DayFoodItemModel = foodUseCase.execute( utilit.dayToModel(date)) // во вьюмодель засунуть
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
//        Text(text = dayFoodItem.toString())
        DayInfoView(dayFoodItem)
        TypeOfMealView(stringResource(R.string.breakfast), dayFoodItem, 1)
        TypeOfMealView(stringResource(R.string.lunch), dayFoodItem, 2)
        TypeOfMealView(stringResource(R.string.dinner), dayFoodItem, 3)
        TypeOfMealView(stringResource(R.string.piece), dayFoodItem, 4)
    }


}