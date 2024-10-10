package com.wagdev.inventorymanagement.auth_feature.presentation

sealed class LoginEvent {
    data class Login(val username:String, val password:String):LoginEvent()
}