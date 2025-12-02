package com.example.widgetappbeta.repository

import android.util.Log
import com.example.widgetappbeta.model.InventoryF
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InventoryRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val TAG = "InventoryRepository"
    private val COLLECTION = "inventario"

    // -----------------------------
    //   🔹 1. GUARDAR (con NO repetir)
    // -----------------------------
    suspend fun saveInventory(inventory: InventoryF) {
        Log.d(TAG, "Guardando producto: $inventory")

        try {
            withContext(Dispatchers.IO) {
                val docRef = firestore.collection(COLLECTION)
                    .document(inventory.id.toString())

                val snapshot = docRef.get().await()

                if (snapshot.exists()) {
                    Log.w(TAG, "ERROR → Ya existe un producto con ID: ${inventory.id}")
                    throw Exception("El producto con ID ${inventory.id} ya existe")
                }

                docRef.set(inventory).await()
                Log.d(TAG, "Producto guardado en Firestore correctamente")
            }
        } catch (e: CancellationException) {
            Log.d(TAG, "Operación de guardado cancelada")
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error al guardar: ${e.message}")
            throw e
        }
    }

    // -----------------------------
    //   🔹 2. OBTENER LISTA
    // -----------------------------
    suspend fun getListInventory(): MutableList<InventoryF> {
        return try {
            withContext(Dispatchers.IO) {
                Log.d(TAG, "Obteniendo lista de inventario...")

                val snapshot = firestore.collection(COLLECTION)
                    .orderBy("id")
                    .get()
                    .await()

                val list = snapshot.toObjects(InventoryF::class.java).toMutableList()
                Log.d(TAG, "Lista obtenida: ${list.size} productos")
                list
            }
        } catch (e: CancellationException) {
            Log.d(TAG, "Obtención de lista cancelada")
            throw e
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
                Log.d(TAG, "Eliminando producto ID: ${inventory.id}")

                firestore.collection(COLLECTION)
                    .document(inventory.id.toString())
                    .delete()
                    .await()

                Log.d(TAG, "Producto eliminado correctamente")
            }
        } catch (e: CancellationException) {
            Log.d(TAG, "Eliminación cancelada (normal si se sale del Fragment)")
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar: ${e.message}", e)
            throw e
        }
    }

    // -----------------------------
    //   🔹 4. ACTUALIZAR
    // -----------------------------
    suspend fun updateInventory(inventory: InventoryF) {
        try {
            withContext(Dispatchers.IO) {
                Log.d(TAG, "Actualizando producto: $inventory")

                firestore.collection(COLLECTION)
                    .document(inventory.id.toString())
                    .set(inventory)
                    .await()

                Log.d(TAG, "Producto actualizado correctamente")
            }
        } catch (e: CancellationException) {
            Log.d(TAG, "Actualización cancelada")
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar: ${e.message}", e)
            throw e
        }
    }
}