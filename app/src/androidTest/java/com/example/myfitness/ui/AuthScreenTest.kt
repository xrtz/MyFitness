package com.example.myfitness.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myfitness.FakeFoodRepository
import com.example.myfitness.FakeUserRepository
import com.example.myfitness.di.ViewModelFactory
import com.example.myfitness.presentation.screens.AuthScreen
import com.example.myfitness.ui.theme.MyFitnessTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val factory = ViewModelFactory(FakeFoodRepository(), FakeUserRepository())

    @Test
    fun экран_отображает_название_приложения() {
        composeTestRule.setContent {
            MyFitnessTheme {
                AuthScreen(viewModelFactory = factory, onAuthSuccess = {})
            }
        }
        composeTestRule.onNodeWithText("MyFitness").assertIsDisplayed()
    }

    @Test
    fun экран_отображает_поля_email_и_пароль() {
        composeTestRule.setContent {
            MyFitnessTheme {
                AuthScreen(viewModelFactory = factory, onAuthSuccess = {})
            }
        }
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Пароль").assertIsDisplayed()
    }

    @Test
    fun кнопка_войти_отображается_в_режиме_логина() {
        composeTestRule.setContent {
            MyFitnessTheme {
                AuthScreen(viewModelFactory = factory, onAuthSuccess = {})
            }
        }
        composeTestRule.onNodeWithText("Войти").assertIsDisplayed()
    }

    @Test
    fun переключение_на_регистрацию_показывает_поля_регистрации() {
        composeTestRule.setContent {
            MyFitnessTheme {
                AuthScreen(viewModelFactory = factory, onAuthSuccess = {})
            }
        }
        composeTestRule.onNodeWithText("Нет аккаунта? Зарегистрироваться").performClick()
        composeTestRule.onNodeWithText("Имя").assertIsDisplayed()
        composeTestRule.onNodeWithText("Зарегистрироваться").assertIsDisplayed()
    }

    @Test
    fun переключение_назад_к_логину_скрывает_поля_регистрации() {
        composeTestRule.setContent {
            MyFitnessTheme {
                AuthScreen(viewModelFactory = factory, onAuthSuccess = {})
            }
        }
        composeTestRule.onNodeWithText("Нет аккаунта? Зарегистрироваться").performClick()
        composeTestRule.mainClock.advanceTimeBy(500)
        composeTestRule.onNodeWithText("Уже есть аккаунт? Войти").performClick()
        composeTestRule.mainClock.advanceTimeBy(500)
        composeTestRule.onNodeWithText("Войти").assertIsDisplayed()
    }
}
