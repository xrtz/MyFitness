package com.example.myfitness.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfitness.R
import com.example.myfitness.domain.models.DayFoodItemModel

@Composable
fun DayInfoView(dayFoodItem: DayFoodItemModel) {
    Card(modifier = Modifier.height(100.dp)
            .fillMaxWidth()
            .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()
            .background(color = Color.White)
            .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.ccal)  + dayFoodItem.calories.toString(), fontSize = 32.sp)

            Column(verticalArrangement = Arrangement.Center) {
                Text(text = stringResource(R.string.protein)  + dayFoodItem.protein.toString())
                Text(text = stringResource(R.string.carbs)  + dayFoodItem.carbohydrates.toString())
            }
            Column(verticalArrangement = Arrangement.Center) {
                Text(text = stringResource(R.string.fats)  + dayFoodItem.fats.toString())
            }
        }
    }
}