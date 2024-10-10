package com.wagdev.inventorymanagement.products_feature.domain.usecases

data class  ProductUseCases(
    val getProductByIdUseCase: GetProductByIdUseCase,
    val getProductsUseCase:GetProductsUseCase,
    val deleteProductUseCase: DeleteProductUseCase,
    val addEditProductUseCase: AddEditProductUseCase,
    val getBoxesTotal: GetBoxesTotalUseCase
)