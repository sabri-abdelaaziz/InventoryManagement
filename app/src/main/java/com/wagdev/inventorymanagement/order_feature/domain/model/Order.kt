package com.wagdev.inventorymanagement.order_feature.domain.model

import androidx.room.*
import com.wagdev.inventorymanagement.clients_feature.domain.model.Client
import com.wagdev.inventorymanagement.products_feature.domain.model.Product

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(entity = Client::class, parentColumns = ["id_client"], childColumns = ["clientId"]),
    ],
    indices = [Index(value = ["clientId"])]
)
data class Order(
    @PrimaryKey(autoGenerate = true) val id_order: Long = 0,
    @ColumnInfo(name = "clientId") val clientId: Long,
    @ColumnInfo(name = "shipping") val shipping: Double=0.0,
    @ColumnInfo(name = "orderDate") val orderDate: Long = System.currentTimeMillis()
)
