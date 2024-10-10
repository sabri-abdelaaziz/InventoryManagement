package com.wagdev.inventorymanagement.core.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment

import com.wagdev.inventorymanagement.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfFileItem(file: File, context: Context, navController: NavController) {
    // Format the last modified date
    val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
    val lastModifiedDate = dateFormat.format(file.lastModified())

    // Define the row layout for the PDF file item
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { openPdf(context, file) }, // Add this line
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

        // Share button
        IconButton(
            onClick = { sharePdf(context, file) },
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Icon(imageVector = Icons.Filled.Share, contentDescription = "Share")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfListScreen(context: Context, navController: NavController) {
    val pdfFiles = remember { mutableStateListOf<File>() }
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    // Load files only once using LaunchedEffect
    LaunchedEffect(key1 = downloadsDir) {
        pdfFiles.clear()  // Clear the list before adding new files to avoid duplicates
        val files = downloadsDir.listFiles { file -> file.extension == "pdf" }
        files?.let { pdfFiles.addAll(it.sortedByDescending { it.lastModified() }) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.orderDownloads)) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
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
                if (pdfFiles.isEmpty()) {
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
                    items(pdfFiles.size) { index ->
                        val file = pdfFiles[index]
                        PdfFileItem(file, context, navController)
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


fun openPdf(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(uri, "application/pdf")
    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION  // Grant temporary read permission

    context.startActivity(Intent.createChooser(intent, "Open PDF"))
}
