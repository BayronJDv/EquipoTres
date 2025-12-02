package com.example.widgetappbeta.view.widget

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.widgetappbeta.R
import com.example.widgetappbeta.widget.InventoryWidgetProvider


class WidgetView(private val context: Context) {

    fun crearVista(saldo: String, saldoVisible: Boolean): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_layout)

        val textoSaldo = if (saldoVisible) saldo else "$ * * * *"
        val iconoVisibilidad = if (saldoVisible)
            R.drawable.visibility_on
        else
            R.drawable.visibility_off

        views.setTextViewText(R.id.saldo, textoSaldo)
        views.setImageViewResource(R.id.icono_mirar, iconoVisibilidad)

        val toggleIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = InventoryWidgetProvider.ACTION_WIDGET_CLICK
            putExtra(InventoryWidgetProvider.EXTRA_BUTTON_ID, InventoryWidgetProvider.BUTTON_TOGGLE_SALDO)
        }
        val togglePendingIntent = PendingIntent.getBroadcast(
            context,
            InventoryWidgetProvider.BUTTON_TOGGLE_SALDO,
            toggleIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        views.setOnClickPendingIntent(R.id.icono_mirar, togglePendingIntent)


        val manageIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = InventoryWidgetProvider.ACTION_WIDGET_CLICK
            putExtra(InventoryWidgetProvider.EXTRA_BUTTON_ID, InventoryWidgetProvider.BUTTON_MANAGE_INVENTORY)
        }
        val managePendingIntent = PendingIntent.getBroadcast(
            context,
            InventoryWidgetProvider.BUTTON_MANAGE_INVENTORY,
            manageIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.icono_gestionar, managePendingIntent)


        return views
    }
}