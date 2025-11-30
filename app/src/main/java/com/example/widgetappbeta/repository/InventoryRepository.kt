package com.example.widgetappbeta.repository

import android.content.Context
import android.util.Log
import com.example.widgetappbeta.data.InventoryDB
import com.example.widgetappbeta.data.InventoryDao
import com.example.widgetappbeta.model.Inventory
import com.example.widgetappbeta.model.InventoryF
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InventoryRepository  @Inject constructor(
    private val inventoryDao: InventoryDao,
    private val firestore: FirebaseFirestore
){
    private val TAG = "InventoryRepository"

    // Guardar con validación de "No Repetir"
    suspend fun saveInventory(inventory: InventoryF) {
        // Log para saber que la función se ha iniciado y con qué datos
        Log.d(TAG, "Iniciando saveInventory para el producto: $inventory")

        try {
            withContext(Dispatchers.IO) {
                val docRef = firestore.collection("inventario").document(inventory.id.toString())
                Log.d(TAG, "Referencia al documento creada: ${docRef.path}")

                // Leemos el documento antes de intentar escribir
                Log.d(TAG, "Intentando leer el documento para verificar si existe...")
                val snapshot = docRef.get().await()

                if (snapshot.exists()) {
                    // Si el documento ya existe, lo registramos y lanzamos la excepción
                    val existingData = snapshot.data
                    Log.w(TAG, "El producto con ID ${inventory.id} ya existe en Firestore. Datos existentes: $existingData")
                    throw Exception("El producto con ID ${inventory.id} ya existe.")
                } else {
                    // Si el documento NO existe, procedemos a guardar
                    Log.d(TAG, "El producto con ID ${inventory.id} no existe. Procediendo a guardarlo...")
                    docRef.set(inventory).await()
                    // ¡Log de éxito!
                    Log.d(TAG, "¡ÉXITO! Producto guardado correctamente en Firestore.")
                }
            }
        } catch (e: Exception) {
            // Capturamos CUALQUIER excepción que ocurra en el proceso y la registramos
            Log.e(TAG, "Error al guardar en Firestore: ${e.message}", e)
            // Relanzamos la excepción para que el ViewModel/UI pueda reaccionar al error
            throw e
        }
    }

    // Obtener lista ordenada
    suspend fun getListInventory(): MutableList<InventoryF> {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = firestore.collection("inventario")
                    .orderBy("id") // Ordenar numéricamente
                    .get()
                    .await()
                snapshot.toObjects(InventoryF::class.java).toMutableList()
            } catch (e: Exception) {
                mutableListOf()
            }
        }
    }

    // Funciones que faltan actualizar a firebase
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
