package com.wagdev.inventorymanagement.order_feature.data.repository

import com.wagdev.inventorymanagement.order_feature.data.local.MonthlySales
import com.wagdev.inventorymanagement.order_feature.data.local.OrderDao
import com.wagdev.inventorymanagement.order_feature.domain.model.Order
import com.wagdev.inventorymanagement.order_feature.domain.repository.OrderRepository
import com.wagdev.inventorymanagement.stock_feature.presentation.FeaturedProduct
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class OrderRepositoryImpl(
    val orderDao: OrderDao
) :OrderRepository{
    override fun getAllOrders(): Flow<List<Order>> {
       return orderDao.getAllOrders()
    }

    override suspend fun getOrderById(id: Long): Order? {
       return orderDao.getOrderById(id)
    }

    override suspend fun deleteOrderById(orderId: Long) {
       orderDao.deleteOrderById(orderId)
    }

    override suspend fun deleteOrder(order: Order) {
       orderDao.deleteOrder(order)
    }

    override suspend fun addEditOrder(order: Order) :Long{
       return orderDao.insertOrder(order)
    }

    override suspend fun getAllOrdersWithDetails(): Flow<List<Order>> {
        return orderDao.getAllOrdersWithDetails()
    }

    override suspend fun getRecentProductsOrdersNbr(): Int {
        return 0
    }

    override suspend fun getOrdersByClientId(clientId: Long): Flow<List<Order>> {
        return orderDao.getOrdersByClientId(clientId)
    }

    override fun getTotalOrders(): Flow<Int> {
        return orderDao.getTotalOrders()
    }

    override fun getTotalOrdersInLastMonth(): Flow<Int> {
        val (startOfLastMonth, endOfLastMonth) = calculateLastMonthRange()
        val totalOrdersLastMonthFlow = orderDao.getTotalOrdersInLastMonth(startOfLastMonth, endOfLastMonth)
        return totalOrdersLastMonthFlow
    }
    override fun getTotalOrdersThisMonth() :Flow<Int>{
        val (startOfCurrentMonth, currentDate) = calculateCurrentMonthRange()
        val totalOrdersThisMonthFlow = orderDao.getTotalOrdersThisMonth(startOfCurrentMonth, currentDate)
return  totalOrdersThisMonthFlow
    }

    override fun getTotalRevenues(): Flow<Double?> {
        return orderDao.getTotalRevenues()
    }

    override fun getTotalValue(): Flow<Double?> {
        return orderDao.getTotalRevenues()
    }

    override fun getValueIn(): Flow<Double?> {
        return orderDao.getValueIn()
    }

    override fun getValueOut(): Flow<Double?> {
        return orderDao.getValueOut()
    }

   override suspend fun getMonthlySales(): List<MonthlySales> {
        return orderDao.getMonthlySales()
    }

    override fun getFeaturedProducts(): Flow<List<FeaturedProduct>> {
        return orderDao.getFeaturedProducts()
    }
}



fun calculateLastMonthRange(): Pair<Long, Long> {
    val calendar = Calendar.getInstance()

    // Set to the first day of the current month
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    // Move back one month
    calendar.add(Calendar.MONTH, -1)

    // Get the start of last month
    val startOfLastMonth = calendar.timeInMillis

    // Move to the last day of last month
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    val endOfLastMonth = calendar.timeInMillis

    return Pair(startOfLastMonth, endOfLastMonth)
}

fun calculateCurrentMonthRange(): Pair<Long, Long> {
    val calendar = Calendar.getInstance()

    // Set to the first day of the current month
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val startOfCurrentMonth = calendar.timeInMillis

    // Get the current date and time
    val currentDate = System.currentTimeMillis()

    return Pair(startOfCurrentMonth, currentDate)
}
