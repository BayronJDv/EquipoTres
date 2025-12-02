package com.example.widgetappbeta.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.example.widgetappbeta.view.MainActivity
import com.example.widgetappbeta.repository.InventoryRepository
import com.example.widgetappbeta.sharedprefs.PrefsManager
import com.example.widgetappbeta.view.widget.WidgetView
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean

class InventoryWidgetProvider : AppWidgetProvider() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WidgetEntryPoint {
        fun getInventoryRepository(): InventoryRepository
    }

    companion object {

        const val ACTION_WIDGET_CLICK = "com.example.widgetappbeta.WIDGET_BUTTON_CLICK"

        // ELIMINAR: ACTION_LOGIN_SUCCESS ya no se usa
        // const val ACTION_LOGIN_SUCCESS = "com.example.widgetappbeta.LOGIN_SUCCESS"

        const val EXTRA_BUTTON_ID = "com.example.widgetappbeta.EXTRA_BUTTON_ID"
        const val BUTTON_TOGGLE_SALDO = 1
        const val BUTTON_MANAGE_INVENTORY = 2

        // ELIMINAR: Ya no se usan estas constantes
        // const val EXTRA_REDIRECT_AFTER_LOGIN = "com.example.widgetappbeta.REDIRECT_AFTER_LOGIN"
        // const val REDIRECT_TO_WIDGET_REFRESH = "WIDGET_REFRESH"
        // const val REDIRECT_TO_MAIN_ACTIVITY = "MAIN_ACTIVITY"

        const val EXTRA_WIDGET_REQUEST = "com.example.widgetappbeta.WIDGET_REQUEST"

        // Usar AtomicBoolean para evitar condiciones de carrera
        private val saldoVisible = AtomicBoolean(false)

        private val widgetScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

        fun createLoginIntent(context: Context, buttonId: Int): Intent {
            return Intent(context, MainActivity::class.java).apply {  // Cambiado a MainActivity
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                // SOLO enviar parámetros esenciales
                putExtra(EXTRA_WIDGET_REQUEST, true)
                putExtra(EXTRA_BUTTON_ID, buttonId)
            }
        }

        suspend fun actualizarWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
            withContext(Dispatchers.IO) {
                val appContext = context.applicationContext
                val entryPoint = EntryPointAccessors.fromApplication(appContext, WidgetEntryPoint::class.java)
                val repository = entryPoint.getInventoryRepository()
                val saldo = obtenerSaldo(repository)

                withContext(Dispatchers.Main) {
                    val views = WidgetView(context).crearVista(saldo, saldoVisible.get())
                    manager.updateAppWidget(widgetId, views)
                }
            }
        }

        private suspend fun obtenerSaldo(repository: InventoryRepository): String = withContext(Dispatchers.IO) {
            val inventario = repository.getListInventory()

            val saldoTotal = inventario.sumOf { inventory ->
                inventory.price * inventory.quantity
            }

            val saldo = String.format("%.2f", saldoTotal)

            val partes = saldo.split(".")
            val entera = partes[0]
            val decimal = partes[1]

            // Formatear parte entera con separadores
            val enteraReversed = entera.reversed()
            val enteraConPuntos = StringBuilder()

            for (i in enteraReversed.indices) {
                if (i > 0 && i % 3 == 0) enteraConPuntos.append(".")
                enteraConPuntos.append(enteraReversed[i])
            }

            "$ ${enteraConPuntos.reverse()},$decimal"
        }
    }

    override fun onUpdate(context: Context, manager: AppWidgetManager, appWidgetIds: IntArray) {
        widgetScope.launch {
            appWidgetIds.forEach { id ->
                try {
                    actualizarWidget(context, manager, id)
                } catch (e: Exception) {
                }
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        PrefsManager.init(context)

        when (intent.action) {
            ACTION_WIDGET_CLICK -> {
                val buttonId = intent.getIntExtra(EXTRA_BUTTON_ID, -1)

                val prefs = PrefsManager
                val auth = FirebaseAuth.getInstance()
                val isLoggedIn = prefs.isLoggedIn() && auth.currentUser != null


                if (isLoggedIn) {
                    // Usuario YA logueado - ejecutar acción directamente
                    widgetScope.launch {
                        val manager = AppWidgetManager.getInstance(context)
                        val widgetIds = manager.getAppWidgetIds(
                            ComponentName(context, InventoryWidgetProvider::class.java)
                        )
                        handleLoggedInActions(context, manager, widgetIds, buttonId)
                    }
                } else {
                    handleNotLoggedInActions(context, buttonId)
                }
            }

            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {
                widgetScope.launch {
                    val manager = AppWidgetManager.getInstance(context)
                    val widgetIds = manager.getAppWidgetIds(
                        ComponentName(context, InventoryWidgetProvider::class.java)
                    )
                    widgetIds.forEach { id ->
                        try {
                            actualizarWidget(context, manager, id)
                        } catch (e: Exception) {
                        }
                    }
                }
            }
        }
    }

    private suspend fun handleLoggedInActions(
        context: Context,
        manager: AppWidgetManager,
        ids: IntArray,
        buttonId: Int
    ) {
        when (buttonId) {
            BUTTON_TOGGLE_SALDO -> {
                val newValue = !saldoVisible.get()
                saldoVisible.set(newValue)


                ids.forEach { id ->
                    actualizarWidget(context, manager, id)
                }
            }

            BUTTON_MANAGE_INVENTORY -> {
                withContext(Dispatchers.Main) {
                    Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(this)
                    }
                }
            }
        }
    }

    private fun handleNotLoggedInActions(context: Context, buttonId: Int) {
        val loginIntent = createLoginIntent(context, buttonId)
        context.startActivity(loginIntent)
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        widgetScope.coroutineContext.cancelChildren()
    }
}