package com.example.widgetappbeta.view

import android.content.Intent
import android.os.Bundle
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

        PrefsManager.init(applicationContext)


        setContentView(R.layout.activity_main)

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

        if (widgetRequest) {
            handleWidgetRequest()
        }
    }

    private fun handleWidgetRequest() {
        when {
            viewModel.verififySession() -> {
                executeWidgetAction(buttonIdFromWidget)
            }
        }
    }

    fun onWidgetLoginSuccess() {
        executeWidgetAction(buttonIdFromWidget)
    }

    fun onLoginSuccess() {
        val widgetRequest = intent.getBooleanExtra(InventoryWidgetProvider.EXTRA_WIDGET_REQUEST, false)
        if (widgetRequest) {
            executeWidgetAction(buttonIdFromWidget)
        } else {
            navigateToHomeFragment()
        }
    }

    private fun executeWidgetAction(buttonId: Int) {

        when (buttonId) {
            InventoryWidgetProvider.BUTTON_MANAGE_INVENTORY -> {
                navigateToHomeFragment()
            }
            InventoryWidgetProvider.BUTTON_TOGGLE_SALDO -> {
                toggleSaldoAndReturnHome()
            }
            else -> {
                navigateToHomeFragment()
            }
        }
    }

    private fun navigateToHomeFragment() {

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as? NavHostFragment

        navHostFragment?.navController?.navigate(R.id.homeFragment)
    }

    private fun toggleSaldoAndReturnHome() {

        val widgetIntent = Intent(InventoryWidgetProvider.ACTION_WIDGET_CLICK).apply {
            putExtra(InventoryWidgetProvider.EXTRA_BUTTON_ID,
                InventoryWidgetProvider.BUTTON_TOGGLE_SALDO)
            flags = Intent.FLAG_RECEIVER_FOREGROUND
        }
        sendBroadcast(widgetIntent)

        returnToHomeScreen()
    }

    private fun returnToHomeScreen() {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(homeIntent)
        finish()
    }
}