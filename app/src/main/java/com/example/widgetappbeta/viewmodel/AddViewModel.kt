package com.example.widgetappbeta.viewmodel

import androidx.lifecycle.*
import com.example.widgetappbeta.model.Inventory
import com.example.widgetappbeta.repository.InventoryRepository
import kotlinx.coroutines.launch

class AddViewModel(private val repository: InventoryRepository) : ViewModel() {

    val codigo = MutableLiveData<String>()
    val nombre = MutableLiveData<String>()
    val precio = MutableLiveData<String>()
    val cantidad = MutableLiveData<String>()

    // LiveData que indica si se puede habilitar el botón
    val isGuardarEnabled: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        val observer = Observer<Any> {
            value = !codigo.value.isNullOrBlank() &&
                    !nombre.value.isNullOrBlank() &&
                    !precio.value.isNullOrBlank() &&
                    !cantidad.value.isNullOrBlank()
        }

        addSource(codigo, observer)
        addSource(nombre, observer)
        addSource(precio, observer)
        addSource(cantidad, observer)
    }

    // Función para guardar el Inventario
    fun guardarProducto(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val cod = codigo.value?.toIntOrNull()
        val nom = nombre.value
        val pre = precio.value?.toDoubleOrNull()
        val cant = cantidad.value?.toIntOrNull()

        if (cod == null || nom.isNullOrBlank() || pre == null || cant == null) {
            onError("Datos inválidos")
            return
        }

        val nuevoItem = Inventory(id = cod, name = nom, price = pre, quantity = cant)

        viewModelScope.launch {
            try {
                repository.saveInventory(nuevoItem)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Error al insertar")
            }
        }
    }
}

// Para pasar el repositorio al ViewModel
class AddViewModelFactory(private val repository: InventoryRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}