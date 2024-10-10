package com.wagdev.inventorymanagement.products_feature.domain.usecases

import com.wagdev.inventorymanagement.products_feature.domain.model.Product
import com.wagdev.inventorymanagement.products_feature.domain.repository.ProductRepository

class GetProductByIdUseCase(
    val productRepository: ProductRepository
) {
    operator suspend fun invoke(id:Long): Product?{
        return productRepository.getProductById(id)
    }
}