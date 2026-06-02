package com.example.myfitness.usecase

import com.example.myfitness.MainDispatcherRule
import com.example.myfitness.domain.repository.FoodRepository
import com.example.myfitness.domain.usecase.DeleteFoodItemUseCase
import com.example.myfitness.emptyTestDay
import com.example.myfitness.testFood
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class DeleteFoodItemUseCaseTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository: FoodRepository = mock()
    private val useCase = DeleteFoodItemUseCase(repository)

    @Test
    fun `удаление из завтрака убирает продукт и пересчитывает калории`() = runTest {
        val food = testFood(
            id = 1,
            typeOfMeal = "breakfast",
            calories = 300,
            protein = 15f,
            fats = 8f,
            carbohydrates = 35f
        )
        val day = emptyTestDay().copy(
            calories = 300f,
            protein = 15f,
            fats = 8f,
            carbohydrates = 35f,
            breakfast = listOf(food)
        )

        val result = useCase.execute(food, day)

        assertEquals(0f, result.calories)
        assertEquals(0f, result.protein)
        assertEquals(0f, result.fats)
        assertEquals(0f, result.carbohydrates)
        assertEquals(0, result.breakfast.size)
    }

    @Test
    fun `удаление одного из двух продуктов пересчитывает итоги`() = runTest {
        val food1 = testFood(
            id = 1,
            typeOfMeal = "lunch",
            calories = 200,
            protein = 10f,
            fats = 5f,
            carbohydrates = 20f
        )
        val food2 = testFood(
            id = 2,
            typeOfMeal = "lunch",
            calories = 300,
            protein = 15f,
            fats = 7f,
            carbohydrates = 30f
        )
        val day = emptyTestDay().copy(
            calories = 500f,
            protein = 25f,
            fats = 12f,
            carbohydrates = 50f,
            lunch = listOf(food1, food2)
        )

        val result = useCase.execute(food1, day)

        assertEquals(300f, result.calories)
        assertEquals(15f, result.protein)
        assertEquals(1, result.lunch.size)
        assertFalse(result.lunch.contains(food1))
    }

    @Test
    fun `удаление из ужина не затрагивает другие приёмы пищи`() = runTest {
        val breakfastFood = testFood(id = 1, typeOfMeal = "breakfast", calories = 100)
        val dinnerFood = testFood(id = 2, typeOfMeal = "dinner", calories = 200)
        val day = emptyTestDay().copy(
            calories = 300f,
            breakfast = listOf(breakfastFood),
            dinner = listOf(dinnerFood)
        )

        val result = useCase.execute(dinnerFood, day)

        assertEquals(100f, result.calories)
        assertEquals(1, result.breakfast.size)
        assertEquals(0, result.dinner.size)
    }

    @Test
    fun `вызывается updateDayFoodItems у репозитория`() = runTest {
        val food = testFood()
        val day = emptyTestDay().copy(breakfast = listOf(food), calories = 200f)

        useCase.execute(food, day)

        verify(repository).updateDayFoodItems(any())
    }
}
