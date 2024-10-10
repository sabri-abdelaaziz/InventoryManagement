package com.wagdev.inventorymanagement.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GolfCourse
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.rounded.LocalActivity
import androidx.compose.material.icons.rounded.Money
import androidx.compose.material.icons.rounded.MoneyOffCsred
import androidx.compose.material.icons.rounded.Newspaper
import androidx.compose.material.icons.rounded.Note
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shop
import androidx.compose.material.icons.rounded.Shop2
import androidx.compose.material.icons.rounded.Timelapse
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.wagdev.inventorymanagement.R
import com.wagdev.inventorymanagement.core.util.Routes


@Composable
fun DrawerContent(
    navController: NavController
) {

    val listItems= listOf(
        DrawerItem(
            icon = Icons.Default.Home,
            label = stringResource(id = R.string.home),
            root = Routes.Home.route+"/0"
        ),
        DrawerItem(
            icon = Icons.Default.Inventory,
            label = stringResource(id = R.string.stock),
            root = Routes.Home.route+"/1" ,
        ),
        DrawerItem(
            icon = Icons.Rounded.Shop2,
            label = stringResource(id = R.string.products),
            root =Routes.Home.route+"/2" ,
        ),
        DrawerItem(
            icon = Icons.Rounded.Person,
            label = stringResource(id = R.string.customers),
            root =Routes.Home.route+"/3" ,
        ),
        DrawerItem(
            icon = Icons.Rounded.Shop,
            label = stringResource(id = R.string.sales),
            root =Routes.Home.route+"/4" ,
        ),
        DrawerItem(
            icon = Icons.Rounded.Newspaper,
            label = stringResource(id = R.string.features),
            root =Routes.Facture.route ,
        ),
        DrawerItem(
            icon = Icons.Rounded.Settings,
            label = stringResource(id = R.string.settings),
            root =Routes.Settings.route,
        )

    )

    ModalDrawerSheet(modifier = Modifier
        .background(MaterialTheme.colorScheme.onSurface)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)

        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    Modifier.size(33.dp)
                )
                Text(
                    "Sabri Abdelaaziz",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text("example@gmail.com", style = TextStyle(fontSize = 11.sp))
            }

        }

        Spacer(Modifier.height(16.dp))
        listItems.forEach { item ->
            NavigationDrawerItem(
                colors = NavigationDrawerItemDefaults.colors(MaterialTheme.colorScheme.surface),
                icon = {
                    Icon(item.icon, contentDescription = null)
                },
                label = { Text(text = item.label) },
                selected = true,
                onClick = {
                    navController.navigate(item.root) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
    // }
    // ){}
}

data class DrawerItem(
    val icon: ImageVector,
    val label:String,
    val root:String
)


