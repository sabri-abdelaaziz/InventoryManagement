package com.wagdev.inventorymanagement.core.presentation

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ProductionQuantityLimits
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AddBusiness
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Money
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.wagdev.inventorymanagement.R
import com.wagdev.inventorymanagement.clients_feature.presentation.ClientsScreen
import com.wagdev.inventorymanagement.core.presentation.components.DrawerContent
import com.wagdev.inventorymanagement.core.presentation.components.HomeScreen
import com.wagdev.inventorymanagement.core.presentation.components.TopAppBarSection
import com.wagdev.inventorymanagement.core.util.BottomItem
import com.wagdev.inventorymanagement.orders_feature.presentation.OrdersScreen
import com.wagdev.inventorymanagement.products_feature.presentation.ProductsScreen
import com.wagdev.inventorymanagement.store_feature.presentation.StockScreen

import kotlinx.coroutines.launch
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Home(navController: NavController, initialSelectedItem: Int) {
    var selectedItem by remember { mutableStateOf(initialSelectedItem) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }

    val itemButton = listOf(
        BottomItem(
            title = stringResource(R.string.home),
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            badgeNum = 0,
            hasBadge = false
        ),
        BottomItem(
            title = stringResource(R.string.stock),
            selectedIcon = Icons.Filled.AddBusiness,
            unselectedIcon = Icons.Outlined.AddBusiness,
            badgeNum = 0,
            hasBadge = false
        ),
        BottomItem(
            title = stringResource(R.string.products),
            selectedIcon = Icons.Filled.BusinessCenter,
            unselectedIcon = Icons.Outlined.BusinessCenter,
            badgeNum = 0,
            hasBadge = false
        ),
        BottomItem(
            title = stringResource(R.string.clients),
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            badgeNum = 0,
            hasBadge = false
        ),
        BottomItem(
            title = stringResource(R.string.orders),
            selectedIcon = Icons.Filled.ShoppingCart,
            unselectedIcon = Icons.Outlined.ShoppingCart,
            badgeNum = 2,
            hasBadge = true
        )
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(navController = navController)
        },
        content = {
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        itemButton.forEachIndexed { i, e ->
                            NavigationBarItem(
                                selected = i == selectedItem,
                                onClick = { selectedItem = i },
                                icon = {
                                    BadgedBox(
                                        badge = {
                                            if (e.badgeNum > 0) {
                                                Badge { Text(e.badgeNum.toString()) }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (i == selectedItem) e.selectedIcon else e.unselectedIcon,
                                            contentDescription = null
                                        )
                                    }
                                },
                                label = {
                                    if (i == selectedItem) {
                                        Text(e.title)
                                    }
                                }
                            )
                        }
                    }
                },
                topBar = {
                  TopAppBarSection(navController=navController, drawerState = drawerState, scope = scope)
                },
                floatingActionButton = {
                                       FloatingActionButton(onClick = {
                                           showDialog = true
                                       }) {
                                         Icon(Icons.Default.Add, contentDescription = null)
                                       }
                },
                content = { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        when (selectedItem) {
                            0 -> HomeScreen(navController = navController,modifier=Modifier)
                            1 -> StockScreen(navController=navController)
                            2 -> ProductsScreen(navController = navController)
                            3 -> ClientsScreen(navController=navController)
                            4 -> OrdersScreen(navController=navController)
                            else -> Text("Invalid selection")
                        }
                    }
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            text = {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .size(50.dp)
                                                .padding(vertical = 4.dp)
                                                .background(MaterialTheme.colorScheme.tertiary)
                                                .clickable {
                                                    showDialog = false
                                                    navController.navigate("addeditorder")
                                                },
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            Icon(Icons.Default.AddCard, contentDescription = "Add Order Icon", tint = Color.White)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(stringResource(R.string.add_order), color = Color.White)
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .size(50.dp)
                                                .padding(vertical = 4.dp)
                                                .background(MaterialTheme.colorScheme.primary)
                                                .clickable {
                                                    showDialog = false
                                                    navController.navigate("addeditproduct")
                                                },
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            Icon(Icons.Default.AddBox, contentDescription = "Add Product Icon", tint = Color.White)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(stringResource(R.string.add_product), color = Color.White)
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .size(50.dp)
                                                .padding(vertical = 4.dp)
                                                .background(MaterialTheme.colorScheme.secondary)
                                                .clickable {
                                                    showDialog = false
                                                    navController.navigate("addeditclient")
                                                },
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            Icon(Icons.Default.Person, contentDescription = "Add Client Icon", tint = Color.White)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(stringResource(R.string.add_client), color = Color.White)
                                        }
                                    }

                                }
                            },



                            confirmButton = {

                            },
                            dismissButton = {

                            }
                        )}
                }
            )
        }
    )
}



