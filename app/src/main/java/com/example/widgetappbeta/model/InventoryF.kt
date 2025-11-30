package com.example.widgetappbeta.model

import java.io.Serializable


// data class para guardar en firestore
data class InventoryF(
    val id: Int = 0,
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0
): Serializable {

}
