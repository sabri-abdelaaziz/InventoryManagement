package com.wagdev.inventorymanagement.core.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.wagdev.inventorymanagement.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Whatsapp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.wagdev.inventorymanagement.core.util.Routes
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfFileItem(
    file: File,
    context: Context,
    navController: NavController,
    onDelete: (File) -> Unit // Callback for delete action
) {
    // Format the last modified date
    val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
    val lastModifiedDate = dateFormat.format(file.lastModified())

    // Define the row layout for the PDF file item
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { openPdf(context, file) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Display file name
            Text(
                text = file.name,
                fontSize = 13.sp,
                modifier = Modifier.padding(8.dp)
            )

            // Display last modified date
            Text(
                text = lastModifiedDate,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )
        }

        Row {
            // Share button
            IconButton(
                onClick = { sharePdf(context, file) },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(imageVector = Icons.Filled.Share, contentDescription = "Share")
            }

            // WhatsApp button
            IconButton(
                onClick = { sendPdfToWhatsApp(context, file) },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(imageVector = Icons.Filled.Whatsapp, contentDescription = "WhatsApp", tint = Color.Green)
            }

            // Delete button
            IconButton(
                onClick = { onDelete(file) },  // Trigger delete action
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,  // Use your delete icon resource
                    contentDescription = "Delete",
                    tint = Color.Red
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfListScreen(context: Context, navController: NavController) {
    val pdfFiles = remember { mutableStateListOf<File>() }
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    var searchText by remember { mutableStateOf("") }  // Add a search state

    // Load files only once using LaunchedEffect
    LaunchedEffect(key1 = downloadsDir) {
        pdfFiles.clear()  // Clear the list before adding new files to avoid duplicates
        val files = downloadsDir.listFiles { file -> file.extension == "pdf" }
        files?.let { pdfFiles.addAll(it.sortedByDescending { it.lastModified() }) }
    }

    // Function to delete a file and update the list
    fun deleteFile(file: File) {
        if (file.exists()) {
            file.delete()
            pdfFiles.remove(file)  // Update the list after deletion
            Toast.makeText(context, "File deleted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed to delete file", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Search Text Field
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            placeholder = { Text(text = stringResource(id = R.string.search)) },
                            modifier = Modifier.weight(3f),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 16.sp,
                                color = Color.Black
                            ),
                            trailingIcon = {
                                IconButton(onClick = { /* Handle search action */ }) {
                                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                                }
                            }
                        )
                        IconButton(
                            modifier = Modifier.weight(1f)
                            ,onClick = {
                            navController.navigate(Routes.Home.route)
                        }) {
                            Icon(imageVector = Icons.Filled.Home, contentDescription = "Back")
                        }
                    }
                }
            )
        },
        content = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)  // Adjust padding for the Scaffold's top bar
            ) {
                // Filtered file list based on search query
                val filteredPdfFiles = pdfFiles.filter { it.name.contains(searchText, ignoreCase = true) }

                if (filteredPdfFiles.isEmpty()) {
                    item {
                        Text(
                            text = "No order files available.",
                            fontSize = 18.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    items(filteredPdfFiles.size) { index ->
                        val file = filteredPdfFiles[index]
                        PdfFileItem(
                            file = file,
                            context = context,
                            navController = navController,
                            onDelete = { deleteFile(it) }  // Handle file deletion
                        )
                    }
                }
            }
        }
    )
}


fun sharePdf(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION // Grant temporary read permission
    }

    context.startActivity(Intent.createChooser(intent, "Share PDF"))
}

fun sendPdfToWhatsApp(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

    val intent = Intent(Intent.ACTION_SEND).apply {
        setPackage("com.whatsapp") // Target WhatsApp
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION // Grant temporary read permission
    }

    try {
        context.startActivity(Intent.createChooser(intent, "Send to WhatsApp"))
    } catch (e: Exception) {
        Toast.makeText(context, "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
    }
}

fun openPdf(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(uri, "application/pdf")
    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION  // Grant temporary read permission

    context.startActivity(Intent.createChooser(intent, "Open PDF"))
}
