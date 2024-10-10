package com.wagdev.inventorymanagement.core.controllers


import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.wagdev.inventorymanagement.clients_feature.presentation.AddEditClientScreen
import com.wagdev.inventorymanagement.core.presentation.Home
import com.wagdev.inventorymanagement.auth_feature.presentation.Password
import com.wagdev.inventorymanagement.clients_feature.domain.model.Client
import com.wagdev.inventorymanagement.core.presentation.PdfListScreen
import com.wagdev.inventorymanagement.core.util.Routes
import com.wagdev.inventorymanagement.facture_feature.presentation.FactureScreen
import com.wagdev.inventorymanagement.order_feature.domain.model.Order
import com.wagdev.inventorymanagement.order_feature.presentation.AddEditOrderScreen
import com.wagdev.inventorymanagement.orders_feature.presentation.OrdersScreen
import com.wagdev.inventorymanagement.products_feature.domain.model.Product
import com.wagdev.inventorymanagement.products_feature.presentation.AddEditProductScreen
import com.wagdev.inventorymanagement.products_feature.presentation.ProductsScreen
import com.wagdev.inventorymanagement.setting_feature.presentation.SettingsScreen

import com.wagdev.inventorymanagement.store_feature.presentation.StockScreen
import org.json.JSONObject

@Composable
fun Navigation(
     navController: NavHostController,
     modifier: Modifier=Modifier
) {
    NavHost(navController = navController, startDestination = Routes.Password.route ) {
        composable(Routes.Password.route){
            Password(navController=navController)
        }
        composable("home/{selectedItem}", arguments = listOf(navArgument("selectedItem") { type = NavType.IntType })) { backStackEntry ->
            val selectedItemIndex = backStackEntry.arguments?.getInt("selectedItem") ?: 0
            Home(navController, selectedItemIndex)
        }
        composable(Routes.Stock.route){
            StockScreen(navController=navController)
        }
        composable(Routes.Products.route){
            ProductsScreen(navController=navController)
        }
        composable(Routes.Orders.route){
            OrdersScreen(navController=navController)
        }
        composable(Routes.Downloads.route){
            PdfListScreen(LocalContext.current,navController)
        }

        composable(
            route = "addeditproduct?product={product}",
            arguments = listOf(navArgument("product") {
                type = NavType.StringType
                nullable = true // Allow null values
            })
        ) { backStackEntry ->
            val productJson = backStackEntry.arguments?.getString("product")
            val product = productJson?.let { parseProduct(it) }
            AddEditProductScreen(
                navController = navController,
                product = product
            )
        }

        composable(
            route = "addeditclient?client={client}",
            arguments = listOf(navArgument("client") {
                type = NavType.StringType
                nullable = true // Allow null values
            })
        ) { backStackEntry ->
            val clientJson = backStackEntry.arguments?.getString("client")
            val client = clientJson?.let { parseClient(it) }
            AddEditClientScreen(
                navController = navController,
                client = client
            )
        }


        composable(
            route = "addeditorder?order={order}",
            arguments = listOf(navArgument("order") {
                type = NavType.StringType
                nullable = true // Allow null values
            })
        ) { backStackEntry ->
            val orderJson = backStackEntry.arguments?.getString("order")
            val order = orderJson?.let { parseOrder(it) }
            AddEditOrderScreen(
                navController = navController,
                order = order
            )
        }
        composable(Routes.Facture.route){
            FactureScreen()
        }
        composable(Routes.Settings.route){
            SettingsScreen(context = LocalContext.current)
        }


    }

}
fun parseProduct(json: String): Product {
    val jsonObject = JSONObject(json)
    val id = jsonObject.getLong("id_product")
    val title = jsonObject.getString("title")
    val price = jsonObject.getDouble("price")
    val nbrItems= jsonObject.getInt("nbrItems")
    val nbrBoxes= jsonObject.getInt("nbrBoxes")
    val nbrItemsPerBox= jsonObject.getInt("nbrItemsPerBox")
    val image = jsonObject.getString("image")


    return Product(id, price, title,nbrItems,nbrBoxes,nbrItemsPerBox, image)
}

fun parseClient(clientJson: String): Client {
    val jsonObject=JSONObject(clientJson)
    val id=jsonObject.getLong("id_client")
    val name=jsonObject.getString("name")
    val email=jsonObject.getString("email")
    val phoneNumber=jsonObject.getString("email")
    val address=jsonObject.getString("address")
    return Client(id,name,email,phoneNumber,address)
}
fun parseOrder(orderJson: String): Order {
    val jsonObject=JSONObject(orderJson)
    val id=jsonObject.getLong("id_order")
    val clientId=jsonObject.getLong("clientId")
    val shipping=jsonObject.getDouble("shipping")
    val orderDate=jsonObject.getLong("orderDate")
    return Order(id, clientId ,shipping ,orderDate)
}