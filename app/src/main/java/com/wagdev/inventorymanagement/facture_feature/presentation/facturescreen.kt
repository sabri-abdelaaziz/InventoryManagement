package com.wagdev.inventorymanagement.facture_feature.presentation

import android.content.Context
import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Composable
fun FactureScreen() {
    val context = LocalContext.current
    var selectedClient by remember { mutableStateOf("") }
    var selectedProduct by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var factureData by remember { mutableStateOf("No data available") }
    var pdfUri by remember { mutableStateOf<Uri?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Filter Section
        Text(text = "Filter Options", fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))

        OutlinedTextField(
            value = selectedClient,
            onValueChange = { selectedClient = it },
            label = { Text("Client") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = selectedProduct,
            onValueChange = { selectedProduct = it },
            label = { Text("Product") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        OutlinedTextField(
            value = selectedDate,
            onValueChange = { selectedDate = it },
            label = { Text("Date") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Fetch and filter data based on selected criteria
            factureData = "Filtered data based on Client: $selectedClient, Product: $selectedProduct, Date: $selectedDate"
        }) {
            Text("Filter Data")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display Filtered Data
        Text(text = "Facture Data", fontSize = 20.sp)
        Text(text = factureData, modifier = Modifier.padding(vertical = 8.dp))

        Spacer(modifier = Modifier.height(16.dp))

        // Download PDF Button
        Button(onClick = {
            val pdfFile = savePdf(context, factureData)
            if (pdfFile != null) {
                pdfUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    pdfFile
                )
                openPdf(context, pdfUri)
            }
        }) {
            Text("Download and View PDF")
        }
    }
}

private fun savePdf(context: Context, data: String): File? {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
    val page = pdfDocument.startPage(pageInfo)

    val canvas = page.canvas
    val paint = android.graphics.Paint()
    paint.textSize = 16f
    canvas.drawText(data, 10f, 25f, paint)

    pdfDocument.finishPage(page)

    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Facture.pdf")
    try {
        pdfDocument.writeTo(FileOutputStream(file))
        Toast.makeText(context, "PDF saved to ${file.absolutePath}", Toast.LENGTH_LONG).show()
        return file
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(context, "Error saving PDF", Toast.LENGTH_SHORT).show()
    } finally {
        pdfDocument.close()
    }
    return null
}

private fun openPdf(context: Context, uri: Uri?) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
    context.startActivity(intent)
}
