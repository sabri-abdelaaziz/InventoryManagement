package com.wagdev.inventorymanagement.products_feature.presentation

import com.wagdev.inventorymanagement.products_feature.domain.model.Product
import kotlinx.coroutines.flow.Flow

sealed class ProductEvent {
   object getProducts: ProductEvent()
           data class getProductById(val id :Long):ProductEvent()
    data class deleteProduct(val product: Product):ProductEvent()
    data class addEditProduct(val product: Product):ProductEvent()
}