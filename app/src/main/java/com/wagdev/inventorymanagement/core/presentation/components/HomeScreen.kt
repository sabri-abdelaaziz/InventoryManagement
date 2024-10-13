package com.wagdev.inventorymanagement.core.presentation.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.wagdev.inventorymanagement.R
import com.wagdev.inventorymanagement.order_feature.presentation.OrderViewModel
import com.wagdev.inventorymanagement.products_feature.presentation.ProductImage
import com.wagdev.inventorymanagement.stock_feature.presentation.StockViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier
) {
    val stockViewModel: StockViewModel = hiltViewModel()
   val Products_list by stockViewModel.getFeaturedProducts().collectAsState(initial = emptyList())
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Section with Image and Brand Name
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp) // Set a fixed height for the Box
        ) {
            Image(
                painter = painterResource(id = R.drawable.elec),
                contentDescription = stringResource(id = R.string.app_name),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp) // Adjust height as needed
                    .clip(
                        GenericShape { size, _ ->
                            val path = Path().apply {
                                // Create a semicircle path at the bottom
                                moveTo(0f, 0f)
                                lineTo(0f, size.height / 2)
                                arcTo(
                                    rect = Rect(0f, 0f, size.width, size.height),
                                    startAngleDegrees = 180f,
                                    sweepAngleDegrees = -180f,
                                    forceMoveTo = false
                                )
                                lineTo(size.width, 0f)
                                close()
                            }
                            addPath(path)
                        }
                    ),
                contentScale = ContentScale.Crop
            )
            Text(
                text = stringResource(id = R.string.specialite),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            )
        }

        // Middle Section with Buttons
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(6) { index -> // Use the number of items you have
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp), // Adjust padding if necessary
                    contentAlignment = Alignment.Center // Center the content within the Box
                ) {
                    when (index) {
                        0 -> IconWithText(iconRes = R.drawable.store, label = stringResource(id = R.string.stock), des = "home/1", navController = navController)
                        1 -> IconWithText(iconRes = R.drawable.products, label = stringResource(id = R.string.products), des = "home/2", navController = navController)
                        2 -> IconWithText(iconRes = R.drawable.clients, label = stringResource(id = R.string.clients), des = "home/3", navController = navController)
                        3 -> IconWithText(iconRes = R.drawable.factors, label = stringResource(id = R.string.orders), des = "home/4", navController = navController)
                        4 -> IconWithText(iconRes = R.drawable.files, label = stringResource(id = R.string.invoices), des = "downloads", navController = navController)
                        5 -> IconWithText(iconRes = R.drawable.baseline_settings_24, label = stringResource(id = R.string.Settings), des = "settings", navController = navController)
                    }
                }
            }
        }

        // Bottom Section with Image and Buttons
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = stringResource(id = R.string.top_s_products), style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp), // Add spacing between columns
            horizontalArrangement = Arrangement.SpaceEvenly // Distribute columns evenly across the row
        ) {
            Products_list.forEach {
                Column(
                    modifier = Modifier
                        .weight(1f) // Each column takes equal space in the row
                        .padding(horizontal = 4.dp), // Add spacing between columns
                    verticalArrangement = Arrangement.SpaceEvenly, // Space content evenly in the column
                    horizontalAlignment = Alignment.CenterHorizontally // Center content horizontally in the column
                ) {
                    it.image?.let { it1 ->
                        ProductImage(
                            it1,
                            modifier = Modifier
                                .fillMaxSize() // Makes the image take all available space in the parent
                                .clip(RoundedCornerShape(10.dp))
                                .align(Alignment.CenterHorizontally)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp)) // Add space between the image and text
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth() // Make sure the row takes the full width of the column
                    ) {
                        Text(text = it.nbrOrders.toString())
                        Spacer(modifier = Modifier.width(4.dp)) // Add space between text and icon
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
            }
        }
    }

}

@Composable
fun IconWithText(iconRes: Int, label: String, size: Dp = 90.dp,des:String,navController: NavController) {
    Box(
        modifier = Modifier
            .size(size)
            .clickable { navController.navigate(des) },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier.size(73.dp) // Adjust this size as needed
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, style = TextStyle(fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold))
        }
    }
}




