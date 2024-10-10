package com.wagdev.inventorymanagement.order_feature.domain.usecases

import com.wagdev.inventorymanagement.order_feature.domain.model.Order
import com.wagdev.inventorymanagement.order_feature.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow

class GetAllOrdersWithDetailsUseCase(
    val orderRepository: OrderRepository
) {
    operator suspend fun invoke(): Flow<List<Order>> {
return orderRepository.getAllOrdersWithDetails()
    }
}