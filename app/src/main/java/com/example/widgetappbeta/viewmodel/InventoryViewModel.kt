package com.example.widgetappbeta.viewmodel

import androidx.lifecycle.*
import com.example.widgetappbeta.model.InventoryF
import com.example.widgetappbeta.repository.InventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val repository: InventoryRepository
) : ViewModel() {

    // ============================================
    // 📦 LISTA DE INVENTARIO
    // ============================================
    private val _listInventory = MutableLiveData<MutableList<InventoryF>>()
    val listInventory: LiveData<MutableList<InventoryF>> get() = _listInventory

    private val _progressState = MutableLiveData(false)
    val progressState: LiveData<Boolean> get() = _progressState


    // ============================================
    // ✏️ CAMPOS DEL FORMULARIO (Para Add/Edit)
    // ============================================
    val codigo = MutableLiveData<String>()
    val nombre = MutableLiveData<String>()
    val precio = MutableLiveData<String>()
    val cantidad = MutableLiveData<String>()

    // Validación en tiempo real
    val isFormValid: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        val validator = Observer<Any> {
            value = !codigo.value.isNullOrBlank() &&
                    !nombre.value.isNullOrBlank() &&
                    !precio.value.isNullOrBlank() &&
                    !cantidad.value.isNullOrBlank()
        }
        addSource(codigo, validator)
        addSource(nombre, validator)
        addSource(precio, validator)
        addSource(cantidad, validator)
    }


    // ============================================
    // 🔹 GUARDAR PRODUCTO
    // ============================================
    fun saveInventory(
        inventory: InventoryF,
        updateList: Boolean = false,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _progressState.value = true
                repository.saveInventory(inventory)

                // Solo actualizar lista si se solicita explícitamente
                if (updateList) {
                    getListInventory()
                }

                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.message)
            } finally {
                _progressState.value = false
            }
        }
    }

    // Sobrecarga: Guardar desde campos del formulario
    fun saveInventoryFromForm(onResult: (Boolean, String?) -> Unit) {
        val cod = codigo.value?.toIntOrNull()
        val nom = nombre.value?.trim()
        val pre = precio.value?.toDoubleOrNull()
        val cant = cantidad.value?.toIntOrNull()

        if (cod == null || nom.isNullOrBlank() || pre == null || cant == null) {
            onResult(false, "Datos inválidos")
            return
        }

        val nuevoProducto = InventoryF(
            id = cod,
            name = nom,
            price = pre,
            quantity = cant
        )

        // No actualizar lista - el ListFragment lo hará en onResume
        saveInventory(nuevoProducto, updateList = false, onResult)
    }


    // ============================================
    // 🗑️ ELIMINAR PRODUCTO
    // ============================================
    fun deleteInventory(
        inventory: InventoryF,
        updateList: Boolean = false,
        onResult: ((Boolean, String?) -> Unit)? = null
    ) {
        viewModelScope.launch {
            try {
                _progressState.value = true
                repository.deleteInventory(inventory)

                // Solo actualizar lista si se solicita explícitamente
                if (updateList) {
                    getListInventory()
                }

                onResult?.invoke(true, null)
            } catch (e: Exception) {
                onResult?.invoke(false, e.message)
            } finally {
                _progressState.value = false
            }
        }
    }


    // ============================================
    // ✏️ ACTUALIZAR PRODUCTO
    // ============================================
    fun updateInventory(
        inventory: InventoryF,
        updateList: Boolean = false,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _progressState.value = true
                repository.updateInventory(inventory)

                // Solo actualizar lista si se solicita explícitamente
                if (updateList) {
                    getListInventory()
                }

                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.message)
            } finally {
                _progressState.value = false
            }
        }
    }

    // Sobrecarga: Actualizar desde campos del formulario
    fun updateInventoryFromForm(onResult: (Boolean, String?) -> Unit) {
        val cod = codigo.value?.toIntOrNull()
        val nom = nombre.value?.trim()
        val pre = precio.value?.toDoubleOrNull()
        val cant = cantidad.value?.toIntOrNull()

        if (cod == null || nom.isNullOrBlank() || pre == null || cant == null) {
            onResult(false, "Datos inválidos")
            return
        }

        val productoActualizado = InventoryF(
            id = cod,
            name = nom,
            price = pre,
            quantity = cant
        )

        // No actualizar lista - el ListFragment lo hará en onResume
        updateInventory(productoActualizado, updateList = false, onResult)
    }


    // ============================================
    // 📋 OBTENER LISTA DE INVENTARIO
    // ============================================
    fun getListInventory() {
        viewModelScope.launch {
            try {
                _progressState.value = true
                _listInventory.value = repository.getListInventory()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _progressState.value = false
            }
        }
    }


    // ============================================
    // 🧹 LIMPIAR FORMULARIO
    // ============================================
    fun clearForm() {
        codigo.value = ""
        nombre.value = ""
        precio.value = ""
        cantidad.value = ""
    }

    // Cargar datos en el formulario (para edición)
    fun loadInventoryToForm(inventory: InventoryF) {
        codigo.value = inventory.id.toString()
        nombre.value = inventory.name
        precio.value = inventory.price.toString()
        cantidad.value = inventory.quantity.toString()
    }
}