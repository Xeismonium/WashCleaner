package com.xeismonium.washcleaner.domain.usecase

import com.xeismonium.washcleaner.core.utils.OrderCodeGenerator
import com.xeismonium.washcleaner.domain.repository.OrderRepository
import java.util.Date
import javax.inject.Inject

class GenerateOrderCodeUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(): Result<String> {
        return orderRepository.getNextOrderCounter().map { count ->
            OrderCodeGenerator.generate(Date(), count)
        }
    }
}
