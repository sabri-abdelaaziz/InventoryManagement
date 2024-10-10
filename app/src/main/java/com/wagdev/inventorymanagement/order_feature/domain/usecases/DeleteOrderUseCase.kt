package com.wagdev.inventorymanagement.order_feature.domain.usecases

import com.wagdev.inventorymanagement.order_feature.domain.model.Order
import com.wagdev.inventorymanagement.order_feature.domain.repository.OrderRepository

class DeleteOrderUseCase(
    val orderRepository: OrderRepository
) {
    operator suspend fun invoke(orderId: Long){
        orderRepository.deleteOrderById(orderId)
    }
}