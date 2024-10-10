package com.wagdev.inventorymanagement.clients_feature.domain.usecases

import com.wagdev.inventorymanagement.clients_feature.domain.model.Client
import com.wagdev.inventorymanagement.clients_feature.domain.repository.ClientRepository
import kotlinx.coroutines.flow.Flow

class GetAllClientsUseCase(
    val clientRepository: ClientRepository
) {
    operator fun invoke(): Flow<List<Client>> {
     return clientRepository.getAllClients()
    }
}