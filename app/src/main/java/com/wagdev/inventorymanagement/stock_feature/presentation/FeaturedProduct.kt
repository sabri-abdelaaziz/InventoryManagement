package com.wagdev.inventorymanagement.stock_feature.presentation

import com.wagdev.inventorymanagement.products_feature.domain.model.Product


    data class FeaturedProduct(
val id_product: Int,
val title: String,
val price: Double,
val image: String?,
val nbrOrders: Int
)
