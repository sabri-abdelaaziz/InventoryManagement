package com.wagdev.inventorymanagement.order_feature.domain.repository

import com.wagdev.inventorymanagement.order_feature.data.local.MonthlySales
import com.wagdev.inventorymanagement.order_feature.data.local.OrderDao
import com.wagdev.inventorymanagement.order_feature.domain.model.Order
import com.wagdev.inventorymanagement.stock_feature.presentation.FeaturedProduct
import kotlinx.coroutines.flow.Flow


interface OrderRepository {
    fun getAllOrders():Flow<List<Order>>
    suspend fun getOrderById(id:Long):Order?
    suspend fun deleteOrderById(orderId: Long)
    suspend fun deleteOrder(order: Order)
    suspend fun addEditOrder(order: Order):Long
    suspend fun getAllOrdersWithDetails():Flow<List<Order>>
    suspend fun getRecentProductsOrdersNbr():Int
    suspend fun getOrdersByClientId(clientId:Long):Flow<List<Order>>
    fun getTotalOrders(): Flow<Int>
    fun getTotalOrdersInLastMonth():Flow<Int>
    fun getTotalOrdersThisMonth():Flow<Int>
    fun getTotalRevenues():Flow<Double?>
    fun getTotalValue():Flow<Double?>
    fun getValueIn():Flow<Double?>
    fun getValueOut():Flow<Double?>
    suspend fun getMonthlySales(): List<MonthlySales>
    fun getFeaturedProducts():Flow<List<FeaturedProduct>>
}