package com.wagdev.inventorymanagement.core.presentation.components

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import com.wagdev.inventorymanagement.R
import com.wagdev.inventorymanagement.core.util.Language
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarSection(
    modifier: Modifier = Modifier,
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    val context = LocalContext.current // Get context here
    val languageChangeHelper = LanguageChangeHelper()

    val allLanguages = listOf(
        Language("ar", "Arabic", R.drawable.ar),
        Language("en", "English", R.drawable.en),
        Language("fr", "French", R.drawable.fr),
    )

    val currentLanguageCode: String = languageChangeHelper.getLanguageCode(context)

    var currentLanguage by remember { mutableStateOf(currentLanguageCode) }

    val onCurrentLanguageChange: (String) -> Unit = { newLanguage ->
        currentLanguage = newLanguage
        languageChangeHelper.changeLanguage(context, newLanguage)
    }

    var expanded by remember { mutableStateOf(false) }

    androidx.compose.material3.TopAppBar(
        navigationIcon = {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                modifier = Modifier.clickable {
                    scope.launch {
                        drawerState.open()
                    }
                }
            )
        },
        title = { Text(stringResource(id = R.string.app_name)) },
        actions = {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(Icons.Default.MoreVert, contentDescription = null)
            }
            // Language Dropdown
            LanguagesDropdown(
                languagesList = allLanguages,
                currentLanguage = currentLanguage,
                onCurrentLanguageChange = onCurrentLanguageChange,
                expanded = expanded,
                onDismiss = { expanded = false }
            )
        }
    )
}


@Composable
fun LanguagesDropdown(
    modifier: Modifier = Modifier,
    languagesList: List<Language>,
    currentLanguage: String,
    onCurrentLanguageChange: (String) -> Unit,
    expanded: Boolean,
    onDismiss: () -> Unit,
) {
   // var selectedItem by remember { mutableStateOf(languagesList.first { it.code == currentLanguage }) }
    var selectedItem by remember { mutableStateOf(languagesList.firstOrNull { it.code == currentLanguage } ?: languagesList.first()) }

    Box(
        modifier = modifier
            .padding(end = 16.dp)
            .wrapContentSize(Alignment.TopEnd)
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onDismiss() },
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            repeat(languagesList.size) {
                val item = languagesList[it]
                DropdownMenuItem(text = {
                    LanguageListItem(selectedItem = item)
                }, onClick = {
                    selectedItem = item
                    onCurrentLanguageChange(selectedItem.code)
                    onDismiss() // Dismiss the dropdown after selection
                })
            }
        }
    }
}

@Composable
fun LanguageListItem(selectedItem: Language) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape),
            painter = painterResource(selectedItem.flag),
            contentScale = ContentScale.Crop,
            contentDescription = selectedItem.code
        )

        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = selectedItem.name,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.onBackground,
            )
        )
    }

}

class LanguageChangeHelper {

    fun changeLanguage(context: Context, languageCode: String) {

        //if needed we can set in pref.
        val availableLocales = LocaleList.getDefault()


        //version >= 13
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales =
                LocaleList.forLanguageTags(languageCode)
        } else {
            //version < 13
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
        }
    }

    fun getLanguageCode(context: Context,): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales[0]?.toLanguageTag()?.split("-")?.first() ?: "en"
        } else {
            //version < 13
            AppCompatDelegate.getApplicationLocales()[0]?.toLanguageTag()?.split("-")?.first() ?: "en"
        }
    }
}