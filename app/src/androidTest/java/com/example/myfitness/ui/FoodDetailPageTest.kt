package com.example.myfitness.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myfitness.FakeFoodRepository
import com.example.myfitness.FakeUserRepository
import com.example.myfitness.domain.usecase.AddFoodItemUseCase
import com.example.myfitness.domain.usecase.ChangeFoodItemUseCase
import com.example.myfitness.domain.usecase.DeleteFoodItemUseCase
import com.example.myfitness.domain.usecase.GetCaloriesGoalUseCase
import com.example.myfitness.domain.usecase.LoadDayUseCase
import com.example.myfitness.domain.usecase.SyncDayUseCase
import com.example.myfitness.domain.usecase.SyncPendingDaysUseCase
import com.example.myfitness.presentation.pages.FoodDetailPage
import com.example.myfitness.presentation.viewmodel.FoodDetailViewModel
import com.example.myfitness.presentation.viewmodel.HomeViewModel
import com.example.myfitness.ui.theme.MyFitnessTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FoodDetailPageTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun makeHomeViewModel(): HomeViewModel {
        val repo = FakeFoodRepository()
        val userRepo = FakeUserRepository()
        return HomeViewModel(
            loadDayUseCase = LoadDayUseCase(repo),
            deleteFoodItemUseCase = DeleteFoodItemUseCase(repo),
            syncDayUseCase = SyncDayUseCase(repo),
            getCaloriesGoalUseCase = GetCaloriesGoalUseCase(userRepo),
            syncPendingDaysUseCase = SyncPendingDaysUseCase(repo)
        )
    }

    private fun makeFoodDetailViewModel(): FoodDetailViewModel {
        val repo = FakeFoodRepository()
        return FoodDetailViewModel(
            addFoodItemUseCase = AddFoodItemUseCase(repo),
            changeFoodItemUseCase = ChangeFoodItemUseCase(repo),
            syncDayUseCase = SyncDayUseCase(repo)
        )
    }

    @Test
    fun отображаются_поля_ввода_еды() {
        composeTestRule.setContent {
            MyFitnessTheme {
                FoodDetailPage(
                    typeOfMeal = "breakfast",
                    homeViewModel = makeHomeViewModel(),
                    foodDetailViewModel = makeFoodDetailViewModel(),
                    onBack = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Название продукта").assertIsDisplayed()
        composeTestRule.onNodeWithText("Вес (г)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Калории (ккал)").assertIsDisplayed()
    }

    @Test
    fun кнопка_добавить_отображается_при_новом_продукте() {
        composeTestRule.setContent {
            MyFitnessTheme {
                FoodDetailPage(
                    typeOfMeal = "lunch",
                    foodId = null,
                    homeViewModel = makeHomeViewModel(),
                    foodDetailViewModel = makeFoodDetailViewModel(),
                    onBack = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Добавить").assertIsDisplayed()
    }

    @Test
    fun ошибка_отображается_при_сохранении_без_названия() {
        composeTestRule.setContent {
            MyFitnessTheme {
                FoodDetailPage(
                    typeOfMeal = "breakfast",
                    homeViewModel = makeHomeViewModel(),
                    foodDetailViewModel = makeFoodDetailViewModel(),
                    onBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Добавить").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Введите название").assertIsDisplayed()
    }

    @Test
    fun заголовок_завтрак_отображается_в_топбаре() {
        composeTestRule.setContent {
            MyFitnessTheme {
                FoodDetailPage(
                    typeOfMeal = "breakfast",
                    homeViewModel = makeHomeViewModel(),
                    foodDetailViewModel = makeFoodDetailViewModel(),
                    onBack = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Завтрак").assertIsDisplayed()
    }

    @Test
    fun ввод_названия_продукта_обновляет_поле() {
        composeTestRule.setContent {
            MyFitnessTheme {
                FoodDetailPage(
                    typeOfMeal = "dinner",
                    homeViewModel = makeHomeViewModel(),
                    foodDetailViewModel = makeFoodDetailViewModel(),
                    onBack = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Название продукта").performTextInput("Рис")
        composeTestRule.onNodeWithText("Рис").assertIsDisplayed()
    }
}
