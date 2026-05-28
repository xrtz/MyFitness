package com.example.myfitness.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myfitness.R
import com.example.myfitness.di.ViewModelFactory
import com.example.myfitness.presentation.pages.HomePage
import com.example.myfitness.presentation.pages.ProfilePage
import com.example.myfitness.presentation.viewmodel.HomeViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier         : Modifier = Modifier,
    navController    : NavController,
    homeViewModel    : HomeViewModel,
    viewModelFactory : ViewModelFactory,
    onLogout         : () -> Unit = {}
) {
    val navItemList = listOf(
        NavItem(stringResource(R.string.label_nav_home),    Icons.Default.Home),
        NavItem(stringResource(R.string.label_nav_profile), Icons.Default.AccountCircle)
    )

    var selectedBar    by rememberSaveable { mutableStateOf(0) }
    var showDatePicker by remember { mutableStateOf(false) }

    val selectedDate  by homeViewModel.selectedDate.collectAsState()
    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEE, d MMMM", Locale("ru")) }

    Scaffold(
        topBar = {
            if (selectedBar == 0) {
                TopAppBar(
                    title = {
                        Text(
                            text       = selectedDate.format(dateFormatter)
                                .replaceFirstChar { it.uppercase() },
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 18.sp
                        )
                    },
                    actions = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Text(text = "📅", fontSize = 22.sp)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor    = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = index == selectedBar,
                        onClick  = { selectedBar = index },
                        icon     = { Icon(navItem.icon, contentDescription = navItem.label) },
                        label    = { Text(navItem.label, fontSize = 11.sp) }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedBar) {
            0 -> HomePage(
                modifier      = modifier.padding(innerPadding),
                navController = navController,
                homeViewModel = homeViewModel
            )
            1 -> ProfilePage(
                modifier         = modifier.padding(innerPadding),
                viewModelFactory = viewModelFactory,
                onSaved          = { homeViewModel.loadUserGoal() },
                onLogout         = onLogout
            )
        }
    }

    if (showDatePicker) {
        DatePickerModal(
            initialDate    = selectedDate,
            onDateSelected = { homeViewModel.selectDate(it) },
            onDismiss      = { showDatePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerModal(
    initialDate    : LocalDate,
    onDateSelected : (LocalDate) -> Unit,
    onDismiss      : () -> Unit
) {
    val initialMillis   = initialDate.toEpochDay() * 86_400_000L
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis
    )

    val context   = LocalContext.current
    val ruContext = remember(context) {
        val config = Configuration(context.resources.configuration)
        config.setLocale(java.util.Locale("ru"))
        context.createConfigurationContext(config)
    }

    CompositionLocalProvider(LocalContext provides ruContext) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(LocalDate.ofEpochDay(millis / 86_400_000L))
                    }
                    onDismiss()
                }) {
                    Text("ОК")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Отмена") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

data class NavItem(val label: String, val icon: ImageVector)
