package com.wagdev.inventorymanagement.order_feature.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import com.wagdev.inventorymanagement.products_feature.domain.model.Product

@Entity(
    tableName = "order_detail",
    primaryKeys = ["orderId", "productId"],
    foreignKeys = [
        ForeignKey(
            entity = Order::class,
            parentColumns = ["id_order"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id_product"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("orderId"), Index("productId")]
)
data class OrderDetail(
    val orderId: Long,
    val productId: Long,
    val nbrBoxes: Int=0,
    val nbrItems:Int=1
)
