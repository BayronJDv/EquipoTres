package com.example.widgetappbeta.view.fragment

import com.example.widgetappbeta.R
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.widgetappbeta.databinding.FragmentEditBinding
import com.example.widgetappbeta.model.InventoryF
import com.example.widgetappbeta.viewmodel.InventoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditFragment : Fragment() {

    private lateinit var binding: FragmentEditBinding
    private val viewModel: InventoryViewModel by viewModels()
    private var producto: InventoryF? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar
        binding.toolbarEdit.title = "Editar producto"
        binding.toolbarEdit.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Recibir producto desde DetailFragment
        producto = arguments?.getSerializable("productoEditar") as? InventoryF

        // Mostrar datos actuales
        producto?.let {
            binding.tvIdValue.text = it.id.toString()
            binding.etNombreEdit.setText(it.name)
            binding.etPrecioEdit.setText(it.price.toString())
            binding.etCantidadEdit.setText(it.quantity.toString())
        }

        configurarValidacionCampos()

        // Guardar cambios
        binding.btnGuardarCambios.setOnClickListener {
            actualizarProducto()
        }
    }

    private fun configurarValidacionCampos() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validarCampos()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etNombreEdit.addTextChangedListener(watcher)
        binding.etPrecioEdit.addTextChangedListener(watcher)
        binding.etCantidadEdit.addTextChangedListener(watcher)
    }

    private fun validarCampos() {
        val nombre = binding.etNombreEdit.text.toString().trim()
        val precio = binding.etPrecioEdit.text.toString().trim()
        val cantidad = binding.etCantidadEdit.text.toString().trim()

        val habilitado = nombre.isNotEmpty() && precio.isNotEmpty() && cantidad.isNotEmpty()

        binding.btnGuardarCambios.isEnabled = habilitado
        binding.btnGuardarCambios.alpha = if (habilitado) 1f else 0.5f
    }

    private fun actualizarProducto() {
        val nombre = binding.etNombreEdit.text.toString().trim()
        val precio = binding.etPrecioEdit.text.toString().toDoubleOrNull()
        val cantidad = binding.etCantidadEdit.text.toString().toIntOrNull()

        if (precio == null || cantidad == null || nombre.isEmpty()) {
            Toast.makeText(requireContext(), "Datos inválidos", Toast.LENGTH_SHORT).show()
            return
        }

        val actualizado = producto?.copy(
            name = nombre,
            price = precio,
            quantity = cantidad
        )

        if (actualizado == null) {
            Toast.makeText(requireContext(), "Error interno", Toast.LENGTH_SHORT).show()
            return
        }

        // Deshabilitar botón
        binding.btnGuardarCambios.isEnabled = false

        // Llamada al ViewModel con callback
        viewModel.updateInventory(actualizado) { success, message ->
            if (success) {
                Toast.makeText(requireContext(), "Producto actualizado", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.homeFragment)
            } else {
                Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_LONG).show()
                binding.btnGuardarCambios.isEnabled = true
            }
        }
    }
}
