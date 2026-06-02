package com.example.myfitness.usecase

import com.example.myfitness.MainDispatcherRule
import com.example.myfitness.domain.function.Util
import com.example.myfitness.domain.repository.UserRepository
import com.example.myfitness.domain.usecase.GetCaloriesGoalUseCase
import com.example.myfitness.testUser
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GetCaloriesGoalUseCaseTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository: UserRepository = mock()
    private val useCase = GetCaloriesGoalUseCase(repository)

    @Test
    fun `цель похудение уменьшает BMR на 500`() = runTest {
        val user = testUser(weight = 70f, height = 175f, gender = 1, target = "похудение")
        whenever(repository.getProfile()).thenReturn(user)
        val expectedBmr = Util.howMuchNeedCalories(70f, 175f, 25, 1)

        val result = useCase.execute()

        assertEquals(expectedBmr - 500f, result, 0.01f)
    }

    @Test
    fun `цель набор массы увеличивает BMR на 300`() = runTest {
        val user = testUser(weight = 70f, height = 175f, gender = 1, target = "набор массы")
        whenever(repository.getProfile()).thenReturn(user)
        val expectedBmr = Util.howMuchNeedCalories(70f, 175f, 25, 1)

        val result = useCase.execute()

        assertEquals(expectedBmr + 300f, result, 0.01f)
    }

    @Test
    fun `поддержание веса не изменяет BMR`() = runTest {
        val user = testUser(weight = 70f, height = 175f, gender = 1, target = "поддержание")
        whenever(repository.getProfile()).thenReturn(user)
        val expectedBmr = Util.howMuchNeedCalories(70f, 175f, 25, 1)

        val result = useCase.execute()

        assertEquals(expectedBmr, result, 0.01f)
    }

    @Test
    fun `минимальное значение 1200 ккал даже при очень низком BMR`() = runTest {
        val user = testUser(weight = 1f, height = 1f, gender = 0, target = "похудение")
        whenever(repository.getProfile()).thenReturn(user)

        val result = useCase.execute()

        assertTrue(result >= 1200f)
    }

    @Test
    fun `корректный расчёт для женщины`() = runTest {
        val user = testUser(weight = 60f, height = 165f, gender = 0, target = "поддержание")
        whenever(repository.getProfile()).thenReturn(user)
        val expectedBmr = Util.howMuchNeedCalories(60f, 165f, 25, 0)

        val result = useCase.execute()

        assertEquals(expectedBmr, result, 0.01f)
    }
}
