package com.wagdev.inventorymanagement.auth_feature.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Login(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    val username:String,
    val password:String
){
    companion object{
        const val USER1="user"
        const val PASS1="pass"
    }
}