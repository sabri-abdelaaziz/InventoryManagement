package com.wagdev.inventorymanagement.clients_feature.presentation

import com.wagdev.inventorymanagement.clients_feature.domain.model.Client
import kotlinx.coroutines.flow.Flow

sealed class ClientStatus {
    data object Idle : ClientStatus()
    data object Loading : ClientStatus()
    data class Success(val clients: Flow<List<Client>>) : ClientStatus()
    data class Error(val message: String) : ClientStatus()
}