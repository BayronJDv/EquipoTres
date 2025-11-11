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
            action = InventoryWidgetProvider.ACTION_TOGGLE_VISIBILITY
        }
        val togglePendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            toggleIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        views.setOnClickPendingIntent(R.id.icono_mirar, togglePendingIntent)

        val intentGestionar = Intent(context, com.example.widgetappbeta.view.MainActivity::class.java)
        val pendingIntentGestionar = PendingIntent.getActivity(
            context,
            1,
            intentGestionar,
            PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.icono_gestionar, pendingIntentGestionar)

        return views
    }
}
