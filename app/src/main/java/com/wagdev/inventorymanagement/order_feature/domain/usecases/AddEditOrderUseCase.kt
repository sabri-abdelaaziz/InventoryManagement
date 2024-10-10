package com.wagdev.inventorymanagement.order_feature.domain.usecases

import com.wagdev.inventorymanagement.order_feature.domain.model.Order
import com.wagdev.inventorymanagement.order_feature.domain.repository.OrderRepository

class AddEditOrderUseCase(
    val orderRepository: OrderRepository
) {
    operator suspend fun invoke(order:Order):Long{
       return  orderRepository.addEditOrder(order)
    }
}