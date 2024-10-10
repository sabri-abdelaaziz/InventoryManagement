package com.wagdev.inventorymanagement.core.util

sealed class Routes(
    val route:String
) {
    object Home: Routes("home")
    object Password: Routes("password")
    object Orders: Routes("orders")
    object Products: Routes( "products")
    object Product: Routes( "product")
    object Clients: Routes("clients")
    object Client:Routes("client")
    object Settings: Routes("settings")
    object Facture: Routes("facture")
    object Stock: Routes("stock")
    object AddEditProduct:Routes("addeditproduct")
    object AddEditClient:Routes("addeditclient")
    object AddEditOrder:Routes("addeditorder")
    object Downloads:Routes("downloads")


}