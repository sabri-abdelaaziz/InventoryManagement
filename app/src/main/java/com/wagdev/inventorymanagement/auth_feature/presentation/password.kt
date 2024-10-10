package com.wagdev.inventorymanagement.auth_feature.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.wagdev.inventorymanagement.R
import com.wagdev.inventorymanagement.core.util.Routes


@Composable
fun Password(modifier: Modifier = Modifier, navController: NavController,loginViewModel: LoginViewModel= hiltViewModel()) {
    val username = remember { mutableStateOf("user") }
    val pass = remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val login=loginViewModel.loginState.collectAsState(initial = false)
    val isError=loginViewModel.isError
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(stringResource(id=R.string.authentication),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                textAlign = TextAlign.Start,
                textDecoration = TextDecoration.Underline,
                style = TextStyle(

                    color = Color.Black,
                    textAlign=TextAlign.Start,
                    fontStyle = FontStyle.Normal,
                    fontSize = 28.sp,
                    shadow = Shadow(color = Color.Gray, offset = Offset(10.0F,10.0F), blurRadius = 2.0F)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username.value,
                onValueChange = { username.value = it },
                label = { Text(stringResource(id=R.string.username)) },
                placeholder = { Text(stringResource(id=R.string.enterUsername)) },
                singleLine = true,
               keyboardOptions = KeyboardOptions.Default.copy(autoCorrect = false),
                trailingIcon = {
                        Icons.Filled.Person
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = pass.value,
                onValueChange = { pass.value = it },
                label = { Text(stringResource(id = R.string.password)) },
                placeholder = { Text(stringResource(id = R.string.enterPassword)) },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(autoCorrect = false),
                trailingIcon = {
                    val image = if (passwordVisible){
                        Icons.Filled.Visibility
                    } else{
                        Icons.Filled.VisibilityOff
                    }

                    IconButton(onClick = {
                        passwordVisible = !passwordVisible
                    }) {
                        Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
           if (isError){
               Text(stringResource(id = R.string.passwordnotcorrect) , style = TextStyle(color = Color.Red))
           }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {

                loginViewModel.onEvent(LoginEvent.Login(username.value, pass.value)) { isLoggedIn ->
                    if (!isLoggedIn) {
                        navController.navigate(Routes.Password.route)
                    } else {
                        navController.navigate(Routes.Home.route + "/0")
                    }
                }
            }) {
                Icon(Icons.Rounded.Verified, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.verify))
            }
        }
    }
}
@Preview
@Composable
fun Test(modifier: Modifier = Modifier) {
    Password(navController = rememberNavController())
}
