package com.example.myfitness.usecase

import com.example.myfitness.MainDispatcherRule
import com.example.myfitness.domain.repository.FoodRepository
import com.example.myfitness.domain.usecase.AddFoodItemUseCase
import com.example.myfitness.emptyTestDay
import com.example.myfitness.testFood
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class AddFoodItemUseCaseTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository: FoodRepository = mock()
    private val useCase = AddFoodItemUseCase(repository)

    @Test
    fun `добавление еды в завтрак обновляет калории и список завтрака`() = runTest {
        val day = emptyTestDay()
        val food = testFood(
            typeOfMeal = "breakfast",
            calories = 300,
            protein = 20f,
            fats = 10f,
            carbohydrates = 40f
        )

        val result = useCase.execute(food, day)

        assertEquals(300f, result.calories)
        assertEquals(20f, result.protein)
        assertEquals(10f, result.fats)
        assertEquals(40f, result.carbohydrates)
        assertEquals(1, result.breakfast.size)
        assertTrue(result.breakfast.contains(food))
        assertEquals(0, result.lunch.size)
        assertEquals(0, result.dinner.size)
    }

    @Test
    fun `добавление еды в обед помещает продукт только в обед`() = runTest {
        val day = emptyTestDay()
        val food = testFood(typeOfMeal = "lunch", calories = 400)

        val result = useCase.execute(food, day)

        assertEquals(400f, result.calories)
        assertEquals(1, result.lunch.size)
        assertEquals(0, result.breakfast.size)
        assertEquals(0, result.dinner.size)
    }

    @Test
    fun `добавление еды в ужин и перекус корректно обновляет списки`() = runTest {
        val dinner = testFood(id = 1, typeOfMeal = "dinner", calories = 200)
        val snack = testFood(id = 2, typeOfMeal = "snacks", calories = 50)
        val day = emptyTestDay()

        val afterDinner = useCase.execute(dinner, day)
        val afterSnack = useCase.execute(snack, afterDinner)

        assertEquals(250f, afterSnack.calories)
        assertEquals(1, afterSnack.dinner.size)
        assertEquals(1, afterSnack.snacks.size)
    }

    @Test
    fun `калории накапливаются если в дне уже есть еда`() = runTest {
        val existing = testFood(id = 1, typeOfMeal = "breakfast", calories = 200)
        val day = emptyTestDay().copy(calories = 200f, breakfast = listOf(existing))
        val newFood = testFood(id = 2, typeOfMeal = "breakfast", calories = 100)

        val result = useCase.execute(newFood, day)

        assertEquals(300f, result.calories)
        assertEquals(2, result.breakfast.size)
    }

    @Test
    fun `вызывается updateDayFoodItems у репозитория`() = runTest {
        useCase.execute(testFood(), emptyTestDay())
        verify(repository).updateDayFoodItems(any())
    }
}
