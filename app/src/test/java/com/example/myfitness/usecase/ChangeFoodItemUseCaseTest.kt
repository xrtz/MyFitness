package com.example.myfitness.usecase

import com.example.myfitness.MainDispatcherRule
import com.example.myfitness.domain.repository.FoodRepository
import com.example.myfitness.domain.usecase.ChangeFoodItemUseCase
import com.example.myfitness.emptyTestDay
import com.example.myfitness.testFood
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class ChangeFoodItemUseCaseTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository: FoodRepository = mock()
    private val useCase = ChangeFoodItemUseCase(repository)

    @Test
    fun `редактирование продукта обновляет калории в итогах`() = runTest {
        val old = testFood(
            id = 1,
            typeOfMeal = "breakfast",
            calories = 200,
            protein = 10f,
            fats = 5f,
            carbohydrates = 20f
        )
        val new = old.copy(calories = 350, protein = 20f, fats = 8f, carbohydrates = 35f)
        val day = emptyTestDay().copy(
            calories = 200f,
            protein = 10f,
            fats = 5f,
            carbohydrates = 20f,
            breakfast = listOf(old)
        )

        val result = useCase.execute(new, day)

        assertEquals(350f, result.calories)
        assertEquals(20f, result.protein)
        assertEquals(8f, result.fats)
        assertEquals(35f, result.carbohydrates)
        assertEquals(new, result.breakfast[0])
    }

    @Test
    fun `редактирование одного из двух продуктов влияет только на него`() = runTest {
        val food1 = testFood(id = 1, typeOfMeal = "lunch", calories = 100)
        val food2 = testFood(id = 2, typeOfMeal = "lunch", calories = 200)
        val day = emptyTestDay().copy(
            calories = 300f,
            lunch = listOf(food1, food2)
        )
        val updatedFood1 = food1.copy(calories = 150)

        val result = useCase.execute(updatedFood1, day)

        assertEquals(350f, result.calories)
        assertEquals(2, result.lunch.size)
        assertEquals(updatedFood1, result.lunch.find { it.id == 1 })
    }

    @Test(expected = IllegalArgumentException::class)
    fun `редактирование несуществующего продукта выбрасывает исключение`() = runTest {
        val ghost = testFood(id = 99, typeOfMeal = "breakfast")
        val day = emptyTestDay()

        useCase.execute(ghost, day)
    }

    @Test
    fun `вызывается updateDayFoodItems у репозитория`() = runTest {
        val food = testFood(id = 1, typeOfMeal = "dinner", calories = 100)
        val day = emptyTestDay().copy(calories = 100f, dinner = listOf(food))

        useCase.execute(food.copy(calories = 200), day)

        verify(repository).updateDayFoodItems(any())
    }
}
