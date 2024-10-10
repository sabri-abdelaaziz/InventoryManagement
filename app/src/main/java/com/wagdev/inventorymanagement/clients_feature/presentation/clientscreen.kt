package com.wagdev.inventorymanagement.clients_feature.presentation

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import com.wagdev.inventorymanagement.R
import com.wagdev.inventorymanagement.clients_feature.domain.model.Client
import com.wagdev.inventorymanagement.core.util.Routes
import kotlinx.coroutines.launch




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ClientViewModel = hiltViewModel()
) {
    val clientState by viewModel.status.collectAsState()
    val searchQuery = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var selectedClient by remember { mutableStateOf<Client?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // State to manage how many products to display in a row
    var productsInRow by remember { mutableStateOf(1) }

    // Collect the clients flow from the ViewModel
    val clients by produceState<List<Client>>(initialValue = emptyList(), clientState) {
        if (clientState is ClientStatus.Success) {
            (clientState as ClientStatus.Success).clients.collect { clientsList ->
                value = clientsList
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
                    Text(stringResource(id= R.string.c_details),)
                    Button(onClick = { navController.navigate("addeditclient") }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,

                            ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Image(
                                painter = painterResource(id = com.wagdev.inventorymanagement.R.drawable.clients),
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
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (clientState) {
                is ClientStatus.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ClientStatus.Success -> {

                    val searchQueryValue = searchQuery.value.orEmpty()

                    val filteredClients = if (searchQueryValue.isEmpty()) {
                        clients
                    } else {
                        clients.filter {
                            it.name.contains(searchQueryValue, ignoreCase = true)
                        }
                    }

                    if (filteredClients.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.no_clients_found),
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
                                items(filteredClients) { client ->
                                    ClientItemView(client, isHorizontal = true) {
                                        selectedClient = client
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
                                items(filteredClients) { client ->
                                    ClientItemView(client, isHorizontal = false) {
                                        selectedClient = client
                                        showBottomSheet = true
                                        scope.launch { sheetState.show() }
                                    }
                                }
                            }
                        }
                    }
                }
                is ClientStatus.Error -> {
                    val message = (clientState as ClientStatus.Error).message
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ClientStatus.Idle -> {
                    Text(
                        text = stringResource(id = R.string.no_clients_found),
                        modifier = Modifier.align(Alignment.Center)
                    )
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
                    selectedClient?.let { client ->
                        BottomSheetContent(
                            client = client,
                            onEdit = {
                                val clientSer = serializeClient(client)
                                navController.navigate("addeditclient?client=${clientSer}")
                                scope.launch { sheetState.hide() }
                                showBottomSheet = false
                            },
                            onDelete = {
                                viewModel.onEvent(ClientEvent.DeleteClient(client))
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
                Text(text = "$option"+stringResource(id = R.string.cols))
            }
        }
    }
}

@Composable
fun ClientItemView(
    client: Client,
    isHorizontal: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        if (isHorizontal) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = com.wagdev.inventorymanagement.R.drawable.clients),
                    contentDescription = client.name,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(end = 16.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surface),
                ) {
                    Text(
                        text = client.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(color = MaterialTheme.colorScheme.onSurface)

                    )
                    Text(text = stringResource(id = R.string.phone)+" : ${client.phoneNumber}",
                        style = TextStyle(color = MaterialTheme.colorScheme.onSurface)
                    )
                    Text(text = stringResource(id = R.string.addr)+" : ${client.address}",
                        style = TextStyle(color = MaterialTheme.colorScheme.onSurface)
                    )
                }
                IconButton(
                    onClick = { /* Handle Details action */ },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .fillMaxHeight(), // Ensures the column uses the full height
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = com.wagdev.inventorymanagement.R.drawable.clients),
                    contentDescription = client.name,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .align(Alignment.CenterHorizontally),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = client.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = TextStyle(color = MaterialTheme.colorScheme.onSurface)

                )
                Text(text = stringResource(id = R.string.phone)+" : ${client.phoneNumber}", modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = TextStyle(color = MaterialTheme.colorScheme.onSurface)
                )
                Text(text = stringResource(id = R.string.addr)+" : ${client.address}", modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = TextStyle(color = MaterialTheme.colorScheme.onSurface)
                )
                Spacer(modifier = Modifier.height(8.dp))
                IconButton(
                    onClick = { /* Handle Details action */ },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
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
fun BottomSheetContent(client: Client, onEdit: () -> Unit, onDelete: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.client_details),
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = com.wagdev.inventorymanagement.R.drawable.clients),
                contentDescription = client.name,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = stringResource(id = R.string.name)+": ${client.name}")
                Text(text = stringResource(id = R.string.phone)+": ${client.phoneNumber}")
                Text(text = stringResource(id = R.string.addr)+": ${client.address}")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onEdit, colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)) {
            Text(stringResource(id = R.string.edit))
            }
            Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
            Text(stringResource(id = R.string.delete))
            }
        }
    }
}

fun serializeClient(client: Client): String {
    return Gson().toJson(client)
}
@Composable
fun ClientItemView1(client: Client, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = com.wagdev.inventorymanagement.R.drawable.clients),
                contentDescription = client.name,
                modifier = Modifier
                    .size(100.dp)
                    .padding(end = 16.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text(
                    text = client.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = stringResource(id = R.string.phone)+" : ${client.phoneNumber}")
                Text(text = stringResource(id = R.string.phone)+" : ${client.address}")
            }
            IconButton(onClick = { /* Handle Details action */ }) {
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }
        }
    }
}





