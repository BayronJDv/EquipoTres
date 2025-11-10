package com.example.widgetappbeta.repository

import android.content.Context
import com.example.widgetappbeta.data.InventoryDB
import com.example.widgetappbeta.model.Inventory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InventoryRepository(val context: Context) {

    // DAO para realizar operaciones en la base de datos
    private val inventoryDao = InventoryDB.getDatabase(context).inventoryDao()

    // Guardar producto
    suspend fun saveInventory(inventory: Inventory) {
        withContext(Dispatchers.IO) {
            inventoryDao.saveInventory(inventory)
        }
    }

    // Obtener lista de productos
    suspend fun getListInventory(): MutableList<Inventory> {
        return withContext(Dispatchers.IO) {
            inventoryDao.getListInventory()
        }
    }

    // Eliminar producto
    suspend fun deleteInventory(inventory: Inventory) {
        withContext(Dispatchers.IO) {
            inventoryDao.deleteInventory(inventory)
        }
    }

    // Actualizar producto
    suspend fun updateInventory(inventory: Inventory) {
        withContext(Dispatchers.IO) {
            inventoryDao.updateInventory(inventory)
        }
    }
}
