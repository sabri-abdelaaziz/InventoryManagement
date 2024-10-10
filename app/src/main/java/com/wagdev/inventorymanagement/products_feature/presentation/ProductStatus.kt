package com.wagdev.inventorymanagement.products_feature.presentation

import com.wagdev.inventorymanagement.products_feature.domain.model.Product
import kotlinx.coroutines.flow.Flow

sealed class ProductStatus {
    object Idle : ProductStatus()
    object Loading : ProductStatus()
    data class Success(val products: Flow<List<Product>>) : ProductStatus()
    data class Error(val message: String) : ProductStatus()
}