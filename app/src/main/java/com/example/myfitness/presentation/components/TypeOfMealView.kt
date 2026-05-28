package com.example.myfitness.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Surface(
        modifier        = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape           = RoundedCornerShape(16.dp),
        color           = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier.weight(1f)
                ) {
                    Column {
                        Text(
                            text       = name,
                            color      = MaterialTheme.colorScheme.onSurface,
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(3.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            MacroChip(label = "$totalCalories ккал", isPrimary = true)
                            MacroChip(label = "Б ${String.format("%.1f", totalProtein)}")
                            MacroChip(label = "Ж ${String.format("%.1f", totalFats)}")
                            MacroChip(label = "У ${String.format("%.1f", totalCarbs)}")
                        }
                    }
                }

                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text     = if (expanded) "▲" else "▼",
                        fontSize = 11.sp,
                        color    = MaterialTheme.colorScheme.outline
                    )
                    FilledTonalIconButton(
                        onClick  = onAddClick,
                        modifier = Modifier.size(36.dp),
                        colors   = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor   = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Add,
                            contentDescription = "Добавить",
                            modifier           = Modifier.size(20.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter   = expandVertically(),
                exit    = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                ) {
                    HorizontalDivider(
                        color     = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 1.dp
                    )
                    if (foods.isEmpty()) {
                        Text(
                            text     = "Нет добавленных продуктов",
                            color    = MaterialTheme.colorScheme.outline,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    } else {
                        foods.forEach { food ->
                            FoodItemRow(
                                food          = food,
                                onEditClick   = { onEditClick(food) },
                                onDeleteClick = { onDeleteClick(food) }
                            )
                            HorizontalDivider(
                                color     = MaterialTheme.colorScheme.outlineVariant,
                                thickness = 0.5.dp
                            )
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
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = food.name,
                fontSize   = 15.sp,
                fontWeight = FontWeight.Medium,
                color      = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(2.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "${food.weight}г",                               fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                Text(text = "•",                                              fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                Text(
                    text       = "${food.calories} ккал",
                    fontSize   = 12.sp,
                    color      = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                Text(text = "Б${String.format("%.1f", food.protein)}",       fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                Text(text = "Ж${String.format("%.1f", food.fats)}",          fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                Text(text = "У${String.format("%.1f", food.carbohydrates)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
            }
        }

        Row {
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector        = Icons.Default.Edit,
                    contentDescription = "Редактировать",
                    tint               = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector        = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint               = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun MacroChip(label: String, isPrimary: Boolean = false) {
    Surface(
        modifier = Modifier.wrapContentHeight(),
        shape    = RoundedCornerShape(6.dp),
        color    = if (isPrimary) MaterialTheme.colorScheme.primaryContainer
                   else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text       = label,
            color      = if (isPrimary) MaterialTheme.colorScheme.onPrimaryContainer
                         else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize   = 10.sp,
            fontWeight = if (isPrimary) FontWeight.SemiBold else FontWeight.Normal,
            modifier   = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}
