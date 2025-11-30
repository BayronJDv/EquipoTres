package com.example.widgetappbeta.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
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
 *
 * COBERTURA: 7 tests que cubren el 43% de los métodos públicos:
 * - login() ✓
 * - register() ✓
 * - clearError() ✓
 * - validateInput() (privado, testeado indirectamente) ✓
 */
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockAuth: FirebaseAuth

    @Mock
    private lateinit var mockTask: Task<AuthResult>

    private lateinit var viewModel: LoginViewModel
    private lateinit var firebaseAuthMockedStatic: MockedStatic<FirebaseAuth>

    @Before
    fun setup() {
        // Mockear FirebaseAuth.getInstance()
        firebaseAuthMockedStatic = mockStatic(FirebaseAuth::class.java)
        firebaseAuthMockedStatic.`when`<FirebaseAuth> { FirebaseAuth.getInstance() }.thenReturn(mockAuth)

        viewModel = LoginViewModel()
    }

    @After
    fun tearDown() {
        firebaseAuthMockedStatic.close()
    }

    // ==================== TEST 1: VALIDACIÓN PASSWORD CORTO ====================
    /**
     * Password mínimo 6 dígitos
     */
    @Test
    fun `test 1 - password menor a 6 digitos muestra error`() {
        // Given
        val email = "test@test.com"
        val password = "12345"

        // When
        viewModel.login(email, password)

        // Then
        val state = viewModel.loginState.value
        assertNotNull("El estado no debe ser null", state)
        assertEquals("Debe mostrar error de mínimo 6 dígitos", "Mínimo 6 dígitos", state!!.error)
        assertFalse("isSuccess debe ser false", state.isSuccess)
        verify(mockAuth, never()).signInWithEmailAndPassword(any(), any())
    }

    // ==================== TEST 2: VALIDACIÓN PASSWORD LARGO ====================
    /**
     * Password máximo 10 dígitos
     */
    @Test
    fun `test 2 - password mayor a 10 digitos muestra error`() {
        // Given
        val email = "test@test.com"
        val password = "12345678901"

        // When
        viewModel.login(email, password)

        // Then
        val state = viewModel.loginState.value
        assertNotNull("El estado no debe ser null", state)
        assertEquals("Debe mostrar error de máximo 10 dígitos", "Máximo 10 dígitos", state!!.error)
        assertFalse("isSuccess debe ser false", state.isSuccess)
        verify(mockAuth, never()).signInWithEmailAndPassword(any(), any())
    }

    // ==================== TEST 3: VALIDACIÓN EMAIL VACÍO ====================
    /**
     * Email requerido
     */
    @Test
    fun `test 3 - email vacio muestra error`() {
        // Given
        val email = ""
        val password = "123456"

        // When
        viewModel.login(email, password)

        // Then
        val state = viewModel.loginState.value
        assertNotNull("El estado no debe ser null", state)
        assertEquals("Debe mostrar error de email requerido", "El email es requerido", state!!.error)
        assertFalse("isSuccess debe ser false", state.isSuccess)
        verify(mockAuth, never()).signInWithEmailAndPassword(any(), any())
    }

    // ==================== TEST 4: LOGIN FALLIDO ====================
    /**
     * Login incorrecto muestra mensaje
     */
    @Test
    fun `test 4 - login con credenciales incorrectas muestra error`() {
        // Given
        val email = "wrong@test.com"
        val password = "123456"

        // Configurar mock para que falle
        whenever(mockAuth.signInWithEmailAndPassword(email, password))
            .thenReturn(mockTask)
        whenever(mockTask.isSuccessful).thenReturn(false)
        whenever(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(mockTask)
            mockTask
        }

        // When
        viewModel.login(email, password)

        // Then
        val state = viewModel.loginState.value
        assertNotNull("El estado no debe ser null", state)
        assertEquals("Debe mostrar mensaje de login incorrecto", "Login incorrecto", state!!.error)
        assertFalse("isSuccess debe ser false", state.isSuccess)
        verify(mockAuth, times(1)).signInWithEmailAndPassword(email, password)
    }

    // ==================== TEST 5: REGISTRO FALLIDO ====================
    /**
     * Registro con usuario existente muestra error
     */
    @Test
    fun `test 5 - registro con usuario existente muestra error`() {
        // Given
        val email = "existente@test.com"
        val password = "123456"

        // Configurar mock para que falle
        whenever(mockAuth.createUserWithEmailAndPassword(email, password))
            .thenReturn(mockTask)
        whenever(mockTask.isSuccessful).thenReturn(false)
        whenever(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(mockTask)
            mockTask
        }

        // When
        viewModel.register(email, password)

        // Then
        val state = viewModel.loginState.value
        assertNotNull("El estado no debe ser null", state)
        assertEquals("Debe mostrar error en el registro", "Error en el registro", state!!.error)
        assertFalse("isSuccess debe ser false", state.isSuccess)
        verify(mockAuth, times(1)).createUserWithEmailAndPassword(email, password)
    }

    // ==================== TEST 6: REGISTRO CON EMAIL VACÍO ====================
    /**
     * Registro requiere email
     */
    @Test
    fun `test 6 - registro con email vacio no ejecuta Firebase`() {
        // Given
        val email = ""
        val password = "123456"

        // When
        viewModel.register(email, password)

        // Then
        verify(mockAuth, never()).createUserWithEmailAndPassword(any(), any())
        val state = viewModel.loginState.value
        assertNotNull("El estado no debe ser null", state)
        assertNotNull("Debe haber un mensaje de error", state!!.error)
        assertFalse("isSuccess debe ser false", state.isSuccess)
    }

    // ==================== TEST 7: CLEAR ERROR ====================
    /**
     * Limpiar mensaje de error
     */
    @Test
    fun `test 7 - clearError limpia el mensaje de error`() {
        // Given - Generamos un error primero
        viewModel.login("", "123456")

        // Verificar que hay un error
        assertNotNull("Debe haber un error antes de limpiar", viewModel.loginState.value?.error)

        // When
        viewModel.clearError()

        // Then
        val state = viewModel.loginState.value
        assertNotNull("El estado no debe ser null", state)
        assertNull("El error debe ser null después de limpiarlo", state!!.error)
    }
}