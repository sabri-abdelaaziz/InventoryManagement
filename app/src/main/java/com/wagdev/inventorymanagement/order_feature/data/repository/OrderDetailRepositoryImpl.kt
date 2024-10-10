package com.wagdev.inventorymanagement.order_feature.data.repository

import androidx.lifecycle.LiveData
import com.wagdev.inventorymanagement.order_feature.data.local.OrderDetailDao
import com.wagdev.inventorymanagement.order_feature.domain.model.OrderDetail
import com.wagdev.inventorymanagement.order_feature.domain.repository.OrderDetailRepository
import kotlinx.coroutines.flow.Flow

class OrderDetailRepositoryImpl(
   val orderDetailDao: OrderDetailDao
): OrderDetailRepository {
    override suspend fun deleteOrderDetail(id_order: Long,id_product:Long) {
        orderDetailDao.delete_orderdetail(id_order,id_product)
    }

    override suspend fun deleteOrderDetailAll(id_order: Long) {
        orderDetailDao.delete_orderdetailAll(id_order)
    }


    override suspend fun getOrderDetail(id: Long): List<OrderDetail> {
        return orderDetailDao.getDetailOfOrder(id)
    }

    override suspend fun getOrderDetails(id: Long): LiveData<List<OrderDetail>> {
        return orderDetailDao.getOrderDetails(id)
    }


    override suspend fun insertOrderDetail(orderDetail: OrderDetail) {
        orderDetailDao.insert(orderDetail)
    }

    override suspend fun insertOrderDetails(listOrderDetail: List<OrderDetail>) {
        listOrderDetail.forEach {
            orderDetailDao.insert(it)
        }
    }

    override fun getNbrDetailsPerOrder(orderId: Long): Flow<Int> {
        return orderDetailDao.getNbrDetailsPerOrder(orderId)
    }
}