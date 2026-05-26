package com.example.myfitness.presentation.pages

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myfitness.presentation.viewmodel.FoodDetailViewModel
import com.example.myfitness.presentation.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailPage(
    typeOfMeal          : String,
    foodId              : Int? = null,
    homeViewModel       : HomeViewModel,
    foodDetailViewModel : FoodDetailViewModel,
    onBack              : () -> Unit
) {
    val detailState  by foodDetailViewModel.state.collectAsState()
    val selectedDate by homeViewModel.selectedDate.collectAsState()
    val dayFoodItem  by homeViewModel.dayFoodItem.collectAsState()

    Log.d("DETAIL_PAGE", "selectedDate=$selectedDate dayFoodItem.date=${dayFoodItem?.date}")

    LaunchedEffect(foodId) {
        if (foodId != null) {
            val food = dayFoodItem?.let { day ->
                (day.breakfast + day.lunch + day.dinner + day.snacks).find { it.id == foodId }
            }
            food?.let { foodDetailViewModel.loadFood(it) }
        } else {
            foodDetailViewModel.resetForm()
        }
    }

    LaunchedEffect(detailState.isSaved) {
        if (detailState.isSaved) {
            foodDetailViewModel.resetSaved()
            onBack()
        }
    }

    val mealTitle = when (typeOfMeal) {
        "breakfast" -> "Завтрак"
        "lunch"     -> "Обед"
        "dinner"    -> "Ужин"
        "snacks"    -> "Перекус"
        else        -> typeOfMeal
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$mealTitle · $selectedDate") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value         = detailState.name,
                onValueChange = foodDetailViewModel::onNameChange,
                label         = { Text("Название продукта") },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true
            )
            OutlinedTextField(
                value           = detailState.weight,
                onValueChange   = foodDetailViewModel::onWeightChange,
                label           = { Text("Вес (г)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier        = Modifier.fillMaxWidth(),
                singleLine      = true
            )
            OutlinedTextField(
                value           = detailState.calories,
                onValueChange   = foodDetailViewModel::onCaloriesChange,
                label           = { Text("Калории (ккал)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier        = Modifier.fillMaxWidth(),
                singleLine      = true
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value           = detailState.protein,
                    onValueChange   = foodDetailViewModel::onProteinChange,
                    label           = { Text("Белки (г)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier        = Modifier.weight(1f),
                    singleLine      = true
                )
                OutlinedTextField(
                    value           = detailState.fats,
                    onValueChange   = foodDetailViewModel::onFatsChange,
                    label           = { Text("Жиры (г)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier        = Modifier.weight(1f),
                    singleLine      = true
                )
                OutlinedTextField(
                    value           = detailState.carbohydrates,
                    onValueChange   = foodDetailViewModel::onCarbohydratesChange,
                    label           = { Text("Углев. (г)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier        = Modifier.weight(1f),
                    singleLine      = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val day = dayFoodItem
                    Log.d("DETAIL_PAGE", "Save clicked: day.date=${day?.date}")
                    if (day != null) {
                        foodDetailViewModel.save(
                            typeOfMeal = typeOfMeal,
                            currentDay = day,
                            foodId     = foodId,
                            onSaved    = { dayCopy ->
                                Log.d("DETAIL_PAGE", "onSaved: dayCopy.breakfast=${dayCopy.breakfast.size}")
                                homeViewModel.syncDayToServer(dayCopy)
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled  = dayFoodItem != null
            ) {
                Text(if (foodId == null) "Добавить" else "Сохранить")
            }

            detailState.error?.let { err ->
                Spacer(modifier = Modifier.height(4.dp))
                Snackbar(modifier = Modifier.fillMaxWidth()) { Text(err) }
            }
        }
    }
}