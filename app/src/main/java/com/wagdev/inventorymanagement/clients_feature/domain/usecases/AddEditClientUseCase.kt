package com.wagdev.inventorymanagement.clients_feature.domain.usecases

import com.wagdev.inventorymanagement.clients_feature.domain.model.Client
import com.wagdev.inventorymanagement.clients_feature.domain.repository.ClientRepository

class AddEditClientUseCase (
    val clientRepository: ClientRepository
){
    operator suspend fun invoke(client: Client){
        clientRepository.addEditClient(client)
    }
}