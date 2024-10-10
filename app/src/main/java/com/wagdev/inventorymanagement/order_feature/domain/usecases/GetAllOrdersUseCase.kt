package com.wagdev.inventorymanagement.order_feature.domain.usecases

import com.wagdev.inventorymanagement.order_feature.domain.model.Order
import com.wagdev.inventorymanagement.order_feature.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow

class GetAllOrdersUseCase(
    val orderRepository: OrderRepository
) {
    operator fun invoke(): Flow<List<Order>>{
        return orderRepository.getAllOrders()
    }
}