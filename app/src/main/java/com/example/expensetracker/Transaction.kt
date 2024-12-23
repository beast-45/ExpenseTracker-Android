package com.example.expensetracker

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "transactions")
@kotlinx.serialization.Serializable
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id : Int ,
    val label : String ,
    val amount : Double ,
    val description : String) : Serializable{

}