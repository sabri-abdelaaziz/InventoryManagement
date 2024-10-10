package com.wagdev.inventorymanagement.products_feature.domain.usecases

import com.wagdev.inventorymanagement.products_feature.domain.repository.ProductRepository

class GetBoxesTotalUseCase(
    val productRepository: ProductRepository
) {
    suspend operator fun invoke() = productRepository.getBoxesTotal()

}