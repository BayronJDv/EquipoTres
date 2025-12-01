package com.example.widgetappbeta.repository

import android.util.Log
import com.example.widgetappbeta.model.InventoryF
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InventoryRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val TAG = "InventoryRepository"

    // -----------------------------
    //   🔹 1. GUARDAR (con NO repetir)
    // -----------------------------
    suspend fun saveInventory(inventory: InventoryF) {
        Log.d(TAG, "Guardando producto: $inventory")

        try {
            withContext(Dispatchers.IO) {
                val docRef = firestore.collection("inventario")
                    .document(inventory.id.toString())

                val snapshot = docRef.get().await()

                if (snapshot.exists()) {
                    Log.w(TAG, "ERROR → Ya existe un producto con ID: ${inventory.id}")
                    throw Exception("El producto con ID ${inventory.id} ya existe.")
                }

                docRef.set(inventory).await()
                Log.d(TAG, "Producto guardado en Firestore correctamente")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al guardar: ${e.message}")
            throw e
        }
    }

    // -----------------------------
    //   🔹 2. OBTENER LISTA
    // -----------------------------
    suspend fun getListInventory(): MutableList<InventoryF> =
        withContext(Dispatchers.IO) {
            try {
                val snapshot = firestore.collection("inventario")
                    .orderBy("id")
                    .get()
                    .await()
                snapshot.toObjects(InventoryF::class.java).toMutableList()
            } catch (e: Exception) {
                Log.e(TAG, "Error al obtener lista: ${e.message}")
                mutableListOf()
            }
        }

    // -----------------------------
    //   🔹 3. ELIMINAR
    // -----------------------------
    suspend fun deleteInventory(inventory: InventoryF) {
        try {
            withContext(Dispatchers.IO) {
                firestore.collection("inventario")
                    .document(inventory.id.toString())
                    .delete()
                    .await()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar en Firestore: ${e.message}", e)
            throw e
        }
    }


    // -----------------------------
    //   🔹 4. ACTUALIZAR
    // -----------------------------
    suspend fun updateInventory(inventory: InventoryF) {
        try {
            withContext(Dispatchers.IO) {
                firestore.collection("inventario")
                    .document(inventory.id.toString())
                    .set(inventory)
                    .await()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar en Firestore: ${e.message}", e)
            throw e
        }
    }

}
