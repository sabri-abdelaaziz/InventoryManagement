package com.wagdev.inventorymanagement.clients_feature.domain.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Client(
    @PrimaryKey(autoGenerate = true) val id_client: Long = 0,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val address: String,
    val date_added:Long=System.currentTimeMillis()
)