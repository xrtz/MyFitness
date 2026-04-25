package com.example.myfitness.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myfitness.domain.models.DayFoodItemModel
import com.example.myfitness.domain.models.FoodModel


@Composable
fun DayFoodItemView(
    dayFoodItem: DayFoodItemModel,
    onAddClick: (typeOfMeal: String) -> Unit,
    onEditClick: (FoodModel) -> Unit,
    onDeleteClick: (FoodModel) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        DayInfoView(dayFoodItem = dayFoodItem)
    }
}