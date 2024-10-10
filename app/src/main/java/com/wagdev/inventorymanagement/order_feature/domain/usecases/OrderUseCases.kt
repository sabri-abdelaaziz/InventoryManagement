package com.wagdev.inventorymanagement.order_feature.domain.usecases

data class OrderUseCases(
    val getOrderByIdUseCase: GetOrderByIdUseCase,
    val getAllOrdersUseCase: GetAllOrdersUseCase,
    val getAllOrdersWithDetailsUseCase: GetAllOrdersWithDetailsUseCase,
    val addEditOrderUseCase: AddEditOrderUseCase,
    val deleteOrderUseCase: DeleteOrderUseCase,
    val getOrderByClientId:GetOrderByClientIdUseCase
)