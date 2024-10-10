package com.wagdev.inventorymanagement.order_feature.domain.usecases

import com.wagdev.inventorymanagement.order_feature.domain.model.Order
import com.wagdev.inventorymanagement.order_feature.domain.repository.OrderRepository

class GetOrderByIdUseCase (
    val orderRepository: OrderRepository
){
    operator suspend fun invoke(id:Long): Order?{
        return orderRepository.getOrderById(id)
    }
}