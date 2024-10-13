package com.wagdev.inventorymanagement.setting_feature.presentation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.wagdev.inventorymanagement.core.util.Routes
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(context: Context,navController: NavController, onThemeChange: (Boolean) -> Unit) {
    val settingsUtils = remember { SettingsUtils(context) }

    var selectedLanguage by remember { mutableStateOf(settingsUtils.language) }
    var isDarkThemeEnabled by remember { mutableStateOf(settingsUtils.isDarkTheme) }

    val languages = listOf("en", "fr", "ar")  // Locale language codes

    Scaffold(
        content = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
                    // Theme Switch
                    Text(text = "Dark Mode")
                    Switch(checked = isDarkThemeEnabled, onCheckedChange = {
                        isDarkThemeEnabled = it
                        onThemeChange(it)
                    })

                    // Language Dropdown
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                        OutlinedTextField(
                            value = selectedLanguage,
                            onValueChange = {},
                            label = { Text("Language") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            languages.forEach { language ->
                                DropdownMenuItem(
                                    text = { Text(Locale(language).displayLanguage) },
                                    onClick = {
                                        selectedLanguage = language
                                        expanded = false
                                        updateLocale(context, language)
                                        settingsUtils.language = language
                                    }
                                )
                            }
                        }
                    }

                    // Save Button
                    Button(onClick = {
                        // Apply settings
                        settingsUtils.isDarkTheme = isDarkThemeEnabled
                        settingsUtils.language = selectedLanguage

                        // Update locale
                        updateLocale(context, selectedLanguage)

                        // Restart activity to apply language change
                        restartActivity(context)
                    }) {
                        Text("Save Settings")
                    }
                }
            }
        }
    )
}

class SettingsUtils(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_settings", Context.MODE_PRIVATE)

    var username: String
        get() = sharedPreferences.getString("username", "") ?: ""
        set(value) = sharedPreferences.edit().putString("username", value).apply()

    var email: String
        get() = sharedPreferences.getString("email", "") ?: ""
        set(value) = sharedPreferences.edit().putString("email", value).apply()

    var notificationsEnabled: Boolean
        get() = sharedPreferences.getBoolean("notifications_enabled", true)
        set(value) = sharedPreferences.edit().putBoolean("notifications_enabled", value).apply()

    // New fields for language and theme
    var language: String
        get() = sharedPreferences.getString("language", "English") ?: "English"
        set(value) = sharedPreferences.edit().putString("language", value).apply()

    var isDarkTheme: Boolean
        get() = sharedPreferences.getBoolean("is_dark_theme", false)
        set(value) = sharedPreferences.edit().putBoolean("is_dark_theme", value).apply()
}

fun updateLocale(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val resources = context.resources
    val config = resources.configuration
    config.setLocale(locale)

    resources.updateConfiguration(config, resources.displayMetrics)
}
fun restartActivity(context: Context) {
    val intent = (context as Activity).intent
    context.finish()
    context.startActivity(intent)
}
