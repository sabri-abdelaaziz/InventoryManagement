package com.wagdev.inventorymanagement.store_feature.presentation

import android.graphics.PointF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DensitySmall
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.RecentActors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.wagdev.inventorymanagement.R
import com.wagdev.inventorymanagement.clients_feature.presentation.ClientViewModel
import com.wagdev.inventorymanagement.order_feature.presentation.OrderViewModel
import com.wagdev.inventorymanagement.products_feature.presentation.ProductViewModel
import com.wagdev.inventorymanagement.stock_feature.presentation.StockViewModel
import kotlin.io.path.Path
import kotlin.io.path.moveTo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockScreen(modifier: Modifier = Modifier, navController: NavController,stockViewModel: StockViewModel = hiltViewModel()) {
    // Collect the productsTotal StateFlow

    // Fetch the store statistics when the screen is first composed



    Scaffold(

       // floatingActionButton = {
            //FloatingActionButton(
                //onClick = {
                  //  navController.navigate("addeditproduct")
                //},
                //containerColor = Color(0xFF03DAC5),
              //  contentColor = Color.White
          //  ) {
            //    Icon(Icons.Default.Add, contentDescription = null)
            //}
        //}
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(stringResource(id = R.string.storeOverview),fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(color = MaterialTheme.colorScheme.onSurface))
                    IconButton(onClick = { /* Handle settings or other actions */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }

            }
            item {
                StoreStatsOverview()
            }
            item{
                Text(
                    text = stringResource(id = R.string.salesByMonth),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6200EE),
                )
            }
            item {
                SampleChartScreen()
            }

        }
    }
}

@Composable
fun StoreStatsOverview() {
    //viewmodels
    val stockViewModel:StockViewModel= hiltViewModel()
    //variables
    val totalProducts by stockViewModel.getTotalProducts().collectAsState(initial = 0)
    val totalOrders by stockViewModel.getTotalOrders().collectAsState(initial = 0)
    val totalClients by stockViewModel.getTotalClients().collectAsState(initial = 0)
    //products variables
    //orders variables
    val totalOrdersInLastMonth by stockViewModel.getTotalOrdersInLastMonth().collectAsState(initial = 0)
    val totalOrdersInThisMonth by stockViewModel.getTotalOrdersInThisMonth().collectAsState(initial = 0)
    val totalRevenues by stockViewModel.getTotalRevenuesFormatted().collectAsState(initial = "0.00")
    //clients variables
    val totalClientsInLastMonth by stockViewModel.getTotalClientInLastMonth().collectAsState(initial=0)
    val topClientName by stockViewModel.getTopClientName().collectAsState(initial = stringResource(
        id = R.string.nothingYet
    ))
    //products variables
    val topProductName by stockViewModel.getTopProductName().collectAsState(initial = stringResource(id=R.string.nothingYet))
    val lowStockProduct by stockViewModel.getLowStockProductName().collectAsState(initial = stringResource(id=R.string.nothingYet))
    val outOfStockProduct by stockViewModel.getOutOfStockProductName().collectAsState(initial = stringResource(id=R.string.nothingYet))
 val recentProductAdd by stockViewModel.getRecentProductAdd().collectAsState(initial = stringResource(id=R.string.nothingYet))
    val totalValues by stockViewModel.getTotalValue().collectAsState(initial = 0.0)
    val valueIn by stockViewModel.getValueIn().collectAsState(initial = 0.0)
    val valueOut by stockViewModel.getValueOut().collectAsState(initial = 0.0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Text(
                stringResource(id = R.string.ordersInfos),fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(color = MaterialTheme.colorScheme.onSurface))
            Icon(Icons.Default.KeyboardDoubleArrowRight, contentDescription = null)
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow {
            item {
                StatCard(title = stringResource(id = R.string.totalOrders), value = "$totalOrders"?: "N/A", icon = Icons.Default.DensitySmall)
                Spacer(modifier = Modifier.width(20.dp))
            }
            item {
                StatCard(title = stringResource(id=R.string.bytime), value = stringResource(id =R.string.lastMonth )+ "$totalOrdersInLastMonth\n"+ stringResource(
                    id = R.string.thismonth
                )+" $totalOrdersInThisMonth"?: "N/A", icon = Icons.Default.AccessTimeFilled)
                Spacer(modifier = Modifier.width(20.dp))
            }
            item {
                StatCard(title = stringResource(id = R.string.totleRevenues ), value = "$totalRevenues DH"?: "N/A", icon = Icons.Filled.MonetizationOn)
                Spacer(modifier = Modifier.width(20.dp))
            }


        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Text(
                stringResource(id = R.string.clientInfos),fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(color = MaterialTheme.colorScheme.onSurface))
            Icon(Icons.Default.KeyboardDoubleArrowRight, contentDescription = null)
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow {
            item {
                StatCard(title = stringResource(id = R.string.totalClients), value = "$totalClients "?: "N/A",icon = Icons.Filled.SupervisedUserCircle )
                Spacer(modifier = Modifier.width(20.dp))
            }
            item {
                StatCard(title = stringResource(id = R.string.clientsInlastMonth), value = "$totalClientsInLastMonth"?: "N/A", icon = Icons.Default.Inventory)
                Spacer(modifier = Modifier.width(20.dp)) }
            item {
                StatCard(title = stringResource(id = R.string.topClient), value = topClientName, icon = Icons.Default.Inventory)
                Spacer(modifier = Modifier.width(20.dp))}


        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Text(stringResource(id=R.string.productInfos),fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(color = MaterialTheme.colorScheme.onSurface))
            Icon(Icons.Default.KeyboardDoubleArrowRight, contentDescription = null)
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow {
            item {
                StatCard(title = stringResource(id=R.string.totalProducts), value = "$totalProducts", icon = Icons.Filled.ShoppingBasket)
                Spacer(modifier = Modifier.width(20.dp))
            }
            item {
                StatCard(title = stringResource(id = R.string.topProduct), value = topProductName?: "N/A", icon = Icons.Default.Inventory)
                Spacer(modifier = Modifier.width(20.dp))}
            item {
                StatCard(title = stringResource(id = R.string.lowStockProduct), value = lowStockProduct?: "N/A", icon = Icons.Default.Inventory)
                Spacer(modifier = Modifier.width(20.dp)) }
            item {
                StatCard(title = stringResource(id = R.string.outOfStoreProduct), value =  outOfStockProduct?: "N/A", icon = Icons.Default.Inventory)
                Spacer(modifier = Modifier.width(20.dp))}
            item {
                StatCard(title = stringResource(id = R.string.recentProductAdd), value = recentProductAdd?: "N/A", icon = Icons.Default.AttachMoney)
                Spacer(modifier = Modifier.width(20.dp))}



        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Text(stringResource(id = R.string.otherInformation),fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(color = MaterialTheme.colorScheme.onSurface))
            Icon(Icons.Default.KeyboardDoubleArrowRight, contentDescription = null)
        }
        Spacer(modifier = Modifier.height(16.dp))

            LazyRow {
                item {
                    StatCard(title = stringResource(id = R.string.totalValues), value = "$${totalValues ?: 0.00} MAD", icon = Icons.Filled.DoneAll)
                    Spacer(modifier = Modifier.width(20.dp))
                }
                item {
                    StatCard(title = stringResource(id = R.string.valueIn), value = "$${valueIn ?: 0.00} MAD", icon = Icons.Default.Inventory)
                    Spacer(modifier = Modifier.width(20.dp))
                }
                item {
                    StatCard(title = stringResource(id = R.string.valueOut), value = "$${valueOut ?: 0.00} MAD", icon = Icons.Default.Inventory)
                    Spacer(modifier = Modifier.width(20.dp))
                }


        }

    }
}

@Composable
fun StatCard(title: String, value: String?, icon: ImageVector) {
    Card(
        modifier = Modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = title, tint = Color(0xFF6200EE), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value ?: "N/A", style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, color = Color.Gray))
        }
    }
}

@Composable
fun SampleChartScreen( ) {
    val stockViewModel:StockViewModel= hiltViewModel()
    val salesData by stockViewModel.monthlySalesData.collectAsState()

    SalesByMonthsLineChart(salesData = salesData)
}
@Composable
fun SalesByMonthsLineChart(
    modifier: Modifier = Modifier,
    salesData: List<Float>
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF1F1F1)),
        contentAlignment = Alignment.Center
    ) {
        val maxYValue = (salesData.maxOrNull() ?: 0f).takeIf { it > 0 } ?: 1f

        Canvas(modifier = Modifier.fillMaxSize()) {
            val spacing = 16.dp.toPx()  // Moved inside Canvas
            val width = size.width
            val height = size.height
            val stepX = (width - spacing) / (salesData.size - 1)
            val stepY = (height - spacing) / maxYValue

            val maxYValue = salesData.maxOrNull()?.toFloat() ?: 0f // Add a check for maxYValue

            if (salesData.isNotEmpty()) {


                val points = salesData.mapIndexed { index, value ->
                    val x = index * stepX + spacing
                    val y = height - (value * stepY) - spacing
                    Offset(x, y)
                }

                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(points.first().x, points.first().y)
                    points.forEach { point ->
                        lineTo(point.x, point.y)
                    }
                }

                drawPath(
                    path = path,
                    color = Color(0xFF6200EE),
                    style = Stroke(
                        width = 4.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // Draw the points
                points.forEach { point ->
                    drawCircle(
                        color = Color(0xFF6200EE),
                        radius = 6.dp.toPx(),
                        center = point
                    )
                }
            }

            // Draw X-axis labels (assuming you want to draw labels even with empty data)
            val monthLabels = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct")
            monthLabels.forEachIndexed { index, label ->
                val x = index * stepX + spacing
                drawContext.canvas.nativeCanvas.drawText(
                    label,
                    x,
                    height,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textAlign = android.graphics.Paint.Align.CENTER
                        textSize = 12.sp.toPx()
                    }
                )
            }

            // Draw Y-axis labels
            val yStep = maxYValue / 4
            (0..4).forEach { i ->
                val yValue = (i * yStep).toInt()
                val y = height - (i * yStep * stepY) - spacing
                drawContext.canvas.nativeCanvas.drawText(
                    yValue.toString(),
                    spacing / 2,
                    y,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textAlign = android.graphics.Paint.Align.LEFT
                        textSize = 12.sp.toPx()
                    }
                )
            }
        }


    }
}




@Composable
fun RecentActivityItem(activity: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

            Text(text = activity, style = TextStyle(fontSize = 24.sp, color = Color.Gray))

    }
}

