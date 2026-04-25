package com.example.myfitness.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfitness.domain.models.FoodModel
import java.time.LocalDate

@Composable
fun TypeOfMealView(
    name          : String,
    foods         : List<FoodModel>,
    typeOfMeal    : String,
    selectedDate  : LocalDate,
    onAddClick    : () -> Unit,
    onEditClick   : (FoodModel) -> Unit,
    onDeleteClick : (FoodModel) -> Unit
) {
    var expanded by remember(selectedDate) { mutableStateOf(false) }

    val totalCalories = foods.sumOf { it.calories }
    val totalProtein  = foods.sumOf { it.protein.toDouble() }.toFloat()
    val totalFats     = foods.sumOf { it.fats.toDouble() }.toFloat()
    val totalCarbs    = foods.sumOf { it.carbohydrates.toDouble() }.toFloat()

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(
                        text       = name,
                        color      = Color.Black,
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        MacroChip(label = "$totalCalories ккал")
                        MacroChip(label = "Б ${String.format("%.1f", totalProtein)}")
                        MacroChip(label = "Ж ${String.format("%.1f", totalFats)}")
                        MacroChip(label = "У ${String.format("%.1f", totalCarbs)}")
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text     = if (expanded) "▲" else "▼",
                        fontSize = 12.sp,
                        color    = Color.Gray,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    OutlinedButton(
                        modifier = Modifier.size(48.dp),
                        onClick  = onAddClick
                    ) {
                        Text(text = "+", fontSize = 18.sp)
                    }
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter   = expandVertically(),
                exit    = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    if (foods.isEmpty()) {
                        Text(
                            text     = "Нет добавленных продуктов",
                            color    = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    } else {
                        foods.forEach { food ->
                            FoodItemRow(
                                food          = food,
                                onEditClick   = { onEditClick(food) },
                                onDeleteClick = { onDeleteClick(food) }
                            )
                            Divider(color = Color.LightGray, thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun FoodItemRow(
    food          : FoodModel,
    onEditClick   : () -> Unit,
    onDeleteClick : () -> Unit
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = food.name,
                fontSize   = 15.sp,
                fontWeight = FontWeight.Medium,
                color      = Color.Black
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "${food.weight}г",                               fontSize = 12.sp, color = Color.Gray)
                Text(text = "• ${food.calories} ккал",                       fontSize = 12.sp, color = Color.Gray)
                Text(text = "Б${String.format("%.1f", food.protein)}",       fontSize = 12.sp, color = Color.Gray)
                Text(text = "Ж${String.format("%.1f", food.fats)}",          fontSize = 12.sp, color = Color.Gray)
                Text(text = "У${String.format("%.1f", food.carbohydrates)}", fontSize = 12.sp, color = Color.Gray)
            }
        }

        Row {
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector     = Icons.Default.Edit,
                    contentDescription = "Редактировать",
                    tint            = Color.Gray
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector        = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint               = Color(0xFFE57373)
                )
            }
        }
    }
}


@Composable
fun MacroChip(label: String) {
    Card(
        modifier = Modifier.wrapContentHeight(),
        colors   = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Text(
            text     = label,
            color    = Color.DarkGray,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}