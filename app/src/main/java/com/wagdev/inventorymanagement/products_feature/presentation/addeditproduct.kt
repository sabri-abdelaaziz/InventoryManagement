package com.wagdev.inventorymanagement.products_feature.presentation

import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.wagdev.inventorymanagement.R
import com.wagdev.inventorymanagement.core.util.Routes
import com.wagdev.inventorymanagement.products_feature.domain.model.Product
import com.wagdev.inventorymanagement.products_feature.presentation.ProductEvent
import com.wagdev.inventorymanagement.products_feature.presentation.ProductViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    navController: NavController,
    productViewModel: ProductViewModel = hiltViewModel(),
    product: Product? = null
) {
    val id by remember { mutableLongStateOf(product?.id_product ?: 0) }
    var title by remember { mutableStateOf(product?.title ?: "") }
    var price by remember { mutableStateOf(product?.price?.toString() ?: "") }
    var nbrBoxes by remember { mutableStateOf(product?.nbrBoxes?.toString() ?: "") }
    var nbrItemPerBox by remember { mutableStateOf(product?.nbrItemsPerBox?.toString() ?: "") }

    // Derived state for calculating `nbrItems`
    val nbrItems by derivedStateOf {
        val boxes = nbrBoxes.toIntOrNull() ?: 0
        val itemsPerBox = nbrItemPerBox.toIntOrNull() ?: 0
        (boxes * itemsPerBox).toString()
    }

    var imageUri by remember { mutableStateOf(product?.image?.toString() ?: "") }
    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = saveImageLocally(context, it)
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            imageUri = saveImageLocally(context, it)
        }
    }

    // States for error messages
    var titleError by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }
    var nbrBoxesError by remember { mutableStateOf(false) }
    var nbrItemPerBoxError by remember { mutableStateOf(false) }

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
                        text = if (product != null) stringResource(id = R.string.edit_product) else stringResource(id = R.string.add_product),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
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
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .background(Color.White),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Image section
                ProductImage(imageUri, Modifier.size(80.dp))

                Spacer(modifier = Modifier.height(20.dp))

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = it.isEmpty()
                    },
                    label = { Text(stringResource(id = R.string.title)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = titleError
                )
                if (titleError) {
                    Text(
                        text = stringResource(id = R.string.error_title),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Price Input
                OutlinedTextField(
                    value = price,
                    onValueChange = {
                        price = it
                        priceError = it.isEmpty() || it.toDoubleOrNull() == null
                    },
                    label = { Text(stringResource(id = R.string.price)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = priceError
                )
                if (priceError) {
                    Text(
                        text = stringResource(id = R.string.error_price),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Number of Items per Box Input
                OutlinedTextField(
                    value = nbrItemPerBox,
                    onValueChange = {
                        nbrItemPerBox = it
                        nbrItemPerBoxError = it.isEmpty() || it.toIntOrNull() == null
                    },
                    label = { Text(stringResource(id = R.string.nbrItemPerBox)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = nbrItemPerBoxError
                )
                if (nbrItemPerBoxError) {
                    Text(
                        text = stringResource(id = R.string.error_nbrItemPerBox),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Number of Boxes Input
                OutlinedTextField(
                    value = nbrBoxes,
                    onValueChange = {
                        nbrBoxes = it
                        nbrBoxesError = it.isEmpty() || it.toIntOrNull() == null
                    },
                    label = { Text(stringResource(id = R.string.nbr_boxes)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = nbrBoxesError
                )
                if (nbrBoxesError) {
                    Text(
                        text = stringResource(id = R.string.error_nbr_boxes),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Number of Items (Derived from nbrBoxes * nbrItemsPerBox)
                OutlinedTextField(
                    value = nbrItems,
                    onValueChange = { /* nbrItems is derived, so no direct change */ },
                    label = { Text(stringResource(id = R.string.itesm_nbr)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = false // Disable editing since it's derived
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Image Picker and Camera
                Text(stringResource(id = R.string.takeoruploadphoto))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = { cameraLauncher.launch() }) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null)
                    }
                    IconButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                        Icon(Icons.Default.Photo, contentDescription = null)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Save Button
                Button(
                    onClick = {
                        // Validation check before saving
                        titleError = title.isEmpty()
                        priceError = price.isEmpty() || price.toDoubleOrNull() == null
                        nbrItemPerBoxError = nbrItemPerBox.isEmpty() || nbrItemPerBox.toIntOrNull() == null
                        nbrBoxesError = nbrBoxes.isEmpty() || nbrBoxes.toIntOrNull() == null

                        if (!titleError && !priceError && !nbrItemPerBoxError && !nbrBoxesError) {
                            val productToSave = Product(
                                id_product = id,
                                title = title,
                                price = price.toDouble(),
                                nbrItems = nbrItems.toInt(),
                                nbrBoxes = nbrBoxes.toInt(),
                                nbrItemsPerBox = nbrItemPerBox.toInt(),
                                image = imageUri
                            )
                            productViewModel.onEvent(ProductEvent.addEditProduct(productToSave))
                            navController.navigate(Routes.Home.route + "/2")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                ) {
                    Text(if (product == null) stringResource(id = R.string.add_product) else stringResource(
                        id = R.string.save
                    ), color = Color.White)
                }
            }
        }
    }
}



@Composable
fun ProductImage(imageUri: String,modifier: Modifier=Modifier) {
    val painter = rememberImagePainter(
        data = if (imageUri.isEmpty()) R.drawable.product_icon else Uri.parse(imageUri)
    )

    Image(
        painter = painter,
        contentDescription = "Product Image",
        contentScale = ContentScale.Crop, // Ensures the image scales to fill the entire space
        modifier = modifier
            .fillMaxSize()  // Ensures the image fills the available space
            .clip(CircleShape)  // Clips the image to a circular shape
            .background(Color.Gray)  // Adds a background if needed
    )

}

fun saveImageLocally(context: android.content.Context, uri: Uri): String {
    val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    return saveImageLocally(context, bitmap)
}

fun saveImageLocally(context: android.content.Context, bitmap: Bitmap): String {
    val filename = "${UUID.randomUUID()}.jpg"
    val directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File(directory, filename)

    return try {
        val outputStream: OutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}
