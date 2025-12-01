package com.example.widgetappbeta.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.widgetappbeta.BR.viewModel
import com.example.widgetappbeta.databinding.FragmentAddBinding
import com.example.widgetappbeta.model.Inventory
import com.example.widgetappbeta.model.InventoryF
import com.example.widgetappbeta.repository.InventoryRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.example.widgetappbeta.viewmodel.InventoryViewModel


@AndroidEntryPoint
class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding
    private val inventoryViewModel: InventoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Botón atrás en el Toolbar
        binding.toolbarAdd.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        configurarValidacionCampos()

        binding.btnGuardar.setOnClickListener {
            guardarProducto()
        }
    }

    private fun configurarValidacionCampos() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnGuardar.isEnabled = camposCompletos()
                binding.btnGuardar.alpha = if (camposCompletos()) 1f else 0.5f
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etCodigo.addTextChangedListener(watcher)
        binding.etNombre.addTextChangedListener(watcher)
        binding.etPrecio.addTextChangedListener(watcher)
        binding.etCantidad.addTextChangedListener(watcher)
    }

    private fun camposCompletos(): Boolean {
        return binding.etCodigo.text!!.isNotEmpty() &&
                binding.etNombre.text!!.isNotEmpty() &&
                binding.etPrecio.text!!.isNotEmpty() &&
                binding.etCantidad.text!!.isNotEmpty()
    }

    private fun guardarProducto() {

        val id = binding.etCodigo.text.toString().toIntOrNull()
        val nombre = binding.etNombre.text.toString().trim()
        val precio = binding.etPrecio.text.toString().toDoubleOrNull()
        val cantidad = binding.etCantidad.text.toString().toIntOrNull()

        if (id == null || precio == null || cantidad == null || nombre.isEmpty()) {
            Toast.makeText(requireContext(), "Datos inválidos", Toast.LENGTH_SHORT).show()
            return
        }

        val nuevo = InventoryF(
            id = id,
            name = nombre,
            price = precio,
            quantity = cantidad
        )

        // Desactivar botón para evitar doble click
        binding.btnGuardar.isEnabled = false

        // Llamada correcta al ViewModel
        inventoryViewModel.saveInventory(nuevo) { success, message ->
            if (success) {
                Toast.makeText(requireContext(), "Producto guardado", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_LONG).show()
                binding.btnGuardar.isEnabled = true
            }
        }
    }
}
