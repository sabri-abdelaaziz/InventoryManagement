package com.wagdev.inventorymanagement.products_feature.domain.repository

import com.wagdev.inventorymanagement.core.util.Routes
import com.wagdev.inventorymanagement.products_feature.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
     fun getAllProducts(): Flow<List<Product>>
    suspend fun getProductById(id: Long):Product?
    suspend fun add_edit_product(product: Product)
    suspend fun delete_product(product: Product)
    suspend fun getBoxesTotal():Int
    suspend fun getItemsTotal():Int
    suspend fun getProductsTotal():Int
    suspend fun  getTotalInventoryValue():Double
    suspend fun getProductsByCategory(category:String):List<Product>
    suspend fun getProductsByName(name:String):List<Product>
    fun getTotalProducts():Flow<Int>
    fun getTopProductName():Flow<String>
    fun getLowStockProductName():Flow<String>
    fun getOutOfStockProductName():Flow<String>
    fun getRecentProductAdd():Flow<String>


}