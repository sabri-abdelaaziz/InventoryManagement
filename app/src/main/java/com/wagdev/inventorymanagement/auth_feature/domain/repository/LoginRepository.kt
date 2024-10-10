package com.wagdev.inventorymanagement.auth_feature.domain.repository

import com.wagdev.inventorymanagement.auth_feature.domain.model.Login
import kotlinx.coroutines.flow.Flow
interface LoginRepository {
    fun getAllLogins(): Flow<List<Login>>
    suspend fun login(username: String, password: String): Login?
    suspend fun insert(login: Login)
    suspend fun delete(login: Login)
    suspend fun countUsers():Int
}