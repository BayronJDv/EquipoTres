package com.example.widgetappbeta.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.widgetappbeta.model.Inventory
import com.example.widgetappbeta.repository.InventoryRepository

class WidgetViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>()

    private val inventoryRepository = InventoryRepository(context)

    //devuelve el saldo en string
    suspend fun obtenerSaldo(): String = withContext(Dispatchers.IO) {
        val inventario = inventoryRepository.getListInventory()

        val saldoTotal = inventario.sumOf { inventory ->
            inventory.price * inventory.quantity
        }

        String.format("%.2f", saldoTotal)
    }

    suspend fun obtenerSaldoFormateado(): String = withContext(Dispatchers.IO) {

        val partes = obtenerSaldo().split(".")
        val entera = partes[0]
        val decimal = partes[1]

        // Formatear parte entera con separadores
        val enteraReversed = entera.reversed()
        val enteraConPuntos = StringBuilder()
        /*
         Esto trabaja haciendo la cadena de atras hacia adelante, despues cada 3
         lugares pone un punto (cada multiplo de 3), recordando que para este for va de 0 hasta
         la longitud - 1
        */
        for (i in enteraReversed.indices) {
            if (i > 0 && i % 3 == 0) enteraConPuntos.append(".")
            enteraConPuntos.append(enteraReversed[i])
        }

        "$ ${enteraConPuntos.reverse()},$decimal"
    }
}
