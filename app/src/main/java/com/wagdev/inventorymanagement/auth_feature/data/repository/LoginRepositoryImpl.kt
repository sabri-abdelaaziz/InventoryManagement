package com.wagdev.inventorymanagement.auth_feature.data.repository

import com.wagdev.inventorymanagement.auth_feature.data.LoginDao
import com.wagdev.inventorymanagement.auth_feature.domain.model.Login
import com.wagdev.inventorymanagement.auth_feature.domain.repository.LoginRepository
import kotlinx.coroutines.flow.Flow


class LoginRepositoryImpl (
    val loginDao: LoginDao
):LoginRepository{
    override fun getAllLogins(): Flow<List<Login>> {
        return loginDao.getAllLogins()
    }

    override suspend fun login(username: String, password: String): Login? {
        return loginDao.getLogin(username,password)
    }


    override suspend fun insert(login: Login) {
        loginDao.insert(login)
    }

    override suspend fun delete(login: Login) {
        loginDao.delete(login)
    }

    override suspend fun countUsers(): Int {
        return loginDao.countUsers()
    }
}