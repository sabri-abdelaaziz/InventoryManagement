package com.wagdev.inventorymanagement.clients_feature.domain.usecases

data class ClientUseCases(
    val getAllClientsUseCase: GetAllClientsUseCase,
    val getClientByIdUseCase: GetClientByIdUseCase,
    val addEditClientUseCase: AddEditClientUseCase,
    val deleteClientUseCase: DeleteClientUseCase
)