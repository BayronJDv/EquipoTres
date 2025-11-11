package com.example.widgetappbeta.viewmodel

import androidx.lifecycle.ViewModel
import com.example.widgetappbeta.sharedprefs.PrefsManager

class LoginViewModel : ViewModel() {

    /**
     * Verifica si ya existe una sesión activa.
     * El LoginFragment usará esto en su onViewCreated para decidir
     * si debe navegar a Home o mostrar el formulario.
     */
    fun verififySession(): Boolean {

        return PrefsManager.isLoggedIn()
    }

    /**
     * Se llama cuando el usuario inicia sesión exitosamente.
     * Guarda el estado de la sesión.
     */
    fun onLoginSuccess() {

        PrefsManager.setLoggedIn(true)
    }
}