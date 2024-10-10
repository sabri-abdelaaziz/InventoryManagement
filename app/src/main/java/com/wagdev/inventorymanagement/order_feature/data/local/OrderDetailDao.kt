package com.wagdev.inventorymanagement.order_feature.data.local


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.wagdev.inventorymanagement.order_feature.domain.model.OrderDetail
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDetailDao {


    @Insert
    fun insert(orderDetail: OrderDetail)

    @Delete
    fun delete(orderDetail: OrderDetail)

    @Query("DELETE  FROM order_detail WHERE orderId = :id_order AND productId=:id_product")
    suspend fun delete_orderdetail(id_order:Long,id_product:Long)
    @Query("DELETE  FROM order_detail WHERE orderId = :id_order")
    suspend fun delete_orderdetailAll(id_order:Long)

    @Query("SELECT * FROM order_detail WHERE orderId = :id")
    suspend fun getDetailOfOrder(id: Long): List<OrderDetail>
    @Query("SELECT * FROM order_detail WHERE orderId = :id")
    fun getOrderDetails(id: Long): LiveData<List<OrderDetail>>



    @Query("SELECT * FROM order_detail")
     fun getAllDetail(): Flow<List<OrderDetail>>
    @Query("SELECT COUNT(*) FROM order_detail WHERE orderId = :orderId")
    fun getNbrDetailsPerOrder(orderId: Long): Flow<Int>


}