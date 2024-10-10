package com.wagdev.inventorymanagement.order_feature.presentation


import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.wagdev.inventorymanagement.R
import com.wagdev.inventorymanagement.clients_feature.domain.model.Client
import com.wagdev.inventorymanagement.clients_feature.presentation.ClientStatus
import com.wagdev.inventorymanagement.clients_feature.presentation.ClientViewModel
import com.wagdev.inventorymanagement.core.util.Routes
import com.wagdev.inventorymanagement.order_feature.domain.model.Order
import com.wagdev.inventorymanagement.order_feature.domain.model.OrderDetail
import com.wagdev.inventorymanagement.order_feature.presentation.OrderEvent
import com.wagdev.inventorymanagement.order_feature.presentation.OrderViewModel
import com.wagdev.inventorymanagement.products_feature.domain.model.Product
import com.wagdev.inventorymanagement.products_feature.presentation.ProductEvent
import com.wagdev.inventorymanagement.products_feature.presentation.ProductStatus
import com.wagdev.inventorymanagement.products_feature.presentation.ProductViewModel
import java.text.SimpleDateFormat

import java.util.Calendar
import java.util.Date
import java.util.Locale



data class Prod(
    val nbrBoxes:Int=0,
    val nbrItems:Int=1
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditOrderScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    clientViewModel: ClientViewModel = hiltViewModel(),
    productViewModel: ProductViewModel = hiltViewModel(),
    orderViewModel: OrderViewModel = hiltViewModel(),
    order: Order? = null
) {
    val clientState by clientViewModel.status.collectAsState(initial = ClientStatus.Loading)
    val productState by productViewModel.status.collectAsState(initial = ProductStatus.Loading)

    val clients = remember { mutableStateOf<List<Client>>(emptyList()) }
    val products = remember { mutableStateOf<List<Product>>(emptyList()) }

    var idClient by remember { mutableStateOf(order?.clientId ?: -1L) }

    // Dropdown states
    var expandedClientDropdown by remember { mutableStateOf(false) }
    var expandedProductDropdown by remember { mutableStateOf(false) }


    // Handle client state
    LaunchedEffect(clientState) {
        when (clientState) {
            is ClientStatus.Success -> {
                (clientState as ClientStatus.Success).clients.collect { clientsList ->
                    clients.value = clientsList
                }
            }
            is ClientStatus.Error -> clients.value = emptyList()
            else -> clients.value = emptyList()
        }
    }

    // Handle product state
    LaunchedEffect(productState) {
        when (productState) {
            is ProductStatus.Success -> {
                (productState as ProductStatus.Success).products.collect { productsList ->
                    products.value = productsList
                }
            }
            is ProductStatus.Error -> products.value = emptyList()
            else -> products.value = emptyList()
        }
    }
val context= LocalContext.current
    var selectedClient by remember { mutableStateOf(order?.let { clients.value.find { client -> client.id_client == it.clientId }?.name } ?: context.getString( R.string.select_client)) }
    var shipping by remember { mutableStateOf(0.0) }
    var idOrder by remember { mutableStateOf(order?.id_order ?: -1L) }
    var selectedProductQuantities by remember { mutableStateOf<Map<Long, Prod>>(emptyMap()) }
    val orderDetails by orderViewModel.getOrderDetails(order?.id_order ?: -1L).observeAsState(emptyList())

    LaunchedEffect(orderDetails) {
        selectedProductQuantities = orderDetails.associate { it.productId to Prod(it.nbrBoxes, it.nbrItems) }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.add_edit_order)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues), // Padding from Scaffold
                contentAlignment = Alignment.Center // Center the content
            ) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Client Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expandedClientDropdown,
                        onExpandedChange = { expandedClientDropdown = !expandedClientDropdown }
                    ) {
                        OutlinedTextField(
                            value = selectedClient,
                            onValueChange = {},
                            label = { Text(stringResource(id = R.string.select_client)) },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedClientDropdown,
                            onDismissRequest = { expandedClientDropdown = false }
                        ) {
                            clients.value.forEach { client ->
                                DropdownMenuItem(
                                    text = { Text(client.name) },
                                    onClick = {
                                        selectedClient = client.name
                                        idClient = client.id_client
                                        expandedClientDropdown = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Products Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expandedProductDropdown,
                        onExpandedChange = { expandedProductDropdown = !expandedProductDropdown }
                    ) {
                        OutlinedTextField(
                            value = "Select Products",
                            onValueChange = {},
                            label = { Text(stringResource(id = R.string.select_products)) },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedProductDropdown,
                            onDismissRequest = { expandedProductDropdown = false }
                        ) {
                            products.value.forEach { product ->
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.SpaceBetween){
                                            Text(product.title)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            product.image?.let { ProductImage(it,modifier = Modifier.size(20.dp)) }
                                        }

                                           },
                                    onClick = {
                                        selectedProductQuantities = selectedProductQuantities + (product.id_product to Prod())
                                        expandedProductDropdown = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Display Selected Products and Quantities
                    if (selectedProductQuantities.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(selectedProductQuantities.keys.toList()) { productId ->
                                val product = products.value.find { it.id_product == productId }
                                val prod = selectedProductQuantities[productId] ?: Prod()

                                if (product != null) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        product.image?.let { ProductImage(it,modifier = Modifier.size(20.dp)) }
Spacer(modifier = Modifier.width(8.dp) )
                                        Text(text = product.title, fontWeight = FontWeight.Bold)

                                        var nbrBoxes by remember { mutableStateOf(prod.nbrBoxes.toString()) }
                                        var nbrItems by remember { mutableStateOf(prod.nbrItems.toString()) }

                                        OutlinedTextField(
                                            value = nbrBoxes,
                                            onValueChange = {
                                                nbrBoxes = it
                                                selectedProductQuantities = selectedProductQuantities + (productId to Prod(nbrBoxes.toIntOrNull() ?: 0, prod.nbrItems))
                                            },
                                            label = { Text(stringResource(id = R.string.nbr_boxes)) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.width(120.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp) )

                                        OutlinedTextField(
                                            value = nbrItems,
                                            onValueChange = {
                                                nbrItems = it
                                                selectedProductQuantities = selectedProductQuantities + (productId to Prod(prod.nbrBoxes, nbrItems.toIntOrNull() ?: 1))
                                            },
                                            label = { Text(stringResource(id = R.string.itesm_nbr)) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.width(120.dp)
                                        )

                                        // Delete button
                                        IconButton(onClick = {
                                            selectedProductQuantities = selectedProductQuantities - productId
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete Product"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Order Date Picker (simplified for this example)
                    val orderDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(order?.orderDate ?: System.currentTimeMillis()))
                    Text(text = stringResource(id=R.string.date)+" : $orderDate")

                    Spacer(modifier = Modifier.height(16.dp))

                    // Shipping Input
                    OutlinedTextField(
                        value = shipping.toString(),
                        onValueChange = { shipping = it.toDoubleOrNull() ?: 0.0 },
                        label = { Text(stringResource(id = R.string.shipping)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Save Button
                    Button(
                        onClick = {
                            // Define the new order
                            val newOrder = Order(
                                id_order = idOrder ?: 0L,
                                clientId = idClient,
                                shipping = shipping,
                                orderDate = dateToTimestamp(orderDate)
                            )

                            // Call ViewModel function to save order and details
                            orderViewModel.saveOrderAndDetails(newOrder, selectedProductQuantities)

                            // Navigate back
                            navController.navigate("home/4")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(id = R.string.save))
                    }
                }
            }
        }
    )
}





@Composable
fun ProductImage(imageUri: String,modifier: Modifier=Modifier) {
    val painter = rememberImagePainter(
        data = if (imageUri.isEmpty()) R.drawable.product_icon else Uri.parse(imageUri)
    )

    Image(
        painter = painter,
        contentDescription = "Product Image",
        modifier = modifier
            .clip(CircleShape)
            .background(Color.Gray)
    )
}



fun dateToTimestamp(dateString: String, format: String = "dd/MM/yyyy"): Long {
    val formatter = SimpleDateFormat(format, Locale.getDefault())
    val date = formatter.parse(dateString)
    return date?.time ?: 0L
}
