package com.wagdev.inventorymanagement.products_feature.presentation

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.google.gson.Gson
import com.wagdev.inventorymanagement.R
import com.wagdev.inventorymanagement.core.util.Routes
import com.wagdev.inventorymanagement.products_feature.domain.model.Product
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    productViewModel: ProductViewModel = hiltViewModel()
) {
    val status by productViewModel.status.collectAsState()
    val searchQuery = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableStateOf<Product?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // State to manage how many products to display in a row
    var productsInRow by remember { mutableStateOf(1) }

    // Collect the products flow from the ViewModel
    val products by produceState<List<Product>>(initialValue = emptyList(), status) {
        if (status is ProductStatus.Success) {
            (status as ProductStatus.Success).products.collect { productsList ->
                value = productsList
            }
        }
    }

    Scaffold(
        topBar = {
            Column {
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,

                    ) {
                    Text(stringResource(id = R.string.products_details))
                    Button(onClick = { navController.navigate("addeditproduct") }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Image(
                                painter = painterResource(id = R.drawable.products),
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
                ProductSelector(productsInRow) { newSelection ->
                    productsInRow = newSelection
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (status) {
                is ProductStatus.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ProductStatus.Success -> {
                    val searchQueryValue = searchQuery.value.orEmpty()

                    val filteredItems = if (searchQueryValue.isEmpty()) {
                        products
                    } else {
                        products.filter {
                            it.title.contains(searchQueryValue, ignoreCase = true)
                        }
                    }

                    if (filteredItems.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.no_products_found),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        if (productsInRow == 1) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                items(filteredItems) { product ->
                                    ProductItemView(product, productsInRow) {
                                        selectedItem = product
                                        showBottomSheet = true
                                        scope.launch { sheetState.show() }
                                    }
                                }
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(productsInRow),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(filteredItems) { product ->
                                    ProductItemView(product, productsInRow) {
                                        selectedItem = product
                                        showBottomSheet = true
                                        scope.launch { sheetState.show() }
                                    }
                                }
                            }
                        }
                    }
                }
                is ProductStatus.Error -> {
                    Text(
                        text = (status as ProductStatus.Error).message,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ProductStatus.Idle -> {
                    // Placeholder when idle
                }
            }

            // Show Bottom Sheet
            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                        scope.launch { sheetState.hide() }
                    },
                    sheetState = sheetState
                ) {
                    selectedItem?.let { product ->
                        BottomSheetContent(
                            product = product,
                            onEdit = {
                                val productJson = serializeProduct(product)
                                navController.navigate("addeditproduct?product=$productJson")

                                scope.launch { sheetState.hide() }
                                showBottomSheet = false
                            },
                            onDelete = {
                                productViewModel.onEvent(ProductEvent.deleteProduct(product))
                                scope.launch { sheetState.hide() }
                                showBottomSheet = false

                                navController.navigate(Routes.Home.route + "/3")
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
fun ProductItemView(product: Product, productsInRow: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        if (productsInRow == 1) {
            // Horizontal layout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x95AD644D)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                product.image?.let { ProductImage(it,modifier = Modifier.size(70.dp)) }
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                ) {
                    Text(
                        text = product.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(color = Color.White)
                    )
                    Text(text = stringResource(id = R.string.price)+" : ${product.price}",
                        style = TextStyle(color =Color.White)
                    )
                    Text(text = stringResource(id = R.string.stock)+" : ${product.nbrItems} "+stringResource(id = R.string.items),
                        style = TextStyle(color = Color.White)
                    )
                    Text(text = stringResource(id = R.string.nbr_boxes)+" : ${product.nbrBoxes} "+stringResource(id = R.string.boxes),
                        style = TextStyle(color = Color.White)
                    )
                }
                IconButton(onClick = { /* Handle action */ }) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary // Use a color from your theme
                    )
                }
            }
        } else {
            // Vertical layout
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                product.image?.let { ProductImage(it, modifier = Modifier.size(100.dp)) }
                Text(
                    text = product.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(color = MaterialTheme.colorScheme.onSurface)

                )
                Text(text = stringResource(id = R.string.price)+" : ${product.price}",
                    style = TextStyle(color = MaterialTheme.colorScheme.onSurface)
                )
                Text(text = stringResource(id = R.string.stock)+" : ${product.nbrItems} Items",
                    style = TextStyle(color = MaterialTheme.colorScheme.onSurface)
                )
                Text(text = stringResource(id = R.string.nbr_boxes)+": ${product.nbrBoxes} Boxes",
                    style = TextStyle(color = MaterialTheme.colorScheme.onSurface)
                )
                IconButton(onClick = { /* Handle action */ }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopAppBar(searchQuery: MutableState<String>) {
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
        }
    )
}

@Composable
fun BottomSheetContent(product: Product, onEdit: () -> Unit, onDelete: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Column{
                Text(
                    text = stringResource(id = R.string.product_details),
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(text = stringResource(id = R.string.title)+" : ${product.title}")
                Text(text = stringResource(id=R.string.price)+" : ${product.price}")
                Text(text = stringResource(id=R.string.nbrItemPerBox)+" : ${product.nbrItemsPerBox}")
                Text(text = stringResource(id=R.string.nbr_boxes)+" : ${product.nbrBoxes}")
          }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                product.image?.let { ProductImage(imageUri = it, modifier = Modifier.size(100.dp)) }
                Text(text = stringResource(id=R.string.itesm_nbr)+" : ${product.nbrBoxes*product.nbrItemsPerBox}")

            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

// Helper function to serialize a Product object to JSON
fun serializeProduct(product: Product): String {
    return Uri.encode(Gson().toJson(product))
}
