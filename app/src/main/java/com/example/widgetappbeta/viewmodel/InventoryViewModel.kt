package com.example.widgetappbeta.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.widgetappbeta.model.Inventory
import com.example.widgetappbeta.repository.InventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val repository: InventoryRepository
) : ViewModel() {
    // LiveData con la lista de productos
    private val _listInventory = MutableLiveData<MutableList<Inventory>>()
    val listInventory: LiveData<MutableList<Inventory>> get() = _listInventory

    // Estado de carga
    private val _progressState = MutableLiveData(false)
    val progressState: LiveData<Boolean> get() = _progressState

    // Guardar producto
    fun saveInventory(inventory: Inventory) {
        viewModelScope.launch {
            repository.saveInventory(inventory)
        }
    }

    // Eliminar producto
    fun deleteInventory(inventory: Inventory) {
        viewModelScope.launch {
            repository.deleteInventory(inventory)
        }
    }

    // Actualizar producto
    fun updateInventory(inventory: Inventory) {
        viewModelScope.launch {
            repository.updateInventory(inventory)
        }
    }

    // Obtener lista de inventario
    fun getListInventory() {
        viewModelScope.launch {
            _progressState.value = true
            try {
                _listInventory.value = repository.getListInventory()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _progressState.value = false
            }
        }
    }
}
