package com.wagdev.inventorymanagement.order_feature.presentation


import com.wagdev.inventorymanagement.order_feature.domain.model.Order
import kotlinx.coroutines.flow.Flow

sealed class OrderStatus {
    object Idle : OrderStatus()
    object Loading : OrderStatus()
    data class Success(val orders: Flow<List<Order>>) : OrderStatus()
    data class Error(val message: String) : OrderStatus()
}
