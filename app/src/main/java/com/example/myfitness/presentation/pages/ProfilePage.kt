package com.example.myfitness.presentation.pages

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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myfitness.di.ViewModelFactory
import com.example.myfitness.presentation.viewmodel.ProfileViewModel

private val TARGET_OPTIONS = listOf("похудение", "поддержание", "набор")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(
    modifier         : Modifier = Modifier,
    viewModelFactory : ViewModelFactory,
    onSaved          : () -> Unit = {}
) {
    val viewModel: ProfileViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onSaved()
    }

    var targetExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Профиль", fontSize = 24.sp)

        OutlinedTextField(
            value         = state.name,
            onValueChange = viewModel::onNameChange,
            label         = { Text("Имя") },
            modifier      = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value           = state.email,
            onValueChange   = viewModel::onEmailChange,
            label           = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier        = Modifier.fillMaxWidth()
        )

        Text(text = "Пол")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = state.gender == "1",
                onClick  = { viewModel.onGenderChange("1") },
                label    = { Text("Мужской") }
            )
            FilterChip(
                selected = state.gender == "0",
                onClick  = { viewModel.onGenderChange("0") },
                label    = { Text("Женский") }
            )
        }

        OutlinedTextField(
            value           = state.weight,
            onValueChange   = viewModel::onWeightChange,
            label           = { Text("Вес (кг)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier        = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value           = state.height,
            onValueChange   = viewModel::onHeightChange,
            label           = { Text("Рост (см)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier        = Modifier.fillMaxWidth()
        )

        ExposedDropdownMenuBox(
            expanded          = targetExpanded,
            onExpandedChange  = { targetExpanded = it },
            modifier          = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value         = state.target.ifEmpty { "Выберите цель" },
                onValueChange = {},
                readOnly      = true,
                label         = { Text("Цель") },
                trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = targetExpanded) },
                modifier      = Modifier
                    .fillMaxWidth()
                    .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded         = targetExpanded,
                onDismissRequest = { targetExpanded = false }
            ) {
                TARGET_OPTIONS.forEach { option ->
                    DropdownMenuItem(
                        text    = { Text(option) },
                        onClick = {
                            viewModel.onTargetChange(option)
                            targetExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            Button(
                onClick  = viewModel::save,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить")
            }
        }

        if (state.isSaved) {
            Snackbar { Text("Профиль сохранён") }
        }
        state.error?.let { Snackbar { Text(it) } }
    }
}
