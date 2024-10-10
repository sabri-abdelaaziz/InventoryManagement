package com.wagdev.inventorymanagement.clients_feature.data.repository

import com.wagdev.inventorymanagement.clients_feature.data.ClientDao
import com.wagdev.inventorymanagement.clients_feature.domain.model.Client
import com.wagdev.inventorymanagement.clients_feature.domain.repository.ClientRepository
import kotlinx.coroutines.flow.Flow

class ClientRepositoryImpl(
    private val dao: ClientDao
) : ClientRepository {

    override fun getAllClients(): Flow<List<Client>> {
        return dao.getClients()
    }
    override suspend fun getClientById(id: Long): Client? {
        return dao.getClientById(id)
    }
    override suspend fun addEditClient(client: Client) {
        dao.insertClient(client)
    }
    override suspend fun deleteClient(client: Client) {
        dao.deleteClient(client)
    }

    override fun getTotalClients(): Flow<Int> {
       return dao.getTotalClients()
    }

    override fun getTotalClientInLastMonth(): Flow<Int> {
        return dao.getTotalClientsInLastMonth()

    }

    override fun getTopClientName(): Flow<String> {
        return dao.getTopClientName()
    }
}