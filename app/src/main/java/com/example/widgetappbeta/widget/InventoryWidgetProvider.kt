package com.example.widgetappbeta.widget

import android.app.Application
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import com.example.widgetappbeta.model.Inventory
import com.example.widgetappbeta.repository.InventoryRepository
import com.example.widgetappbeta.view.widget.WidgetView
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InventoryWidgetProvider : AppWidgetProvider() {

    // 1. Definimos la "Ventanilla" (EntryPoint) para pedir dependencias
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WidgetEntryPoint {
        fun getInventoryRepository(): InventoryRepository
    }

    companion object {
        const val ACTION_TOGGLE_VISIBILITY = "com.example.widgetappbeta.TOGGLE_VISIBILITY"
        private var saldoVisible = false

        suspend fun actualizarWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {

            // 2. Accedemos a Hilt a través de la ventanilla usando el Contexto de la Aplicación
            val appContext = context.applicationContext ?: throw IllegalStateException("Context is null")
            val hiltEntryPoint = EntryPointAccessors.fromApplication(
                appContext,
                WidgetEntryPoint::class.java
            )

            // 3. Obtenemos el Repositorio desde Hilt (¡Sin instanciar ViewModel!)
            val repository = hiltEntryPoint.getInventoryRepository()

            // 4. Ejecutamos la lógica (Adaptada del ViewModel anterior)
            val saldoFormateado = obtenerSaldoLogica(repository)

            val widgetView = WidgetView(context)
            val views = widgetView.crearVista(saldoFormateado, saldoVisible)

            manager.updateAppWidget(widgetId, views)
        }

        // Esta lógica antes vivía en el ViewModel. Al estar aquí, el Widget es autónomo.
        // Nota: En una arquitectura Clean pura, esto sería un "UseCase" inyectado.
        private suspend fun obtenerSaldoLogica(repository: InventoryRepository): String = withContext(Dispatchers.IO) {
            try {
                val inventario = repository.getListInventory()
                val saldoTotal = inventario.sumOf { it.price * it.quantity }

                // Formateo (Tu lógica original)
                val saldoString = String.format("%.2f", saldoTotal)
                val partes = saldoString.split(".")
                val entera = partes[0]
                val decimal = partes[1]

                val enteraReversed = entera.reversed()
                val enteraConPuntos = StringBuilder()

                for (i in enteraReversed.indices) {
                    if (i > 0 && i % 3 == 0) enteraConPuntos.append(".")
                    enteraConPuntos.append(enteraReversed[i])
                }

                "$ ${enteraConPuntos.reverse()},$decimal"
            } catch (e: Exception) {
                // Manejo básico de error para que el widget no muestre basura
                "$ 0,00"
            }
        }
    }

    override fun onUpdate(context: Context, manager: AppWidgetManager, appWidgetIds: IntArray) {
        // Usamos GlobalScope o un Scope controlado.
        // GoAsync es preferible para Widgets, pero CoroutineScope funciona para tareas rápidas.
        CoroutineScope(Dispatchers.Main).launch {
            for (id in appWidgetIds) {
                actualizarWidget(context, manager, id)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_TOGGLE_VISIBILITY) {
            saldoVisible = !saldoVisible
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                android.content.ComponentName(context, InventoryWidgetProvider::class.java)
            )

            CoroutineScope(Dispatchers.Main).launch {
                for (id in ids) {
                    actualizarWidget(context, manager, id)
                }
            }
        }
    }
}