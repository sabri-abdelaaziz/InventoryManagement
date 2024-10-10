package com.wagdev.inventorymanagement.order_feature.presentation

import com.wagdev.inventorymanagement.order_feature.domain.model.Order
import com.wagdev.inventorymanagement.order_feature.domain.model.OrderDetail


sealed class OrderEvent {
    object GetOrders : OrderEvent()
    data class AddEditOrder(val order: Order) : OrderEvent()
    data class DeleteOrder(val id: Long) : OrderEvent()
    data class GetOrderById(val orderId: String) : OrderEvent()
    data class GetOrderByClient(val clientId :Long):OrderEvent()
    data class GetOrderDetail(val orderId:Long) : OrderEvent()
    data class AddOrderDetail(val orderDetail: OrderDetail) :OrderEvent()
    data class DeleteOrderDetail(val id_order:Long,val id_product:Long):OrderEvent()
    data class DeleteOrderDetailAll(val id_order: Long):OrderEvent()
}