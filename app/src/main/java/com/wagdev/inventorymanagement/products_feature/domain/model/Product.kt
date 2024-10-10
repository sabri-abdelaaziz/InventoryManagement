package com.wagdev.inventorymanagement.products_feature.domain.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Product (
    @PrimaryKey(autoGenerate = true)
    val id_product: Long = 0,
    val price: Double,
    val title: String,
    val nbrItems: Int,
    var nbrBoxes: Int,
    var nbrItemsPerBox: Int,
    val image: String? = null , // Default to null if not provided
    val date_added:Long=System.currentTimeMillis()
)
