package com.example.widgetappbeta.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.widgetappbeta.model.InventoryF
import com.example.widgetappbeta.repository.InventoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class InventoryViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var inventoryViewModel: InventoryViewModel

    @Mock
    private lateinit var inventoryRepository: InventoryRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        inventoryViewModel = InventoryViewModel(inventoryRepository)
    }


    // ============================================
    // PRUEBAS DE GUARDAR INVENTARIO
    // ============================================

    @Test
    fun `test saveInventory success`() = runBlocking {
        // given
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val inventory = InventoryF(id = 1, name = "Producto Test", price = 100.0, quantity = 10)

        // when
        inventoryViewModel.saveInventory(inventory) { _, _ -> }

        // then
        verify(inventoryRepository).saveInventory(inventory)
    }

    @Test
    fun `test saveInventory con updateList true actualiza la lista`() = runBlocking {
        // given
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val inventory = InventoryF(id = 1, name = "Producto", price = 50.0, quantity = 5)
        val mockList = mutableListOf(inventory)
        `when`(inventoryRepository.getListInventory()).thenReturn(mockList)

        // when
        inventoryViewModel.saveInventory(inventory, updateList = true) { _, _ -> }

        // then
        verify(inventoryRepository).saveInventory(inventory)
        verify(inventoryRepository).getListInventory()
        assertEquals(mockList, inventoryViewModel.listInventory.value)
    }

    @Test
    fun `test saveInventoryFromForm con datos validos guarda correctamente`() = runBlocking {
        // given
        Dispatchers.setMain(UnconfinedTestDispatcher())
        inventoryViewModel.codigo.value = "1"
        inventoryViewModel.nombre.value = "Producto Test"
        inventoryViewModel.precio.value = "100.0"
        inventoryViewModel.cantidad.value = "10"
        var resultSuccess = false

        // when
        inventoryViewModel.saveInventoryFromForm { success, _ ->
            resultSuccess = success
        }

        // then
        assertTrue(resultSuccess)
        verify(inventoryRepository).saveInventory(org.mockito.kotlin.any())
    }

    @Test
    fun `test saveInventoryFromForm con codigo invalido retorna false`() = runBlocking {
        // given
        Dispatchers.setMain(UnconfinedTestDispatcher())
        inventoryViewModel.codigo.value = "abc"
        inventoryViewModel.nombre.value = "Producto"
        inventoryViewModel.precio.value = "100.0"
        inventoryViewModel.cantidad.value = "10"
        var resultSuccess = true
        var resultMessage = ""

        // when
        inventoryViewModel.saveInventoryFromForm { success, message ->
            resultSuccess = success
            resultMessage = message ?: ""
        }

        // then
        assertFalse(resultSuccess)
        assertEquals("Datos inválidos", resultMessage)
        verify(inventoryRepository, never()).saveInventory(org.mockito.kotlin.any())
    }


    // ============================================
    // PRUEBAS DE ELIMINAR INVENTARIO
    // ============================================

    @Test
    fun `test deleteInventory success`() = runBlocking {
        // given
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val inventory = InventoryF(id = 1, name = "Producto", price = 50.0, quantity = 5)

        // when
        inventoryViewModel.deleteInventory(inventory) { _, _ -> }

        // then
        verify(inventoryRepository).deleteInventory(inventory)
    }

    @Test
    fun `test deleteInventory retorna true en callback cuando es exitoso`() = runBlocking {
        // given
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val inventory = InventoryF(id = 1, name = "Producto", price = 50.0, quantity = 5)
        var resultSuccess = false

        // when
        inventoryViewModel.deleteInventory(inventory) { success, _ ->
            resultSuccess = success
        }

        // then
        assertTrue(resultSuccess)
    }


    // ============================================
    // PRUEBAS DE ACTUALIZAR INVENTARIO
    // ============================================

    @Test
    fun `test updateInventory success`() = runBlocking {
        // given
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val inventory = InventoryF(id = 1, name = "Producto Actualizado", price = 150.0, quantity = 20)

        // when
        inventoryViewModel.updateInventory(inventory) { _, _ -> }

        // then
        verify(inventoryRepository).updateInventory(inventory)
    }

    @Test
    fun `test updateInventoryFromForm con datos validos actualiza correctamente`() = runBlocking {
        // given
        Dispatchers.setMain(UnconfinedTestDispatcher())
        inventoryViewModel.codigo.value = "1"
        inventoryViewModel.nombre.value = "Producto Actualizado"
        inventoryViewModel.precio.value = "200.0"
        inventoryViewModel.cantidad.value = "25"
        var resultSuccess = false

        // when
        inventoryViewModel.updateInventoryFromForm { success, _ ->
            resultSuccess = success
        }

        // then
        assertTrue(resultSuccess)
        verify(inventoryRepository).updateInventory(org.mockito.kotlin.any())
    }

    @Test
    fun `test updateInventoryFromForm con datos invalidos retorna false`() = runBlocking {
        // given
        Dispatchers.setMain(UnconfinedTestDispatcher())
        inventoryViewModel.codigo.value = "abc"
        inventoryViewModel.nombre.value = "Producto"
        inventoryViewModel.precio.value = "100.0"
        inventoryViewModel.cantidad.value = "10"
        var resultSuccess = true
        var resultMessage = ""

        // when
        inventoryViewModel.updateInventoryFromForm { success, message ->
            resultSuccess = success
            resultMessage = message ?: ""
        }

        // then
        assertFalse(resultSuccess)
        assertEquals("Datos inválidos", resultMessage)
        verify(inventoryRepository, never()).updateInventory(org.mockito.kotlin.any())
    }


    // ============================================
    // PRUEBAS DE OBTENER LISTA
    // ============================================

    @Test
    fun `test getListInventory obtiene lista correctamente`() = runBlocking {
        // given
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val mockProducts = mutableListOf(
            InventoryF(1, "Producto 1", 50.0, 5),
            InventoryF(2, "Producto 2", 75.0, 8),
            InventoryF(3, "Producto 3", 120.0, 3)
        )
        `when`(inventoryRepository.getListInventory()).thenReturn(mockProducts)

        // when
        inventoryViewModel.getListInventory()

        // then
        verify(inventoryRepository).getListInventory()
        assertEquals(mockProducts, inventoryViewModel.listInventory.value)
    }


    // ============================================
    // PRUEBAS DE FORMULARIO
    // ============================================

    @Test
    fun `test clearForm limpia todos los campos`() {
        // given
        inventoryViewModel.codigo.value = "1"
        inventoryViewModel.nombre.value = "Producto"
        inventoryViewModel.precio.value = "100.0"
        inventoryViewModel.cantidad.value = "10"

        // when
        inventoryViewModel.clearForm()

        // then
        assertEquals("", inventoryViewModel.codigo.value)
        assertEquals("", inventoryViewModel.nombre.value)
        assertEquals("", inventoryViewModel.precio.value)
        assertEquals("", inventoryViewModel.cantidad.value)
    }

    @Test
    fun `test loadInventoryToForm carga datos correctamente`() {
        // given
        val inventory = InventoryF(
            id = 5,
            name = "Producto Test",
            price = 250.0,
            quantity = 15
        )

        // when
        inventoryViewModel.loadInventoryToForm(inventory)

        // then
        assertEquals("5", inventoryViewModel.codigo.value)
        assertEquals("Producto Test", inventoryViewModel.nombre.value)
        assertEquals("250.0", inventoryViewModel.precio.value)
        assertEquals("15", inventoryViewModel.cantidad.value)
    }
}