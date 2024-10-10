package com.wagdev.inventorymanagement.products_feature.domain.usecases

import com.wagdev.inventorymanagement.products_feature.domain.model.Product
import com.wagdev.inventorymanagement.products_feature.domain.repository.ProductRepository

class AddEditProductUseCase(
    val productRepository: ProductRepository
) {
    operator suspend fun invoke(product: Product){
        productRepository.add_edit_product(product)
    }
}