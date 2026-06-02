package com.example.myfitness.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myfitness.FakeFoodRepository
import com.example.myfitness.FakeUserRepository
import com.example.myfitness.domain.usecase.DeleteFoodItemUseCase
import com.example.myfitness.domain.usecase.GetCaloriesGoalUseCase
import com.example.myfitness.domain.usecase.LoadDayUseCase
import com.example.myfitness.domain.usecase.SyncDayUseCase
import com.example.myfitness.domain.usecase.SyncPendingDaysUseCase
import com.example.myfitness.presentation.pages.HomePage
import com.example.myfitness.presentation.viewmodel.HomeViewModel
import com.example.myfitness.ui.theme.MyFitnessTheme
import com.example.myfitness.uiTestDay
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomePageTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun makeViewModel(repo: FakeFoodRepository = FakeFoodRepository()): HomeViewModel {
        val userRepo = FakeUserRepository()
        return HomeViewModel(
            loadDayUseCase = LoadDayUseCase(repo),
            deleteFoodItemUseCase = DeleteFoodItemUseCase(repo),
            syncDayUseCase = SyncDayUseCase(repo),
            getCaloriesGoalUseCase = GetCaloriesGoalUseCase(userRepo),
            syncPendingDaysUseCase = SyncPendingDaysUseCase(repo)
        )
    }

    @Test
    fun отображаются_секции_приёмов_пищи() {
        composeTestRule.setContent {
            MyFitnessTheme {
                HomePage(
                    navController = rememberNavController(),
                    homeViewModel = makeViewModel()
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Завтрак").assertIsDisplayed()
        composeTestRule.onNodeWithText("Обед").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ужин").assertIsDisplayed()
    }

    @Test
    fun продукты_завтрака_отображаются_в_списке() {
        val repo = FakeFoodRepository(uiTestDay())
        composeTestRule.setContent {
            MyFitnessTheme {
                HomePage(
                    navController = rememberNavController(),
                    homeViewModel = makeViewModel(repo)
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Завтрак").performClick()
        composeTestRule.onNodeWithText("Овсянка").assertIsDisplayed()
    }

    @Test
    fun продукты_обеда_отображаются_в_списке() {
        val repo = FakeFoodRepository(uiTestDay())
        composeTestRule.setContent {
            MyFitnessTheme {
                HomePage(
                    navController = rememberNavController(),
                    homeViewModel = makeViewModel(repo)
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Обед").performClick()
        composeTestRule.onNodeWithText("Курица").assertIsDisplayed()
    }

    @Test
    fun пустой_день_показывает_секции_без_продуктов() {
        val emptyRepo = FakeFoodRepository(
            uiTestDay(breakfast = emptyList(), lunch = emptyList())
        )
        composeTestRule.setContent {
            MyFitnessTheme {
                HomePage(
                    navController = rememberNavController(),
                    homeViewModel = makeViewModel(emptyRepo)
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Завтрак").assertIsDisplayed()
        composeTestRule.onNodeWithText("Овсянка").assertDoesNotExist()
    }
}
