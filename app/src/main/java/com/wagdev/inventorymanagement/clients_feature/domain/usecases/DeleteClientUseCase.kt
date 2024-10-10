package com.wagdev.inventorymanagement.clients_feature.domain.usecases

import com.wagdev.inventorymanagement.clients_feature.domain.model.Client
import com.wagdev.inventorymanagement.clients_feature.domain.repository.ClientRepository

class DeleteClientUseCase(
    val clientRepository: ClientRepository
) {
    operator suspend fun invoke(client: Client) {
        clientRepository.deleteClient(client)
    }
}