package com.wagdev.inventorymanagement.products_feature.data.repository

import com.wagdev.inventorymanagement.products_feature.data.local.ProductDao
import com.wagdev.inventorymanagement.products_feature.domain.model.Product
import com.wagdev.inventorymanagement.products_feature.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

class ProductRepositoryImpl(
    val productDao: ProductDao
) :ProductRepository{
    override fun getAllProducts(): Flow<List<Product>> {
      return productDao.getAllProducts()
    }

    override suspend fun getProductById(id: Long): Product? {
        return productDao.getProductById(id)
    }

    override suspend fun add_edit_product(product: Product) {
        productDao.add_edit_product(product)
    }

    override suspend fun delete_product(product: Product) {
       productDao.delete_product(product)
    }

    override suspend fun getBoxesTotal(): Int {
        return productDao.getBoxesTotal()
    }

    override suspend fun getItemsTotal(): Int {
        return productDao.getItemsTotal()
    }

    override suspend fun getProductsTotal(): Int {
        return productDao.getProductsTotal()
    }

    override suspend fun getTotalInventoryValue(): Double {
       return productDao.getTotalInventoryValue()
    }

    override suspend fun getProductsByCategory(category: String): List<Product> {
        TODO("Not yet implemented")
    }

    override suspend fun getProductsByName(name: String): List<Product> {
        TODO("Not yet implemented")
    }

    override fun getTotalProducts(): Flow<Int> {
        return productDao.getTotalProducts()
    }

    override fun getTopProductName(): Flow<String> {
        return productDao.getTopProductName()
    }

    override fun getLowStockProductName(): Flow<String> {
        return productDao.getLowStockProductName()
    }

    override fun getOutOfStockProductName(): Flow<String> {
        return productDao.getOutOfStockProductName()
    }

    override fun getRecentProductAdd(): Flow<String> {
       return productDao.getRecentProductAdd()
    }

}