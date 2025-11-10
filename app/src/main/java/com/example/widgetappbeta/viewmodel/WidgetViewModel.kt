package com.example.widgetappbeta.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.widgetappbeta.model.Inventory
import com.example.widgetappbeta.repository.InventoryRepository

class WidgetViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>()

    private val inventoryRepository = InventoryRepository(context)

    suspend fun obtenerSaldoFormateado(): String = withContext(Dispatchers.IO) {
        "perfectoManurdo"
    }
}
