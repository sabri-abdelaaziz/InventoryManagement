package com.wagdev.inventorymanagement.clients_feature.domain.usecases

import com.wagdev.inventorymanagement.clients_feature.domain.model.Client
import com.wagdev.inventorymanagement.clients_feature.domain.repository.ClientRepository

class GetClientByIdUseCase(
    val clientRepository: ClientRepository
) {
    operator suspend fun invoke(id:Long): Client?{
        return clientRepository.getClientById(id)
    }
}