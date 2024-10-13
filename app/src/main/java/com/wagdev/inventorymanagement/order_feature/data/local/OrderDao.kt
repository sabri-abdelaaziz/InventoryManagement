package com.wagdev.inventorymanagement.order_feature.data.local

import androidx.room.*
import com.wagdev.inventorymanagement.order_feature.domain.model.Order
import com.wagdev.inventorymanagement.stock_feature.presentation.FeaturedProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Upsert
    suspend fun insertOrder(order: Order): Long

    @Update
    suspend fun updateOrder(order: Order)

    @Delete
    suspend fun deleteOrder(order: Order)

    @Query("DELETE FROM orders WHERE id_order = :orderId")
    suspend fun deleteOrderById(orderId: Long)

    @Query("SELECT * FROM orders WHERE id_order = :orderId")
    suspend fun getOrderById(orderId: Long): Order?

    @Transaction
    @Query("SELECT * FROM orders WHERE id_order = :orderId")
    suspend fun getOrderWithDetailsById(orderId: Long): Order?

    @Transaction
    @Query("SELECT * FROM orders")
    fun getAllOrdersWithDetails(): Flow<List<Order>>

    @Query("SELECT * FROM orders")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE clientId = :clientId")
    fun getOrdersByClientId(clientId: Long): Flow<List<Order>>

    @Query("SELECT COUNT(id_order) FROM orders")
    fun getTotalOrders():Flow<Int>

    @Query("SELECT COUNT(id_order) FROM orders  WHERE orderDate BETWEEN :startOfLastMonth AND :endOfLastMonth")
    fun getTotalOrdersInLastMonth(startOfLastMonth: Long, endOfLastMonth: Long): Flow<Int>
    @Query(" SELECT COUNT(id_order) FROM orders WHERE orderDate BETWEEN :startOfCurrentMonth AND :currentDate")
    fun getTotalOrdersThisMonth(startOfCurrentMonth: Long, currentDate: Long): Flow<Int>

    @Query("SELECT SUM(o.shipping+(d.nbrItems*p.price)) FROM product p,orders o ,order_detail d  WHERE o.id_order=d.orderId AND d.productId=p.id_product")
    fun getTotalRevenues():Flow<Double?>
    @Query("SELECT SUM(o.shipping+(d.nbrItems*p.price)) FROM product p,orders o ,order_detail d  WHERE o.id_order=d.orderId AND d.productId=p.id_product")
    fun getValueIn():Flow<Double?>
    @Query("SELECT SUM(o.shipping+(d.nbrItems*p.price)) FROM product p,orders o ,order_detail d  WHERE o.id_order=d.orderId AND d.productId=p.id_product")
    fun getValueOut():Flow<Double?>

    @Query("""
    SELECT strftime('%m', o.orderDate) AS month, 
           SUM(o.shipping + (d.nbrItems * p.price)) AS totalRevenue 
    FROM orders o 
    JOIN order_detail d ON o.id_order = d.orderId 
    JOIN product p ON d.productId = p.id_product 
    WHERE o.orderDate IS NOT NULL
    GROUP BY month 
    ORDER BY month
""")
    suspend fun getMonthlySales(): List<MonthlySales>

    @Query("""
        SELECT p.id_product, p.title, p.price, p.image, COUNT(o.id_order) AS nbrOrders
        FROM Product p
        LEFT JOIN order_detail od ON p.id_product = od.productId
        LEFT JOIN `orders` o ON od.orderId = o.id_order
        GROUP BY p.id_product
        ORDER BY nbrOrders DESC
        LIMIT 4
    """)
    fun getFeaturedProducts(): Flow<List<FeaturedProduct>>

}
data class MonthlySales(
    val month: String?, // Nullable
    val totalRevenue: Float
)

