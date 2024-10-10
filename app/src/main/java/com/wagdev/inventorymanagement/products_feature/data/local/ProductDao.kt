package com.wagdev.inventorymanagement.products_feature.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.wagdev.inventorymanagement.products_feature.domain.model.Product
import kotlinx.coroutines.flow.Flow


@Dao
interface  ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add_edit_product(product: Product)

    @Delete
    suspend fun delete_product(product: Product)
    @Query("SELECT * FROM  product")
    fun getAllProducts(): Flow<List<Product>>
    @Query("SELECT * FROM product WHERE id_product=:id")
    suspend fun getProductById(id:Long): Product?

    @Query("SELECT COUNT(nbrBoxes) FROM product")
    suspend fun getBoxesTotal():Int
    @Query("SELECT COUNT(nbrItems) FROM product")
    suspend fun getItemsTotal():Int
    @Query("SELECT COUNT(id_product) FROM product")
    suspend fun getProductsTotal():Int
    @Query("SELECT IFNULL(SUM(nbrBoxes*nbrItems*price), 0.0) FROM product")
    suspend fun getTotalInventoryValue(): Double

    @Query("SELECT COUNT(id_product) from product")
    fun getTotalProducts():Flow<Int>
    @Query("SELECT p.title FROM product p ,orders o ,order_detail d WHERE p.id_product=d.productId AND d.productId=o.id_order GROUP BY p.title ORDER BY SUM(d.nbrItems) DESC LIMIT 1")
    fun getTopProductName():Flow<String>
    @Query("SELECT p.title FROM product p ,orders o ,order_detail d WHERE p.id_product=d.productId AND d.productId=o.id_order GROUP BY p.title ORDER BY SUM(d.nbrItems) ASC LIMIT 1")
    fun getLowStockProductName():Flow<String>
    @Query("SELECT title FROM product WHERE nbrItems<=0")
    fun getOutOfStockProductName():Flow<String>
    @Query("SELECT title FROM product GROUP BY date_added ORDER BY date_added DESC LIMIT 1 ")
    fun getRecentProductAdd():Flow<String>
}