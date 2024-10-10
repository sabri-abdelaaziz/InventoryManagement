package com.wagdev.inventorymanagement.order_feature.presentation


import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wagdev.inventorymanagement.clients_feature.domain.model.Client
import com.wagdev.inventorymanagement.clients_feature.domain.repository.ClientRepository
import com.wagdev.inventorymanagement.order_feature.domain.model.Order
import com.wagdev.inventorymanagement.order_feature.domain.model.OrderDetail
import com.wagdev.inventorymanagement.order_feature.domain.repository.OrderDetailRepository
import com.wagdev.inventorymanagement.order_feature.domain.repository.OrderRepository
import com.wagdev.inventorymanagement.order_feature.domain.usecases.OrderUseCases
import com.wagdev.inventorymanagement.products_feature.domain.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderUseCases: OrderUseCases,
    private val orderDetailRepository:OrderDetailRepository,
) : ViewModel() {

    // initial orderDetail
    private val _orderDetail = MutableStateFlow<List<OrderDetail>>(emptyList())
    val orderDetail: StateFlow<List<OrderDetail>> = _orderDetail

    var orderDetails = mutableStateOf<List<OrderDetail>>(emptyList())
        private set


    private val _status = MutableStateFlow<OrderStatus>(OrderStatus.Idle)
    val status: StateFlow<OrderStatus> = _status.asStateFlow()


    private val _clients = MutableStateFlow<List<Client>>(emptyList())
    val clients: StateFlow<List<Client>> = _clients

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _id_order = MutableStateFlow<Long?>(null)
    val id_order: StateFlow<Long?> = _id_order
    private val _selectedClient = MutableStateFlow<Client?>(null)
    val selectedClient: StateFlow<Client?> = _selectedClient

    private val _selectedProducts = MutableStateFlow<List<Product>>(emptyList())
    val selectedProducts: StateFlow<List<Product>> = _selectedProducts

    private val _orderDate = MutableStateFlow(Date())
    val orderDate: StateFlow<Date> = _orderDate


    init {
        loadOrders()
    }

    fun onEvent(event: OrderEvent) {
        when (event) {


            is OrderEvent.GetOrders -> {
                loadOrders()
            }

            is OrderEvent.GetOrderByClient -> {
                viewModelScope.launch {
                    _status.value = OrderStatus.Loading
                    try {
                        val filteredOrders = orderUseCases.getOrderByClientId(event.clientId)
                        _status.value = OrderStatus.Success(filteredOrders)
                    } catch (e: Exception) {
                    }
                }
            }

            is OrderEvent.AddEditOrder -> {
                viewModelScope.launch {
                    _status.value = OrderStatus.Loading
                    try {
                        _id_order.value = orderUseCases.addEditOrderUseCase(event.order)
                        println("_id_order" + _id_order.value)
                        loadOrders()// Provide actual list after loading
                    } catch (e: Exception) {
                        println(e)
                    }
                }
            }

            is OrderEvent.DeleteOrder -> {
                viewModelScope.launch {
                    _status.value = OrderStatus.Loading
                    try {
                        orderUseCases.deleteOrderUseCase(event.id)
                        loadOrders()
                        _status.value =
                            OrderStatus.Success(emptyFlow()) // Provide actual list after loading
                    } catch (e: Exception) {
                        _status.value = OrderStatus.Error("Error deleting order")
                    }
                }
            }

            is OrderEvent.DeleteOrderDetail -> {

                viewModelScope.launch {
                    _status.value = OrderStatus.Loading
                    try {
                        orderDetailRepository.deleteOrderDetail(event.id_order, event.id_product)
                        loadOrders()
                        _status.value =
                            OrderStatus.Success(emptyFlow()) // Provide actual list after loading
                    } catch (e: Exception) {
                        _status.value = OrderStatus.Error("Error deleting order")
                    }
                }
            }

            is OrderEvent.DeleteOrderDetailAll -> {

                viewModelScope.launch {
                    _status.value = OrderStatus.Loading
                    try {
                        orderDetailRepository.deleteOrderDetailAll(event.id_order)
                        loadOrders()
                        _status.value =
                            OrderStatus.Success(emptyFlow()) // Provide actual list after loading
                    } catch (e: Exception) {
                        _status.value = OrderStatus.Error("Error deleting order")
                    }
                }
            }

            is OrderEvent.GetOrderById -> {
                // Implement logic to handle fetching order by ID
            }

            is OrderEvent.GetOrderDetail -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _orderDetail.value = orderDetailRepository.getOrderDetail(event.orderId)
                }
            }

            is OrderEvent.AddOrderDetail -> {
                viewModelScope.launch {
                    orderDetailRepository.insertOrderDetail(event.orderDetail)
                    loadOrders()
                }
            }
        }
    }

    private fun loadOrders() {
        viewModelScope.launch {
            _status.value = OrderStatus.Loading
            try {
                val orders = orderUseCases.getAllOrdersWithDetailsUseCase()
                _status.value = OrderStatus.Success(orders)
            } catch (e: Exception) {
                _status.value = OrderStatus.Error("Error loading orders")
            }
        }
    }

    fun selectClient(client: Client) {
        _selectedClient.value = client
    }

    fun selectProducts(products: List<Product>) {
        _selectedProducts.value = products
    }

    fun updateOrderDate(date: Date) {
        _orderDate.value = date
    }

    fun addOrUpdateOrder(order: Order) {
        viewModelScope.launch {
            _status.value = OrderStatus.Loading
            try {
                orderUseCases.addEditOrderUseCase(order)
                loadOrders()
                _status.value =
                    OrderStatus.Success(emptyFlow()) // Provide actual list after loading
            } catch (e: Exception) {
                _status.value = OrderStatus.Error("Error adding order")
            }
        }
    }

    fun addOrUpdateOrder(order: Order, orderDetails: List<OrderDetail>) {
        viewModelScope.launch {
            // Save order first and retrieve the generated orderId
            _status.value = OrderStatus.Loading
            try {
                orderUseCases.addEditOrderUseCase(order)
                loadOrders()
                _status.value =
                    OrderStatus.Success(emptyFlow()) // Provide actual list after loading
            } catch (e: Exception) {
                _status.value = OrderStatus.Error("Error adding order")
            }


            // Map the orderDetails to include the generated orderId
            val detailsWithOrderId = orderDetails.map { it.copy(orderId = order.id_order) }

            // Insert the orderDetails with the generated orderId
            orderDetailRepository.insertOrderDetails(detailsWithOrderId)
        }
    }

    fun saveOrderAndDetails(
        newOrder: Order,
        selectedProductQuantities: Map<Long, Prod>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val orderId = orderUseCases.addEditOrderUseCase(newOrder)
            _id_order.value = orderId
            println("Ordid" + id_order.value)

            selectedProductQuantities.forEach { (productId, prod) ->
                val orderDetail = OrderDetail(
                    orderId = orderId,
                    productId = productId,
                    nbrBoxes = prod.nbrBoxes,
                    nbrItems = prod.nbrItems
                )
                orderDetailRepository.insertOrderDetail(orderDetail)
                println("Ord" + orderDetail)

            }
        }
    }

    fun getOrderDetails(orderId: Long): LiveData<List<OrderDetail>> {
        var data: LiveData<List<OrderDetail>> = MutableLiveData()
        viewModelScope.launch {
            data = orderDetailRepository.getOrderDetails(orderId)
        }
        return data
    }

    fun getNbrDetailsPerOrder(orderId: Long): Flow<Int> {
        return orderDetailRepository.getNbrDetailsPerOrder(orderId)
    }
}