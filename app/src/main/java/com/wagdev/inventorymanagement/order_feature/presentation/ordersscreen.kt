package com.wagdev.inventorymanagement.orders_feature.presentation

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import com.wagdev.inventorymanagement.clients_feature.domain.model.Client
import com.wagdev.inventorymanagement.clients_feature.presentation.ClientEvent
import com.wagdev.inventorymanagement.clients_feature.presentation.ClientStatus
import com.wagdev.inventorymanagement.clients_feature.presentation.ClientViewModel
import com.wagdev.inventorymanagement.core.util.Routes

import com.wagdev.inventorymanagement.order_feature.domain.model.Order
import com.wagdev.inventorymanagement.order_feature.presentation.OrderEvent
import com.wagdev.inventorymanagement.order_feature.presentation.OrderStatus
import com.wagdev.inventorymanagement.order_feature.presentation.OrderViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.compose.material.icons.filled.Download
import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.filter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.Shader
import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Whatsapp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.wagdev.inventorymanagement.R
import com.wagdev.inventorymanagement.clients_feature.presentation.SearchTopAppBar
import com.wagdev.inventorymanagement.order_feature.domain.model.OrderDetail
import com.wagdev.inventorymanagement.products_feature.domain.model.Product
import com.wagdev.inventorymanagement.products_feature.presentation.ProductEvent
import com.wagdev.inventorymanagement.products_feature.presentation.ProductStatus
import com.wagdev.inventorymanagement.products_feature.presentation.ProductViewModel
import kotlinx.coroutines.delay
import coil.compose.AsyncImage
import com.google.android.gms.common.SignInButton
import com.wagdev.inventorymanagement.products_feature.presentation.serializeProduct

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    orderViewModel: OrderViewModel = hiltViewModel(),
    clientViewModel: ClientViewModel = hiltViewModel()
) {
    //ids
    var selectedClientId by remember {
        mutableStateOf(-1L)
    }

    //states
    val clientState by clientViewModel.status.collectAsState()
    val orderState by orderViewModel.status.collectAsState()
    //search Query
    val searchQuery = remember { mutableStateOf("") }
    //scope
    val scope = rememberCoroutineScope()
    // selected elements
    var selectedOrder by remember { mutableStateOf<Order?>(null) }
    val clients = remember { mutableStateOf<List<Client>>(emptyList()) }
    var selectedClient by remember { mutableStateOf<String?>(null) }

    // bottom sheet status
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var expandedForClient by remember { mutableStateOf(false) }

    // orders in rows

    var ordersInRow by remember { mutableStateOf(1) }

    // Initialize date with the order's date or current date
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    var selectedDate by remember { mutableStateOf( dateFormat.format(calendar.time)) }

    // DatePickerDialog to pick a date
    val datePickerDialog = android.app.DatePickerDialog(
        navController.context,
        { _, year, month, dayOfMonth ->
            selectedDate = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

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

    Scaffold(
        topBar = {
            Column {
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,

                    ) {
                    Text(stringResource(id = R.string.orders_detail),)
                    Button(onClick = { navController.navigate("addeditorder") }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,

                            ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Image(
                                painter = painterResource(id = R.drawable.store),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(25.dp) // Increased size
                                    .padding(end = 6.dp)
                                    .clip(RoundedCornerShape(20.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                }
                SearchTopAppBar(searchQuery)
            }

        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ProductSelector(selectedValue = ordersInRow) { ordersInRow = it }

            // Client Selection Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedForClient,
                onExpandedChange = { expandedForClient = !expandedForClient }
            ) {
                OutlinedTextField(
                    value = selectedClient ?: stringResource(id = R.string.select_client),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedForClient)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(),
                    label = { Text(stringResource(id = R.string.client)) }
                )
                ExposedDropdownMenu(
                    expanded = expandedForClient,
                    onDismissRequest = { expandedForClient = false }
                ) {
                    clients.value.forEach { client ->
                        DropdownMenuItem(
                            text = { Text(client.name) },
                            onClick = {
                                selectedClientId = client.id_client
                                orderViewModel.onEvent(OrderEvent.GetOrderByClient(client.id_client))
                                expandedForClient = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                when (orderState) {
                    is OrderStatus.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is OrderStatus.Success -> {
                        val orders = (orderState as OrderStatus.Success).orders.collectAsState(initial = emptyList())
                        val filteredOrders = orders.value.filter {
                            clientViewModel.onEvent(ClientEvent.GetClientById(it.clientId))
                            val client=clientViewModel.client.collectAsState()
                            client.value?.name?.contains(searchQuery.value, ignoreCase = true) == true
                            //|| it.product.title.contains(searchQuery.value, ignoreCase = true) ||
                            //it.order.orderDate.toString().contains(searchQuery.value, ignoreCase = true)
                        }

                        if (filteredOrders.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.no_orders_found),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            if (ordersInRow == 1) {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(16.dp)
                                ) {
                                    items(filteredOrders) { order ->
                                        OrderItemView(order, ordersInRow) {
                                            selectedOrder = order
                                            showBottomSheet = true
                                            scope.launch { sheetState.show() }
                                        }
                                    }
                                }
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(ordersInRow),
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(filteredOrders) { order ->
                                        OrderItemView(order, ordersInRow) {
                                            selectedOrder = order
                                            showBottomSheet = true
                                            scope.launch { sheetState.show() }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    is OrderStatus.Error -> {
                        Text(
                            text = (orderState as OrderStatus.Error).message,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    OrderStatus.Idle -> TODO()
                }
            }

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                        scope.launch { sheetState.hide() }
                    },
                    sheetState = sheetState
                ) {
                    selectedOrder?.let { order ->
                        BottomSheetContent(
                            order = order,
                            onEdit = {
                                val orderJson = serializeOrder(order)
                                navController.navigate("addeditorder?order=$orderJson")
                                scope.launch { sheetState.hide()
                                    showBottomSheet = false}
                            },
                            onDelete = {
                                orderViewModel.onEvent(OrderEvent.DeleteOrderDetailAll(order.id_order))
                                orderViewModel.onEvent(OrderEvent.DeleteOrder(order.id_order))
                               scope.launch { sheetState.hide()
                                    showBottomSheet = false
                               navController.navigate("home/4")
                               }
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ProductSelector(selectedValue: Int, onSelectionChange: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        listOf(1, 2, 3).forEach { option ->
            Button(
                onClick = { onSelectionChange(option) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedValue == option) Color.Gray else Color.LightGray
                )
            ) {
                Text(text = "$option "+stringResource(id = R.string.cols))
            }
        }
    }
}


@Composable
fun OrderItemView(order: Order, ordersInRow: Int, onClick: () -> Unit) {
    val orderViewModel: OrderViewModel= hiltViewModel()
    val clientViewModel:ClientViewModel= hiltViewModel()
    clientViewModel.onEvent(ClientEvent.GetClientById(order.clientId))
    val client = clientViewModel.client.collectAsState()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        val nbrProducts by orderViewModel.getNbrDetailsPerOrder(order.id_order).collectAsState(initial = 0)

        if (ordersInRow == 1) {
            // Horizontal layout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .background(Color.Transparent)
                ) {

                    client.value?.let {
                        Text(
                            text = it.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(color = MaterialTheme.colorScheme.onSurface)
                        )
                    }
                    Text(text = stringResource(id = R.string.date)+" : ${timestampToDate( order.orderDate)}")
                    Text(text = stringResource(id = R.string.product_nbr)+" : ${nbrProducts} pcs")
                    Text(text = stringResource(id = R.string.shipping)+" : ${order.shipping} DH")

                }
                IconButton(onClick = { /* Handle action */ }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                }
            }
        } else {
            // Vertical layout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    client.value?.let {
                        Text(
                            text = it.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(color = MaterialTheme.colorScheme.onSurface)
                        )
                    }
                    Text(text = stringResource(id = R.string.shipping)+" : ${order.shipping} DH",
                        style = TextStyle(color = MaterialTheme.colorScheme.onSurface)
                    )
                    Text(text = stringResource(id = R.string.date)+" : ${timestampToDate(order.orderDate)}",
                        style = TextStyle(color = MaterialTheme.colorScheme.onSurface)
                    )
                    Text(text = stringResource(id = R.string.product_nbr)+" : ${nbrProducts} prds",
                        style = TextStyle(color = MaterialTheme.colorScheme.onSurface)
                    )
                }
                IconButton(onClick = { /* Handle action */ }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}


@Composable
fun OrderSelector(selectedValue: Int, onSelectionChange: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        listOf(1, 2, 3).forEach { option ->
            Button(
                onClick = { onSelectionChange(option) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedValue == option) Color.Gray else Color.LightGray
                )
            ) {
                Text(text = "$option "+stringResource(id = R.string.cols))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopAppBar1(
    searchQuery: MutableState<String>,
    orderViewModel: OrderViewModel = hiltViewModel(),
    clientViewModel: ClientViewModel= hiltViewModel(),
    context: Context
) {
    val orderState by orderViewModel.status.collectAsState()

    // Prepare a filtered orders list
    val orders = (orderState as OrderStatus.Success).orders.collectAsState(initial = emptyList())
    val filteredOrders = orders.value.filter {
        clientViewModel.onEvent(ClientEvent.GetClientById(it.clientId))
        val client=clientViewModel.client.collectAsState()
        client.value?.name?.contains(searchQuery.value, ignoreCase = true) == true
                //|| it.product.title.contains(searchQuery.value, ignoreCase = true) ||
                //it.order.orderDate.toString().contains(searchQuery.value, ignoreCase = true)
    }

    TopAppBar(
        title = {
            OutlinedTextField(
                value = searchQuery.value,
                onValueChange = { searchQuery.value = it },
                placeholder = { Text(text = stringResource(id = R.string.search)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { /* Handle search action */ }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },

    )
}

@Composable
fun BottomSheetContent(
    order: Order,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    clientViewModel: ClientViewModel = hiltViewModel(),
    orderViewModel: OrderViewModel = hiltViewModel(),
    productViewModel: ProductViewModel = hiltViewModel()
) {
    // Load client and order details
    clientViewModel.onEvent(ClientEvent.GetClientById(order.clientId))
    val client = clientViewModel.client.collectAsState()
    val orderDetails = orderViewModel.orderDetail.collectAsState()
    val product by productViewModel.product.collectAsState() // Collect product state as LiveData/State

    orderViewModel.onEvent(OrderEvent.GetOrderDetail(order.id_order))
    val productState by productViewModel.status.collectAsState(initial = ProductStatus.Loading)
    val products = remember { mutableStateOf<List<Product>>(emptyList()) }
    val productMap = remember { mutableStateMapOf<Long, Product?>() }

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

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White) // Background color
    ) {
        item {
            Text(
                text = stringResource(id = R.string.order_detail),
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        client.value?.let {
            item {
                Text(
                    text = it.name,
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        item {
            DetailRow(label = stringResource(id = R.string.shipping), value = "${order.shipping} DH")
        }

        item {
            DetailRow(label = stringResource(id = R.string.date), value = timestampToDate(order.orderDate))
        }

        // Display the number of products
        item {
            DetailRow(label = stringResource(id = R.string.product_nbr), value = "${orderDetails.value.size} Pcs")
        }

        // Display order details if available
        items(orderDetails.value) { detail ->
            // Load product details for each order detail
            LaunchedEffect(detail.productId) {
                productViewModel.onEvent(ProductEvent.getProductById(detail.productId)) { product ->
                    productMap[detail.productId] = product
                }
            }

            // Render UI based on whether the product data is available for this detail
            val product = productMap[detail.productId]
            when {
                product == null -> {
                    // Show a loading view or empty state while waiting for product data
                    LoadingView()
                }
                else -> {
                    // Once the product data is available, show the OrderDetailRow
                    OrderDetailRow(detail = detail, product = product, onDelete = {id_pro,id_ord->
                        orderViewModel.onEvent(OrderEvent.DeleteOrderDetail(id_ord,id_pro))
                    })
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onEdit,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text(stringResource(id = R.string.edit), color = Color.White)
                }
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(stringResource(id = R.string.delete), color = Color.White)
                }
                val context= LocalContext.current

                IconButton(onClick = {

                    generateAndDownloadPdf(orderDetails.value, context, client.value, products.value,order)

                }) {
                    Icon(Icons.Default.Download, contentDescription = "Download PDF")
                }
                IconButton(onClick = {
                    // Share the PDF file
                    val file = generateAndDownloadPdf(orderDetails.value, context, client.value, products.value, order)
                    if (file != null) {
                        sharePdfFile(context, file)
                    }
                }) {
                    Icon(Icons.Default.Share, contentDescription = "Share")
                }

                IconButton(onClick = {
                    // Share via WhatsApp
                    val file = generateAndDownloadPdf(orderDetails.value, context, client.value, products.value, order)
                    if (file != null) {
                        shareViaWhatsApp(context, file)
                    }
                }) {
                    Icon(Icons.Default.Whatsapp, contentDescription = "WhatsApp", modifier = Modifier.background(Color.Green))
                }
            }
        }
    }
}
fun sharePdfFile(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.shareFile)))
}
fun shareViaWhatsApp(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

    val whatsappIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        setPackage("com.whatsapp") // Ensure only WhatsApp opens
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    try {
        context.startActivity(whatsappIntent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, context.getString(R.string.whatsapp_not_found), Toast.LENGTH_SHORT).show()
    }
}


@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
        )
        Text(
            text = value,
            style = TextStyle(fontSize = 16.sp)
        )
    }
}



@Composable
fun OrderDetailRow(
    detail: OrderDetail,
    product: Product,
    onDelete: (productId: Long, orderId: Long) -> Unit // Corrected parameter for deletion
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0xFFF7F7F7)) // Background color for detail rows
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically // Ensure alignment of image and text
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Display the product image
            AsyncImage(
                model = product.image ?: R.drawable.product_icon, // Use default image if null
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape) // Circular shape for the image
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(12.dp)) // Add some space between image and text

            Column {
                Text(text = stringResource(id = R.string.product)+" : ${product.title}", fontWeight = FontWeight.Bold)
                Text(text = stringResource(id = R.string.price)+" : ${product.price} DH")
            }
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(text = stringResource(id=R.string.nbr_boxes)+" : ${detail.nbrBoxes}", fontWeight = FontWeight.Bold)
            Text(text = stringResource(id = R.string.itesm_nbr)+" : ${detail.nbrItems}")
        }

        IconButton(onClick = { onDelete(detail.productId, detail.orderId) }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Product")
        }
    }
}

@Composable
fun LoadingView() {
    Text(
        text = stringResource(id = R.string.loading_product_data),
        style = TextStyle(fontStyle = FontStyle.Italic),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}








//fun serializeOrder(order: Order): String {
  //  return Gson().toJson(order)
//}
fun serializeOrder(order: Order): String {
    return Uri.encode(Gson().toJson(order))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSection(
    clients: List<String>,
    selectedClient: String?,
    onClientSelected: (String) -> Unit,
    selectedDate: String?,
    onDateSelected: (String) -> Unit,
    openDatePicker: () -> Unit // Add callback to trigger date picker
) {
    // Dropdown states
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {


        // Date Selection
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = selectedDate?.let { stringResource(id = R.string.pick_date)+" $it" } ?: stringResource(id = R.string.pick_date),
                modifier = Modifier.clickable {
                    openDatePicker() // Trigger date picker
                }
            )
            IconButton(onClick = {
                openDatePicker() // Trigger date picker
            }) {
                Icon(Icons.Filled.CalendarToday, contentDescription = null)
            }
        }
    }
}


@SuppressLint("ResourceAsColor")
fun generateAndDownloadPdf(
    orders: List<OrderDetail>,
    context: Context,
    client: Client?,
    products: List<Product>,
    order: Order
): File? {
    val pdfDocument = PdfDocument()
    val textPaint = Paint().apply {
        textSize = 16f
        isAntiAlias = true
        color = com.wagdev.inventorymanagement.R.color.black
    }
    val titlePaint = Paint().apply {
        textSize = 24f
        isFakeBoldText = true
        color = com.wagdev.inventorymanagement.R.color.black
    }
    val headerPaint = Paint().apply {
        textSize = 18f
        isFakeBoldText = true
        color = com.wagdev.inventorymanagement.R.color.white
    }
    val cellPaint = Paint().apply {
        textSize = 16f
        color = com.wagdev.inventorymanagement.R.color.black
    }

    val pageInfo = PdfDocument.PageInfo.Builder(792, 1120, 1).create()
    var page = pdfDocument.startPage(pageInfo)
    var canvas = page.canvas

    // Load and draw the static image (logo or icon)
    val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.inventorylogo) // replace with your image resource
    val scaledLogoBitmap = Bitmap.createScaledBitmap(logoBitmap, 100, 100, false) // adjust size as needed
    canvas.drawBitmap(scaledLogoBitmap, 670f, 40f, null) // adjust position as needed

    canvas.drawText("Wagazi", 40f, 60f, titlePaint)
    canvas.drawText("2 res essafea", 40f, 85f, textPaint)
    canvas.drawText("Hay Essalam", 40f, 105f, textPaint)
    canvas.drawText("Agadir", 40f, 125f, textPaint)

    canvas.drawText("Invoice Number", 400f, 60f, textPaint)
    canvas.drawText(order.id_order.toString(), 550f, 60f, textPaint)
    canvas.drawText("ORDER DATE : ", 400f, 90f, textPaint)
    canvas.drawText(timestampToDate(order.orderDate), 550f, 90f, textPaint)

    canvas.drawText("BILLED TO", 40f, 180f, titlePaint)
    canvas.drawText("CUSTOMER : Mr ", 40f, 240f, textPaint)
    client?.name?.let { canvas.drawText(it, 140f, 240f, textPaint) }
    canvas.drawText("address : ", 40f, 260f, textPaint)
    client?.address?.let { canvas.drawText(it, 150f, 260f, textPaint) }

    canvas.drawRect(40f, 300f, 750f, 340f, headerPaint)
    canvas.drawText("IMAGE", 60f, 320f, cellPaint)
    canvas.drawText("ITEMS", 140f, 320f, cellPaint)
    canvas.drawText("PRICE", 300f, 320f, cellPaint)
    canvas.drawText("QTY", 400f, 320f, cellPaint)
    canvas.drawText("SUBTOTAL", 600f, 320f, cellPaint)

    var yOffset = 340f
    var grandTotal = 0.0
    val topMargin = 20f

    orders.forEach { detail ->
        val product = products.find { it.id_product == detail.productId }
        product?.let {
            val imagePath = it.image ?: ""
            val imageBitmap = if (imagePath.isNotBlank()) {
                loadImageBitmap(imagePath, context)
            } else {
                null
            }

            imageBitmap?.let { bitmap ->
                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, false)
                canvas.drawBitmap(scaledBitmap, 60f, yOffset, null)
            } ?: run {
                canvas.drawText("No Image", 60f, yOffset + 20f, textPaint)
            }

            val itemTotal = it.price * detail.nbrItems
            grandTotal += itemTotal

            canvas.drawText(it.title, 140f, yOffset + 20f, textPaint)
            canvas.drawText("${"%.2f".format(it.price)} DH", 300f, yOffset + 20f, textPaint)
            canvas.drawText("${detail.nbrItems}", 400f, yOffset + 20f, textPaint)
            canvas.drawText("${"%.2f".format(itemTotal)} DH", 600f, yOffset + 20f, textPaint)

            yOffset += 100f + topMargin

            if (yOffset > pageInfo.pageHeight - 200) {
                pdfDocument.finishPage(page)
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                yOffset = 60f
            }
        }
    }

    // Draw totals
    canvas.drawLine(40f, yOffset + 10f, 750f, yOffset + 10f, textPaint)
    canvas.drawText("SUBTOTAL", 500f, yOffset + 40f, textPaint)
    canvas.drawText("${"%.2f".format(grandTotal)} DH", 600f, yOffset + 40f, textPaint)
    canvas.drawText("SHIPPING ", 500f, yOffset + 60f, textPaint)
    canvas.drawText("${order.shipping} DH", 600f, yOffset + 60f, textPaint)
    canvas.drawText("TAX", 500f, yOffset + 80f, textPaint)
    canvas.drawText("0 DH", 600f, yOffset + 80f, textPaint)
    canvas.drawText(" TOTAL", 250f, yOffset + 120f, titlePaint)
    canvas.drawText(
        "${"%.2f".format(grandTotal + 0.0 + order.shipping)} DH",
        400f,
        yOffset + 120f,
        titlePaint
    )
    canvas.drawText("Signature", 650f, yOffset + 120f, textPaint)
    pdfDocument.finishPage(page)

    val fileName = "Orders_${System.currentTimeMillis()}.pdf"
    val downloadsDir =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val filePath = File(downloadsDir, fileName)

    try {
        pdfDocument.writeTo(FileOutputStream(filePath))
        pdfDocument.close()

        Toast.makeText(context, "PDF saved: ${filePath.absolutePath}", Toast.LENGTH_LONG).show()

        scanFile(context, filePath)

        return filePath // Return the File object

    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to save PDF: ${e.message}", Toast.LENGTH_SHORT).show()
    }

    return null // Return null in case of failure
}





// Helper function to load image from Uri
private fun loadImageBitmap(imagePath: String, context: Context): Bitmap? {
    return try {
        val file = File(imagePath)
        if (file.exists()) {
            BitmapFactory.decodeFile(file.absolutePath)
        } else {
            Log.e("PDF_GENERATOR", "File does not exist: $imagePath")
            null
        }
    } catch (e: Exception) {
        Log.e("PDF_GENERATOR", "Failed to load image: ${e.message}")
        null
    }
}



private fun scanFile(context: Context, file: File) {
    val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
    val contentUri = Uri.fromFile(file)
    mediaScanIntent.setData(contentUri)
    context.sendBroadcast(mediaScanIntent)
}

private fun openPdfFile(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    val packageManager = context.packageManager
    val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
    if (resolveInfo != null) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, context.getString(R.string.pdf_no_apptoopen), Toast.LENGTH_SHORT).show()
    }
}



private const val CHANNEL_ID = "myapp_channel"
private const val NOTIFICATION_ID = 1

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "MyApp Channel"
        val descriptionText = "Channel for MyApp notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun showSuccessNotification(context: Context, filePath: String) {
    createNotificationChannel(context)

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(FileProvider.getUriForFile(context, "${context.packageName}.provider", File(filePath)), "application/pdf")
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(com.wagdev.inventorymanagement.R.drawable.ic_notifications) // Replace with your app's notification icon
        .setContentTitle(context.getString(R.string.pdf_download_success1))
        .setContentText(context.getString(R.string.pdf_download_success))
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notify(NOTIFICATION_ID, notification)
    }
}

fun timestampToDate(timestamp: Long, format: String = "dd/MM/yyyy"): String {
    val formatter = SimpleDateFormat(format, Locale.getDefault())
    val date = Date(timestamp)
    return formatter.format(date)
}



