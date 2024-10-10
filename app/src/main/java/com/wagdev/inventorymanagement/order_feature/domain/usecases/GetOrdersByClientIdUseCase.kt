package com.wagdev.inventorymanagement.order_feature.domain.usecases

import com.wagdev.inventorymanagement.order_feature.domain.repository.OrderRepository

class GetOrderByClientIdUseCase(
    var orderRepository: OrderRepository
) {
    suspend operator fun invoke(clientId: Long) = orderRepository.getOrdersByClientId(clientId)


}