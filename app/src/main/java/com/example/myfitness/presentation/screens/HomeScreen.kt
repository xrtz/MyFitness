package com.example.myfitness.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.myfitness.R
import com.example.myfitness.di.ViewModelFactory
import com.example.myfitness.presentation.pages.HomePage
import com.example.myfitness.presentation.pages.ProfilePage
import com.example.myfitness.presentation.viewmodel.HomeViewModel

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
    var selectedBar by rememberSaveable { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = index == selectedBar,
                        onClick  = { selectedBar = index },
                        icon     = { Icon(navItem.icon, contentDescription = navItem.label) },
                        label    = { Text(navItem.label) }
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
}

data class NavItem(val label: String, val icon: ImageVector)