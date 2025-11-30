package com.example.widgetappbeta.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.widgetappbeta.sharedprefs.PrefsManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.junit.Assert.*
import org.junit.After

/**
 * Pruebas unitarias para LoginViewModel
 * Basadas en HU 2.0: Ventana Login y Registro
 * Cubre 100% de los métodos públicos del ViewModel
 */
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockAuth: FirebaseAuth

    @Mock
    private lateinit var mockAuthResult: AuthResult

    @Mock
    private lateinit var mockTask: Task<AuthResult>

    @Mock
    private lateinit var mockUser: FirebaseUser

    private lateinit var viewModel: LoginViewModel
    private lateinit var firebaseAuthMockedStatic: MockedStatic<FirebaseAuth>

    @Before
    fun setup() {
        // Mockear FirebaseAuth.getInstance() para evitar que intente inicializar Firebase
        firebaseAuthMockedStatic = mockStatic(FirebaseAuth::class.java)
        firebaseAuthMockedStatic.`when`<FirebaseAuth> { FirebaseAuth.getInstance() }.thenReturn(mockAuth)

        // Ahora sí podemos crear el ViewModel sin que falle
        viewModel = LoginViewModel()
    }

    @After
    fun tearDown() {
        // Liberar el mock estático
        firebaseAuthMockedStatic.close()
    }

    // ==================== CRITERIO 5: VALIDACIÓN DE CONTRASEÑA ====================

    /**
     * Test 1: Criterio 5 - Validación mínimo 6 dígitos
     */
    @Test
    fun `test criterio 5 - password menor a 6 digitos muestra error Minimo 6 digitos`() {
        // Arrange
        val email = "usuario@test.com"
        val password = "12345"

        // Act
        viewModel.login(email, password)

        // Assert
        val state = viewModel.loginState.value
        assertNotNull("El estado no debe ser null", state)
        assertEquals("Debe mostrar error de mínimo 6 dígitos", "Mínimo 6 dígitos", state!!.error)
        verify(mockAuth, never()).signInWithEmailAndPassword(any(), any())
    }

    /**
     * Test 2: Criterio 5 - Validación máximo 10 dígitos
     */
    @Test
    fun `test criterio 5 - password mayor a 10 digitos muestra error Maximo 10 digitos`() {
        // Arrange
        val email = "usuario@test.com"
        val password = "12345678901"

        // Act
        viewModel.login(email, password)

        // Assert
        val state = viewModel.loginState.value
        assertNotNull("El estado no debe ser null", state)
        assertEquals("Debe mostrar error de máximo 10 dígitos", "Máximo 10 dígitos", state!!.error)
        verify(mockAuth, never()).signInWithEmailAndPassword(any(), any())
    }

    /**
     * Test 3: Criterio 5 - Password con 6 dígitos es válido
     */
    @Test
    fun `test criterio 5 - password con 6 digitos exactos es valido`() {
        // Arrange
        val email = "usuario@test.com"
        val password = "123456"

        whenever(mockAuth.signInWithEmailAndPassword(email, password))
            .thenReturn(mockTask)
        whenever(mockTask.isSuccessful).thenReturn(true)
        whenever(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(mockTask)
            mockTask
        }

        // Act
        viewModel.login(email, password)

        // Assert
        val state = viewModel.loginState.value
        assertNotNull("El estado no debe ser null", state)
        assertNull("No debe haber error de validación", state!!.error)
        verify(mockAuth, times(1)).signInWithEmailAndPassword(email, password)
    }

    /**
     * Test 4: Criterio 5 - Password con 10 dígitos es válido
     */
    @Test
    fun `test criterio 5 - password con 10 digitos exactos es valido`() {
        // Arrange
        val email = "usuario@test.com"
        val password = "1234567890"

        whenever(mockAuth.signInWithEmailAndPassword(email, password))
            .thenReturn(mockTask)
        whenever(mockTask.isSuccessful).thenReturn(true)
        whenever(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(mockTask)
            mockTask
        }

        // Act
        viewModel.login(email, password)

        // Assert
        val state = viewModel.loginState.value
        assertNotNull("El estado no debe ser null", state)
        assertNull("No debe haber error de validación", state!!.error)
        verify(mockAuth, times(1)).signInWithEmailAndPassword(email, password)
    }

    // ==================== CRITERIO 7-8: VALIDACIÓN CAMPOS VACÍOS ====================

    /**
     * Test 5: Criterio 7 - Email vacío no permite login
     */
    @Test
    fun `test criterio 7 - email vacio impide login y muestra error`() {
        // Arrange
        val email = ""
        val password = "123456"

        // Act
        viewModel.login(email, password)

        // Assert
        val state = viewModel.loginState.value
        assertNotNull("El estado no debe ser null", state)
        assertEquals("Debe mostrar error de email requerido", "El email es requerido", state!!.error)
        verify(mockAuth, never()).signInWithEmailAndPassword(any(), any())
    }

    /**
     * Test 6: Criterio 8 - Login se habilita con campos completos
     */
    @Test
    fun `test criterio 8 - login se ejecuta cuando todos los campos estan llenos`() {
        // Arrange
        val email = "usuario@test.com"
        val password = "123456"

        whenever(mockAuth.signInWithEmailAndPassword(email, password))
            .thenReturn(mockTask)
        whenever(mockTask.isSuccessful).thenReturn(true)
        whenever(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(mockTask)
            mockTask
        }

        // Act
        viewModel.login(email, password)

        // Assert
        verify(mockAuth, times(1)).signInWithEmailAndPassword(email, password)
    }

    // ==================== CRITERIO 9: LOGIN INCORRECTO ====================

    /**
     * Test 7: Criterio 9 - Login incorrecto muestra Toast
     */
    @Test
    fun `test criterio 9 - login incorrecto muestra mensaje Login incorrecto`() {
        // Arrange
        val email = "noexiste@test.com"
        val password = "123456"

        whenever(mockAuth.signInWithEmailAndPassword(email, password))
            .thenReturn(mockTask)
        whenever(mockTask.isSuccessful).thenReturn(false)
        whenever(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(mockTask)
            mockTask
        }

        // Act
        viewModel.login(email, password)

        // Assert
        val state = viewModel.loginState.value
        assertNotNull("El estado no debe ser null", state)
        assertEquals("Debe mostrar mensaje de login incorrecto", "Login incorrecto", state!!.error)
        assertFalse("isSuccess debe ser false", state.isSuccess)
    }

    // ==================== CRITERIO 10: LOGIN EXITOSO ====================

    /**
     * Test 8: Criterio 10 - Login exitoso navega a Home
     */
    @Test
    fun `test criterio 10 - login exitoso cambia isSuccess a true para navegar a Home`() {
        // Arrange
        val email = "usuario@test.com"
        val password = "123456"

        whenever(mockAuth.signInWithEmailAndPassword(email, password))
            .thenReturn(mockTask)
        whenever(mockTask.isSuccessful).thenReturn(true)
        whenever(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(mockTask)
            mockTask
        }

        // Act
        viewModel.login(email, password)

        // Assert
        val state = viewModel.loginState.value
        assertNotNull("El estado no debe ser null", state)
        assertTrue("isSuccess debe ser true para navegar a Home", state!!.isSuccess)
        assertNull("No debe haber error", state.error)
        assertTrue("La sesión debe quedar guardada", PrefsManager.isLoggedIn())
    }

    // ==================== CRITERIO 11-12: BOTÓN REGISTRARSE ====================

    /**
     * Test 9: Criterio 11 - Registrarse inactivo con campos vacíos
     */
    @Test
    fun `test criterio 11 - registro no procede con email vacio`() {
        // Arrange
        val email = ""
        val password = "123456"

        // Act
        viewModel.register(email, password)

        // Assert
        val state = viewModel.loginState.value
        assertNotNull("El estado no debe ser null", state)
        assertEquals("Debe mostrar error de email requerido", "El email es requerido", state!!.error)
        verify(mockAuth, never()).createUserWithEmailAndPassword(any(), any())
    }

    /**
     * Test 10: Criterio 12 - Registrarse se habilita con campos completos
     */
    @Test
    fun `test criterio 12 - registro se ejecuta cuando todos los campos estan llenos`() {
        // Arrange
        val email = "nuevo@test.com"
        val password = "123456"

        whenever(mockAuth.createUserWithEmailAndPassword(email, password))
            .thenReturn(mockTask)
        whenever(mockTask.isSuccessful).thenReturn(true)
        whenever(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(mockTask)
            mockTask
        }

        // Act
        viewModel.register(email, password)

        // Assert
        verify(mockAuth, times(1)).createUserWithEmailAndPassword(email, password)
    }

    // ==================== CRITERIO 13: REGISTRO CON USUARIO EXISTENTE ====================

    /**
     * Test 11: Criterio 13 - Registro con usuario existente muestra error
     */
    @Test
    fun `test criterio 13 - registro con usuario existente muestra Error en el registro`() {
        // Arrange
        val email = "yaexiste@test.com"
        val password = "123456"

        whenever(mockAuth.createUserWithEmailAndPassword(email, password))
            .thenReturn(mockTask)
        whenever(mockTask.isSuccessful).thenReturn(false)
        whenever(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(mockTask)
            mockTask
        }

        // Act
        viewModel.register(email, password)

        // Assert
        val state = viewModel.loginState.value
        assertNotNull("El estado no debe ser null", state)
        assertEquals("Debe mostrar error en el registro", "Error en el registro", state!!.error)
        assertFalse("isSuccess debe ser false", state.isSuccess)
    }

    // ==================== CRITERIO 14: REGISTRO EXITOSO ====================

    /**
     * Test 12: Criterio 14 - Registro exitoso navega a Home
     */
    @Test
    fun `test criterio 14 - registro exitoso cambia isSuccess a true para navegar a Home`() {
        // Arrange
        val email = "nuevo@test.com"
        val password = "123456"

        whenever(mockAuth.createUserWithEmailAndPassword(email, password))
            .thenReturn(mockTask)
        whenever(mockTask.isSuccessful).thenReturn(true)
        whenever(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(mockTask)
            mockTask
        }

        // Act
        viewModel.register(email, password)

        // Assert
        val state = viewModel.loginState.value
        assertNotNull("El estado no debe ser null", state)
        assertTrue("isSuccess debe ser true para navegar a Home", state!!.isSuccess)
        assertNull("No debe haber error", state.error)
        assertTrue("La sesión debe quedar guardada", PrefsManager.isLoggedIn())
    }

    // ==================== CRITERIO 17: FIREBASE AUTHENTICATION ====================

    /**
     * Test 13: Criterio 17 - Verificación de sesión con Firebase
     */
    @Test
    fun `test criterio 17 - verififySession valida usuario de Firebase correctamente`() {
        // Arrange
        whenever(mockAuth.currentUser).thenReturn(mockUser)
        PrefsManager.setLoggedIn(true)

        // Act
        val resultado = viewModel.verififySession()

        // Assert
        assertTrue("Debe retornar true cuando hay sesión activa", resultado)
        verify(mockAuth, times(1)).currentUser
    }

    /**
     * Test 14: Criterio 17 - Sin sesión activa retorna false
     */
    @Test
    fun `test criterio 17 - verififySession retorna false sin usuario autenticado`() {
        // Arrange
        whenever(mockAuth.currentUser).thenReturn(null)
        PrefsManager.setLoggedIn(false)

        // Act
        val resultado = viewModel.verififySession()

        // Assert
        assertFalse("Debe retornar false cuando no hay sesión activa", resultado)
        verify(mockAuth, times(1)).currentUser
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Test 15: clearError limpia el estado de error
     */
    @Test
    fun `clearError limpia el mensaje de error del estado`() {
        // Arrange - Generar un error primero
        viewModel.login("", "123456")

        // Act
        viewModel.clearError()

        // Assert
        val state = viewModel.loginState.value
        assertNotNull("El estado no debe ser null", state)
        assertNull("El error debe ser null después de limpiarlo", state!!.error)
    }

    /**
     * Test 16: onLoginSuccess actualiza el estado de sesión
     */
    @Test
    fun `onLoginSuccess actualiza PrefsManager correctamente`() {
        // Arrange
        PrefsManager.setLoggedIn(false)

        // Act
        viewModel.onLoginSuccess()

        // Assert
        assertTrue("PrefsManager debe indicar que hay sesión activa", PrefsManager.isLoggedIn())
    }

    /**
     * Test 17: Login muestra estado de loading
     */
    @Test
    fun `login muestra isLoading true durante la autenticacion`() {
        // Arrange
        val email = "usuario@test.com"
        val password = "123456"

        whenever(mockAuth.signInWithEmailAndPassword(email, password))
            .thenReturn(mockTask)

        var loadingDuringAuth = false

        whenever(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val currentState = viewModel.loginState.value
            if (currentState != null && currentState.isLoading) {
                loadingDuringAuth = true
            }

            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            whenever(mockTask.isSuccessful).thenReturn(true)
            listener.onComplete(mockTask)
            mockTask
        }

        // Act
        viewModel.login(email, password)

        // Assert
        assertTrue("Debe mostrar loading durante la autenticación", loadingDuringAuth)

        val finalState = viewModel.loginState.value
        assertNotNull("El estado final no debe ser null", finalState)
        assertFalse("isLoading debe ser false al finalizar", finalState!!.isLoading)
    }

    /**
     * Test 18: Registro con password inválido no llama a Firebase
     */
    @Test
    fun `registro con password invalido no ejecuta llamada a Firebase`() {
        // Arrange
        val email = "usuario@test.com"
        val password = "123"

        // Act
        viewModel.register(email, password)

        // Assert
        verify(mockAuth, never()).createUserWithEmailAndPassword(any(), any())

        val state = viewModel.loginState.value
        assertNotNull("El estado no debe ser null", state)
        assertNotNull("Debe haber un mensaje de error", state!!.error)
    }
}