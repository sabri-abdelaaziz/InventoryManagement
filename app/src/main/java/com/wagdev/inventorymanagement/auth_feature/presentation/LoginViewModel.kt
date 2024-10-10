package com.wagdev.inventorymanagement.auth_feature.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wagdev.inventorymanagement.auth_feature.domain.model.Login
import com.wagdev.inventorymanagement.auth_feature.domain.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<Boolean?>(null)
    val loginState: StateFlow<Boolean?> = _loginState
    var isError by mutableStateOf(
        false
    )
    init {
        viewModelScope.launch {
            // Insert default user if no users exist in the database
            val userCount = loginRepository.countUsers()
            println("UCL"+userCount)
            if (userCount < 1) {
                loginRepository.insert(Login(1, Login.USER1, Login.PASS1))
            }
        }
    }

    fun onEvent(event: LoginEvent, onLoginResult: (Boolean) -> Unit) {
        when (event) {
            is LoginEvent.Login -> {
                viewModelScope.launch {
                    val isAuthenticated = loginRepository.login(event.username, event.password)
                    println("UCL"+isAuthenticated)
                    if(isAuthenticated!=null){
                        if(isAuthenticated.username!=Login.USER1 || isAuthenticated.password!=Login.PASS1){
                            _loginState.value = false
                            isError=true
                            onLoginResult(false)
                        }else{
                            _loginState.value = true
                            isError=false
                            onLoginResult(true)
                        }
                    }else{
                        _loginState.value=false
                        isError=true
                        onLoginResult(false)
                    }


                }
            }
        }
    }
}