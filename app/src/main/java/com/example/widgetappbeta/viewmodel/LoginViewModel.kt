package com.example.widgetappbeta.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.widgetappbeta.sharedprefs.PrefsManager
import com.google.firebase.auth.FirebaseAuth

data class LoginState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    /**
     * Verifica si ya existe una sesión activa.
     */
    fun verififySession(): Boolean {
        return PrefsManager.isLoggedIn() && auth.currentUser != null
    }

    /**
     * Inicia sesión con email y contraseña.
     */
    fun login(email: String, password: String) {
        if (!validateInput(email, password)) return

        _loginState.value = LoginState(isLoading = true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login exitoso
                    PrefsManager.setLoggedIn(true)
                    _loginState.value = LoginState(isSuccess = true)
                } else {
                    // Login fallido
                    _loginState.value = LoginState(error = "Login incorrecto")
                }
            }
    }

    /**
     * Registra un nuevo usuario con email y contraseña.
     */
    fun register(email: String, password: String) {
        if (!validateInput(email, password)) return

        _loginState.value = LoginState(isLoading = true)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registro exitoso
                    PrefsManager.setLoggedIn(true)
                    _loginState.value = LoginState(isSuccess = true)
                } else {
                    // Registro fallido
                    _loginState.value = LoginState(error = "Error en el registro")
                }
            }
    }

    /**
     * Valida los datos de entrada.
     */
    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            _loginState.value = LoginState(error = "El email es requerido")
            return false
        }

        if (password.length < 6) {
            _loginState.value = LoginState(error = "Mínimo 6 dígitos")
            return false
        }

        if (password.length > 10) {
            _loginState.value = LoginState(error = "Máximo 10 dígitos")
            return false
        }

        return true
    }

    /**
     * Limpia el error del estado.
     */
    fun clearError() {
        _loginState.value = _loginState.value?.copy(error = null)
    }

    /**
     * Se llama cuando el usuario inicia sesión exitosamente.
     */
    fun onLoginSuccess() {
        PrefsManager.setLoggedIn(true)
    }
}