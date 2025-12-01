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

    // LiveData - Lista de inventario
    private val _listInventory = MutableLiveData<MutableList<InventoryF>>()
    val listInventory: LiveData<MutableList<InventoryF>> get() = _listInventory

    // LiveData - Estado loading
    private val _progressState = MutableLiveData(false)
    val progressState: LiveData<Boolean> get() = _progressState


    // -----------------------------
    //   🔹 GUARDAR PRODUCTO
    // -----------------------------
    fun saveInventory(inventory: InventoryF, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                _progressState.value = true
                repository.saveInventory(inventory)
                getListInventory()
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.message)
            } finally {
                _progressState.value = false
            }
        }
    }


    // -----------------------------
    //   🔹 ELIMINAR PRODUCTO
    // -----------------------------
    fun deleteInventory(inventory: InventoryF, onResult: ((Boolean, String?) -> Unit)? = null) {
        viewModelScope.launch {
            try {
                _progressState.value = true
                repository.deleteInventory(inventory)
                getListInventory()
                onResult?.invoke(true, null)
            } catch (e: Exception) {
                onResult?.invoke(false, e.message)
            } finally {
                _progressState.value = false
            }
        }
    }


    // -----------------------------
    //   🔹 ACTUALIZAR PRODUCTO
    // -----------------------------
    fun updateInventory(inventory: InventoryF, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                _progressState.value = true
                repository.updateInventory(inventory)
                getListInventory()
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.message)
            } finally {
                _progressState.value = false
            }
        }
    }


    // -----------------------------
    //   🔹 OBTENER LISTA
    // -----------------------------
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
}
