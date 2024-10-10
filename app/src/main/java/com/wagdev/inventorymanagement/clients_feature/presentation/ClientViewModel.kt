package com.wagdev.inventorymanagement.clients_feature.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wagdev.inventorymanagement.clients_feature.domain.model.Client
import com.wagdev.inventorymanagement.clients_feature.domain.usecases.ClientUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientViewModel @Inject constructor(
    private val clientUseCases: ClientUseCases
) : ViewModel() {

    private val _status = MutableStateFlow<ClientStatus>(ClientStatus.Idle)
    val status: StateFlow<ClientStatus> = _status.asStateFlow()
    private val _client = MutableStateFlow<Client?>(null)
    val client: StateFlow<Client?> = _client.asStateFlow()

    init {
        loadClients()
    }

    fun onEvent(event: ClientEvent){
        when (event) {
            is ClientEvent.GetClients -> {
                loadClients()
            }

            is ClientEvent.AddEditClient -> {
                viewModelScope.launch {
                    _status.value = ClientStatus.Loading
                    try {
                        clientUseCases.addEditClientUseCase(event.client)
                        loadClients()
                        _status.value = ClientStatus.Success(emptyFlow()) // Provide actual list after loading
                    } catch (e: Exception) {
                        _status.value = ClientStatus.Error("Error adding client")
                    }
                }
            }

            is ClientEvent.DeleteClient -> {
                viewModelScope.launch {
                    _status.value = ClientStatus.Loading
                    try {
                        clientUseCases.deleteClientUseCase(event.client)
                        loadClients()
                        _status.value = ClientStatus.Success(emptyFlow()) // Provide actual list after loading
                    } catch (e: Exception) {
                        _status.value = ClientStatus.Error("Error deleting client")
                    }
                }
            }

            is ClientEvent.GetClientById -> {
                viewModelScope.launch {
                    _client.value=clientUseCases.getClientByIdUseCase(event.clientId)
                }
            }
        }
    }

    private fun loadClients() {
        viewModelScope.launch {
            _status.value = ClientStatus.Loading
            try {
                val clients = clientUseCases.getAllClientsUseCase()
                _status.value = ClientStatus.Success(clients)
            } catch (e: Exception) {
                _status.value = ClientStatus.Error("Error loading clients")
            }
        }
    }
}
