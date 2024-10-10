package com.wagdev.inventorymanagement.clients_feature.presentation

import com.wagdev.inventorymanagement.clients_feature.domain.model.Client

sealed class ClientEvent {
    object GetClients : ClientEvent()
    data class AddEditClient(val client: Client) : ClientEvent()
    data class DeleteClient(val client: Client) : ClientEvent()
    data class GetClientById(val clientId: Long) : ClientEvent()
}