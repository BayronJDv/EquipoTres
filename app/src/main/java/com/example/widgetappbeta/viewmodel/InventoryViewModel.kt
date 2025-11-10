package com.example.widgetappbeta.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.widgetappbeta.model.Inventory
import com.example.widgetappbeta.repository.InventoryRepository
import com.example.widgetappbeta.sharedprefs.PrefsManager
import kotlinx.coroutines.launch


class InventoryViewModel(application: Application) : AndroidViewModel(application) {
    val context = getApplication<Application>()
    private val inventoryRepository = InventoryRepository(context)


    // a esta lista accede el view model
    private val _listImventory = MutableLiveData<MutableList<Inventory>>()
    // esta lisat solo es la de lectura a la que accede la view
    val listInventory: LiveData<MutableList<Inventory>> get()= _listImventory


    // lo mismo para el estado de la consulta
    private val _progresState = MutableLiveData(false)
    val progresState: LiveData<Boolean> get() = _progresState




    // funcion para obetener el inventario
    fun getListInventory(){
        viewModelScope.launch {
            _progresState.value = true
            try {
                _listImventory.value = inventoryRepository.getListInventory()
                _progresState.value = false

            } catch (e: Exception){
                _progresState.value = false


            }
        }
    }



}