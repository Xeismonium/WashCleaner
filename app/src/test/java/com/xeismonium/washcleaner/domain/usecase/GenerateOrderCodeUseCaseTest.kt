package com.xeismonium.washcleaner.domain.usecase

import com.xeismonium.washcleaner.domain.repository.OrderRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GenerateOrderCodeUseCaseTest {

    private lateinit var orderRepository: OrderRepository
    private lateinit var generateOrderCodeUseCase: GenerateOrderCodeUseCase

    @Before
    fun setUp() {
        orderRepository = mockk()
        generateOrderCodeUseCase = GenerateOrderCodeUseCase(orderRepository)
    }

    @Test
    fun `invoke should return formatted code when repository returns count`() = runBlocking {
        // Given
        coEvery { orderRepository.getNextOrderCounter() } returns Result.success(5L)

        // When
        val result = generateOrderCodeUseCase()

        // Then
        assertTrue(result.isSuccess)
        val code = result.getOrNull()
        assertTrue(code?.startsWith("WC-") == true)
        assertTrue(code?.endsWith("-005") == true)
    }

    @Test
    fun `invoke should return failure when repository fails`() = runBlocking {
        // Given
        val exception = Exception("DB Error")
        coEvery { orderRepository.getNextOrderCounter() } returns Result.failure(exception)

        // When
        val result = generateOrderCodeUseCase()

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
