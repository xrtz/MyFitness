package com.example.myfitness.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import com.example.myfitness.presentation.model.CommonFoodPresets
import com.example.myfitness.presentation.model.FoodPreset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodPickerBottomSheet(
    history: List<FoodModel>,
    onSelectPreset: (FoodPreset) -> Unit,
    onSelectFromHistory: (FoodModel) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    val uniqueHistory = remember(history) {
        history.distinctBy { it.name.lowercase().trim() }
    }

    val filteredHistory = remember(query, uniqueHistory) {
        if (query.isBlank()) uniqueHistory
        else uniqueHistory.filter { it.name.contains(query, ignoreCase = true) }
    }

    val filteredPresets = remember(query) {
        if (query.isBlank()) CommonFoodPresets
        else CommonFoodPresets.filter { it.name.contains(query, ignoreCase = true) }
    }

    val hasResults = filteredHistory.isNotEmpty() || filteredPresets.isNotEmpty()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Выбрать продукт",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Поиск...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = query.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Очистить")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(Modifier.height(8.dp))

            LazyColumn(contentPadding = PaddingValues(bottom = 32.dp)) {
                if (filteredHistory.isNotEmpty()) {
                    item { PickerSectionHeader(title = "Добавлено сегодня") }
                    items(filteredHistory, key = { "h_${it.id}" }) { food ->
                        HistoryFoodItem(food = food, onClick = { onSelectFromHistory(food) })
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 16.dp),
                            thickness = 0.5.dp
                        )
                    }
                }

                if (filteredPresets.isNotEmpty()) {
                    item { PickerSectionHeader(title = "Продукты") }
                    items(filteredPresets, key = { "p_${it.name}" }) { preset ->
                        PresetFoodItem(preset = preset, onClick = { onSelectPreset(preset) })
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 16.dp),
                            thickness = 0.5.dp
                        )
                    }
                }

                if (!hasResults) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "«$query» не найдено",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Spacer(Modifier.height(12.dp))
                            FilledTonalButton(onClick = onDismiss) {
                                Text("Добавить вручную")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PickerSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
    )
}

@Composable
private fun PresetFoodItem(preset: FoodPreset, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(preset.name, fontWeight = FontWeight.Medium) },
        supportingContent = {
            Text(
                text = "Б ${preset.proteinPer100g}г · Ж ${preset.fatsPer100g}г · У ${preset.carbsPer100g}г  (на 100г)",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.outline
            )
        },
        trailingContent = {
            Text(
                text = "${preset.caloriesPer100g} ккал",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun HistoryFoodItem(food: FoodModel, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(food.name, fontWeight = FontWeight.Medium) },
        supportingContent = {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${food.weight.toInt()}г",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )
                Text("·", fontSize = 12.sp, color = MaterialTheme.colorScheme.outlineVariant)
                Text(
                    text = "Б ${food.protein.toInt()}г · Ж ${food.fats.toInt()}г · У ${food.carbohydrates.toInt()}г",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        },
        trailingContent = {
            Text(
                text = "${food.calories.toInt()} ккал",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}
