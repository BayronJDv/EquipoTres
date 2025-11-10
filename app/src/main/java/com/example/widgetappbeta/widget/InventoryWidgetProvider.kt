package com.example.widgetappbeta.widget

import android.app.Application
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.*
import com.example.widgetappbeta.viewmodel.WidgetViewModel
import com.example.widgetappbeta.view.widget.WidgetView

class InventoryWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_TOGGLE_VISIBILITY = "com.example.widgetappbeta.TOGGLE_VISIBILITY"

        // estado del widget true = visible, false = invisible
        private var saldoVisible = false


        suspend fun actualizarWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
            val app = context.applicationContext as Application
            val viewModel = WidgetViewModel(app)
            val saldo = viewModel.obtenerSaldoFormateado()

            val widgetView = WidgetView(context)
            val views = widgetView.crearVista(saldo, saldoVisible)

            manager.updateAppWidget(widgetId, views)
        }
    }

    override fun onUpdate(context: Context, manager: AppWidgetManager, appWidgetIds: IntArray) {
        CoroutineScope(Dispatchers.Main).launch {
            for (id in appWidgetIds) {
                actualizarWidget(context, manager, id)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == ACTION_TOGGLE_VISIBILITY) {
            saldoVisible = !saldoVisible // Cambiar visibilidad de widget
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
