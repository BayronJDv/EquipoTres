package com.example.widgetappbeta.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.example.widgetappbeta.R
import com.example.widgetappbeta.sharedprefs.PrefsManager
import com.example.widgetappbeta.viewmodel.LoginViewModel
import com.example.widgetappbeta.widget.InventoryWidgetProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()
    private var buttonIdFromWidget = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // INICIALIZAR PrefsManager PRIMERO, antes de cualquier otra cosa
        PrefsManager.init(applicationContext)

        Log.d("MainActivity", "PrefsManager inicializado")

        setContentView(R.layout.activity_main)

        // Manejar intent del widget
        handleWidgetIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleWidgetIntent(intent)
    }

    private fun handleWidgetIntent(intent: Intent) {
        val widgetRequest = intent.getBooleanExtra(
            InventoryWidgetProvider.EXTRA_WIDGET_REQUEST,
            false
        )

        buttonIdFromWidget = intent.getIntExtra(
            InventoryWidgetProvider.EXTRA_BUTTON_ID,
            -1
        )

        Log.d("MainActivity", "Widget request: $widgetRequest, Button ID: $buttonIdFromWidget")

        if (widgetRequest) {
            handleWidgetRequest()
        }
    }

    private fun handleWidgetRequest() {
        when {
            viewModel.verififySession() -> {
                Log.d("MainActivity", "Usuario YA logueado, ejecutando acción del widget")
                executeWidgetAction(buttonIdFromWidget)
            }
            else -> {
                Log.d("MainActivity", "Usuario NO logueado, se manejará desde LoginFragment")
            }
        }
    }

    fun onWidgetLoginSuccess() {
        Log.d("MainActivity", "onWidgetLoginSuccess llamado con buttonId: $buttonIdFromWidget")
        executeWidgetAction(buttonIdFromWidget)
    }

    private fun executeWidgetAction(buttonId: Int) {
        Log.d("MainActivity", "Ejecutando acción del widget para botón ID: $buttonId")

        when (buttonId) {
            InventoryWidgetProvider.BUTTON_MANAGE_INVENTORY -> {
                navigateToHomeFragment()
            }
            InventoryWidgetProvider.BUTTON_TOGGLE_SALDO -> {
                toggleSaldoAndReturnHome()
            }
            else -> {
                Log.w("MainActivity", "ID de botón de widget desconocido: $buttonId")
                navigateToHomeFragment()
            }
        }
    }

    private fun navigateToHomeFragment() {
        Log.d("MainActivity", "Navegando a HomeFragment")

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as? NavHostFragment

        navHostFragment?.navController?.navigate(R.id.homeFragment)
    }

    private fun toggleSaldoAndReturnHome() {
        Log.d("MainActivity", "Alternando saldo y volviendo al escritorio")

        val widgetIntent = Intent(InventoryWidgetProvider.ACTION_WIDGET_CLICK).apply {
            putExtra(InventoryWidgetProvider.EXTRA_BUTTON_ID,
                InventoryWidgetProvider.BUTTON_TOGGLE_SALDO)
            flags = Intent.FLAG_RECEIVER_FOREGROUND
        }
        sendBroadcast(widgetIntent)

        returnToHomeScreen()
    }

    private fun returnToHomeScreen() {
        Log.d("MainActivity", "Volviendo al escritorio")
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(homeIntent)
        finish()
    }
}