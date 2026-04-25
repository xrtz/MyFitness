package com.example.myfitness.presentation.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myfitness.di.ViewModelFactory
import com.example.myfitness.presentation.viewmodel.AuthState
import com.example.myfitness.presentation.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    modifier         : Modifier = Modifier,
    viewModelFactory : ViewModelFactory,
    onAuthSuccess    : () -> Unit
) {
    val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)
    val state by authViewModel.state.collectAsState()

    var isLoginMode by remember { mutableStateOf(true) }

    // Поля
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name     by remember { mutableStateOf("") }
    var weight   by remember { mutableStateOf("") }
    var height   by remember { mutableStateOf("") }
    var gender   by remember { mutableStateOf(1) }
    var target   by remember { mutableStateOf("maintain") }

    // Переход на главный экран после успешной авторизации
    LaunchedEffect(state) {
        if (state is AuthState.Success) {
            authViewModel.resetState()
            onAuthSuccess()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement   = Arrangement.Center,
        horizontalAlignment   = Alignment.CenterHorizontally
    ) {
        Text(
            text     = "MyFitness",
            fontSize = 32.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text     = if (isLoginMode) "Вход" else "Регистрация",
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // ── Общие поля ────────────────────────────────────────

        OutlinedTextField(
            value          = email,
            onValueChange  = { email = it },
            label          = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier       = Modifier.fillMaxWidth(),
            singleLine     = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value                  = password,
            onValueChange          = { password = it },
            label                  = { Text("Пароль") },
            visualTransformation   = PasswordVisualTransformation(),
            keyboardOptions        = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier               = Modifier.fillMaxWidth(),
            singleLine             = true
        )

        // ── Поля только для регистрации ───────────────────────

        AnimatedVisibility(visible = !isLoginMode) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value         = name,
                    onValueChange = { name = it },
                    label         = { Text("Имя") },
                    modifier      = Modifier.fillMaxWidth(),
                    singleLine    = true
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value           = weight,
                        onValueChange   = { weight = it },
                        label           = { Text("Вес (кг)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier        = Modifier.weight(1f),
                        singleLine      = true
                    )
                    OutlinedTextField(
                        value           = height,
                        onValueChange   = { height = it },
                        label           = { Text("Рост (см)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier        = Modifier.weight(1f),
                        singleLine      = true
                    )
                }

                Text(text = "Пол")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = gender == 1,
                        onClick  = { gender = 1 },
                        label    = { Text("Мужской") }
                    )
                    FilterChip(
                        selected = gender == 0,
                        onClick  = { gender = 0 },
                        label    = { Text("Женский") }
                    )
                }

                Text(text = "Цель")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = target == "lose",
                        onClick  = { target = "lose" },
                        label    = { Text("Похудение") }
                    )
                    FilterChip(
                        selected = target == "maintain",
                        onClick  = { target = "maintain" },
                        label    = { Text("Поддержание") }
                    )
                    FilterChip(
                        selected = target == "gain",
                        onClick  = { target = "gain" },
                        label    = { Text("Набор") }
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Кнопка действия ───────────────────────────────────

        if (state is AuthState.Loading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick  = {
                    if (isLoginMode) {
                        authViewModel.login(email, password)
                    } else {
                        authViewModel.register(
                            email    = email,
                            password = password,
                            name     = name,
                            weight   = weight,
                            height   = height,
                            gender   = gender,
                            target   = target
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLoginMode) "Войти" else "Зарегистрироваться")
            }
        }

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = {
                isLoginMode = !isLoginMode
                authViewModel.resetState()
            }
        ) {
            Text(
                if (isLoginMode)
                    "Нет аккаунта? Зарегистрироваться"
                else
                    "Уже есть аккаунт? Войти"
            )
        }

        // ── Ошибка ────────────────────────────────────────────

        if (state is AuthState.Error) {
            Spacer(Modifier.height(8.dp))
            Snackbar(modifier = Modifier.fillMaxWidth()) {
                Text((state as AuthState.Error).message)
            }
        }
    }
}