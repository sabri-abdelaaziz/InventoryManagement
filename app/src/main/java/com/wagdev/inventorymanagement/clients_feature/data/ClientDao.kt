package com.wagdev.inventorymanagement.clients_feature.data

import androidx.room.*
import com.wagdev.inventorymanagement.clients_feature.domain.model.Client
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Query("SELECT * FROM client")
    fun getClients(): Flow<List<Client>>

    @Query("SELECT * FROM client WHERE id_client = :id")
    suspend fun getClientById(id: Long): Client?

    @Upsert
    suspend fun insertClient(client: Client)

    @Delete
    suspend fun deleteClient(client: Client)

    @Query("SELECT COUNT(id_client) FROM client")
    fun getTotalClients():Flow<Int>
    @Query("SELECT COUNT(id_client) FROM client WHERE date_added >= date('now', '-1 month')")
    fun getTotalClientsInLastMonth():Flow<Int>
    @Query("SELECT c.name FROM client c ,orders o WHERE c.id_client = o.clientId GROUP BY c.name ORDER BY COUNT(c.id_client) DESC LIMIT 1")
    fun getTopClientName():Flow<String>
}
