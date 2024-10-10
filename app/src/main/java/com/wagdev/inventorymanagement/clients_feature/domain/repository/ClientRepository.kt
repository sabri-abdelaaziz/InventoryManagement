package com.wagdev.inventorymanagement.clients_feature.domain.repository

import com.wagdev.inventorymanagement.clients_feature.domain.model.Client
import kotlinx.coroutines.flow.Flow

interface ClientRepository{
    fun getAllClients(): Flow<List<Client>>
    suspend fun getClientById(id:Long):Client?
    suspend fun addEditClient(client: Client)
    suspend fun deleteClient(client: Client)
    fun getTotalClients(): Flow<Int>
    fun getTotalClientInLastMonth() :Flow<Int>
    fun getTopClientName():Flow<String>
}