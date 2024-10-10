package com.wagdev.inventorymanagement.auth_feature.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.wagdev.inventorymanagement.auth_feature.domain.model.Login
import kotlinx.coroutines.flow.Flow

@Dao
interface LoginDao {
    @Upsert
    suspend fun insert(login: Login)

    @Delete
    suspend fun delete(login: Login)

    @Query("SELECT * FROM login")
    fun getAllLogins(): Flow<List<Login>>

    @Query("SELECT * FROM login WHERE username = :username AND password = :password ")
    suspend fun getLogin(username: String, password: String): Login?
    @Query("SELECT COUNT(username) FROM login")
    suspend fun countUsers():Int
}
