package com.example.widgetappbeta.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Inventory(
    @PrimaryKey
    val id: Int,
    val name: String,
    val price: Double,
    val quantity: Int) : Serializable
