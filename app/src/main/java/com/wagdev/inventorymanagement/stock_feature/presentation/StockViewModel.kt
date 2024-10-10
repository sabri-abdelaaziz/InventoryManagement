package com.wagdev.inventorymanagement.stock_feature.presentation

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wagdev.inventorymanagement.clients_feature.domain.repository.ClientRepository
import com.wagdev.inventorymanagement.order_feature.domain.repository.OrderRepository
import com.wagdev.inventorymanagement.products_feature.domain.model.Product
import com.wagdev.inventorymanagement.products_feature.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject



@HiltViewModel
class StockViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val clientRepository: ClientRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    // MutableStateFlow to hold store statistics
    val monthlySalesData: StateFlow<List<Float>> = flow {
        val sales = orderRepository.getMonthlySales() // Fetch sales data from repository
        val salesData = MutableList(12) { 0f } // Initialize list for 12 months

        sales.forEach { monthlySale ->
            val month = monthlySale.month?.toIntOrNull() // Safely convert to Int, or null if conversion fails
            if (month != null) {
                val monthIndex = month - 1 // Convert '01' to index 0
                if (monthIndex in salesData.indices) { // Ensure the index is valid
                    salesData[monthIndex] = monthlySale.totalRevenue
                }
            }
        }

        emit(salesData)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    // Function to fetch and update store statistics

    fun getTotalProducts(): Flow<Int> = productRepository.getTotalProducts()
    fun getTotalOrders(): Flow<Int> = orderRepository.getTotalOrders()
    fun getTotalClients(): Flow<Int> = clientRepository.getTotalClients()
    fun getTotalOrdersInLastMonth(): Flow<Int> = orderRepository.getTotalOrdersInLastMonth()
    fun getTotalOrdersInThisMonth(): Flow<Int> = orderRepository.getTotalOrdersThisMonth()
    fun getTotalRevenuesFormatted(): Flow<Double?> = orderRepository.getTotalRevenues()
    fun getTotalClientInLastMonth(): Flow<Int> = clientRepository.getTotalClientInLastMonth()
    fun getTopClientName(): Flow<String> = clientRepository.getTopClientName()
    fun getTopProductName(): Flow<String> = productRepository.getTopProductName()
    fun getLowStockProductName(): Flow<String> = productRepository.getLowStockProductName()
    fun getOutOfStockProductName(): Flow<String> = productRepository.getOutOfStockProductName()
    fun getRecentProductAdd(): Flow<String> = productRepository.getRecentProductAdd()
    fun getTotalValue(): Flow<Double?> = orderRepository.getTotalValue()
    fun getValueIn(): Flow<Double?> = orderRepository.getValueIn()
    fun getValueOut(): Flow<Double?> = orderRepository.getValueOut()

    fun getFeaturedProducts():Flow<List<FeaturedProduct>> {
        return orderRepository.getFeaturedProducts()
    }
}





