package com.wagdev.inventorymanagement.setting_feature.presentation

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(context: Context) {
    val settingsUtils = remember { SettingsUtils(context) }
    var username by remember { mutableStateOf(settingsUtils.username) }
    var email by remember { mutableStateOf(settingsUtils.email) }
    var notificationsEnabled by remember { mutableStateOf(settingsUtils.notificationsEnabled) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Account Settings", fontSize = 20.sp, modifier = Modifier.padding(bottom = 16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Notification Settings", fontSize = 20.sp, modifier = Modifier.padding(vertical = 16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Enable Notifications", fontSize = 16.sp, modifier = Modifier.weight(1f))
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Save settings
                settingsUtils.username = username
                settingsUtils.email = email
                settingsUtils.notificationsEnabled = notificationsEnabled
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Settings")
        }
    }
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
}
