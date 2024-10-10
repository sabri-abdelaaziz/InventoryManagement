package com.wagdev.inventorymanagement.order_feature.domain.repository

import androidx.lifecycle.LiveData
import com.wagdev.inventorymanagement.order_feature.domain.model.OrderDetail
import kotlinx.coroutines.flow.Flow

interface OrderDetailRepository {
    suspend fun deleteOrderDetail(id_order: Long,id_product:Long)
    suspend fun deleteOrderDetailAll(id_order: Long)
    suspend fun getOrderDetail(id:Long): List<OrderDetail>
    suspend fun getOrderDetails(id:Long): LiveData<List<OrderDetail>>
    suspend fun insertOrderDetail(orderDetail: OrderDetail)
    suspend fun insertOrderDetails(listOrderDetail:List<OrderDetail>)
     fun getNbrDetailsPerOrder(orderId:Long):Flow<Int>

}