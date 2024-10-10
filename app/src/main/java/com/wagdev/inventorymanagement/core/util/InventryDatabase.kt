package com.wagdev.inventorymanagement.core.util

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wagdev.inventorymanagement.auth_feature.data.LoginDao
import com.wagdev.inventorymanagement.clients_feature.data.ClientDao
import com.wagdev.inventorymanagement.clients_feature.domain.model.Client
import com.wagdev.inventorymanagement.order_feature.data.local.OrderDao
import com.wagdev.inventorymanagement.order_feature.data.local.OrderDetailDao
import com.wagdev.inventorymanagement.order_feature.domain.model.Order
import com.wagdev.inventorymanagement.order_feature.domain.model.OrderDetail
import com.wagdev.inventorymanagement.products_feature.data.local.ProductDao
import com.wagdev.inventorymanagement.products_feature.domain.model.Product
import com.wagdev.inventorymanagement.auth_feature.domain.model.Login
@Database(entities = [Product::class, Client::class, Order::class,OrderDetail::class,Login::class], version = 1,exportSchema = false)
@TypeConverters(UriConverter::class)
abstract class InventoryDatabase:RoomDatabase(){
    abstract val productDao:ProductDao
    abstract val clientDao:ClientDao
    abstract val orderDao:OrderDao
    abstract val orderDetailDao: OrderDetailDao
    abstract val loginDao:LoginDao
    companion object{
        const val DATABASE_NAME:String="inventory_db_13"
    }
}