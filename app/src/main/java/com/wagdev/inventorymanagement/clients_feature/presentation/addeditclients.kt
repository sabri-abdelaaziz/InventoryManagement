package com.wagdev.inventorymanagement.clients_feature.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import com.wagdev.inventorymanagement.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.wagdev.inventorymanagement.clients_feature.domain.model.Client
import com.wagdev.inventorymanagement.core.util.Routes

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddEditClientScreen(
    navController: NavController,
    clientViewModel: ClientViewModel = hiltViewModel(),
    client: Client? = null // Receive the Client object
) {
    var id by remember { mutableStateOf(client?.id_client ?: 0) }
    var name by remember { mutableStateOf(client?.name ?: "") }
    var number by remember { mutableStateOf(client?.phoneNumber ?: "") }
    var address by remember { mutableStateOf(client?.address ?: "") }
    var email by remember { mutableStateOf(client?.email ?: "") }

    // Error states for validation
    var nameError by remember { mutableStateOf<String?>(null) }
    var numberError by remember { mutableStateOf<String?>(null) }
    var addressError by remember { mutableStateOf<String?>(null) }
    val context= LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, Modifier.size(20.dp))
                    }
                },
                title = {
                    Text(
                        text = if (client != null) stringResource(id = R.string.edit_client) else stringResource(id = R.string.add_client),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                },
                actions = {
                    Icon(Icons.Default.Settings, contentDescription = null)
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .background(Color.White),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Name Input with Error Handling
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(id = R.string.name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = nameError != null
                )
                if (nameError != null) {
                    Text(text = nameError!!, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Number Input with Error Handling
                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it },
                    label = { Text(stringResource(id = R.string.phone)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = numberError != null
                )
                if (numberError != null) {
                    Text(text = numberError!!, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Address Input with Error Handling
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text(stringResource(id = R.string.addr)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = addressError != null
                )
                if (addressError != null) {
                    Text(text = addressError!!, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Save Button with Validation
                Button(
                    onClick = {
                        // Reset error messages
                        nameError = null
                        numberError = null
                        addressError = null

                        // Validate inputs
                        val hasError = when {
                            name.isEmpty() -> {
                                nameError = context.getString( R.string.error_name_empty)
                                true
                            }
                            number.isEmpty() -> {
                                numberError = context.getString( R.string.error_phone_empty)
                                true
                            }
                            address.isEmpty() -> {
                                addressError = context.getString( R.string.error_address_empty)
                                true
                            }
                            else -> false
                        }

                        if (!hasError) {
                            val clientToSave = Client(
                                id_client = id,
                                name = name,
                                phoneNumber = number,
                                email = email,
                                address = address,
                            )
                            clientViewModel.onEvent(ClientEvent.AddEditClient(clientToSave))
                            navController.navigate(Routes.Home.route + "/3")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                ) {
                    Text(if (client == null) stringResource(id = R.string.add_client) else stringResource(id = R.string.save), color = Color.White)
                }
            }
        }
    }
}
