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
import androidx.navigation.fragment.findNavController
import com.example.widgetappbeta.databinding.FragmentEditBinding
import com.example.widgetappbeta.model.Inventory
import com.example.widgetappbeta.viewmodel.InventoryViewModel

class EditFragment : Fragment() {

    private lateinit var binding: FragmentEditBinding
    private val viewModel: InventoryViewModel by viewModels()
    private var producto: Inventory? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Configurar Toolbar ---
        binding.toolbarEdit.title = "Editar producto"
        binding.toolbarEdit.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // --- Recuperar producto enviado desde DetailFragment ---
        producto = arguments?.getSerializable("productoEditar") as? Inventory

        producto?.let {
            binding.tvIdValue.text = it.id.toString()

            binding.etNombreEdit.setText(it.name)
            binding.etPrecioEdit.setText(it.price.toString())
            binding.etCantidadEdit.setText(it.quantity.toString())
        }

        // Configurar el botón al inicio (desactivado si hay campos vacíos)
        validarCampos()

        // --- Guardar cambios ---
        binding.btnGuardarCambios.setOnClickListener {
            guardarCambios()
        }

        // --- Agregar TextWatchers para validar en tiempo real ---
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validarCampos()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etNombreEdit.addTextChangedListener(textWatcher)
        binding.etPrecioEdit.addTextChangedListener(textWatcher)
        binding.etCantidadEdit.addTextChangedListener(textWatcher)
    }

    private fun validarCampos() {
        val id = binding.tvIdValue.text.toString().trim()
        val nombre = binding.etNombreEdit.text.toString().trim()
        val precio = binding.etPrecioEdit.text.toString().trim()
        val cantidad = binding.etCantidadEdit.text.toString().trim()

        val todosLlenos = id.isNotEmpty() && nombre.isNotEmpty() && precio.isNotEmpty() && cantidad.isNotEmpty()

        binding.btnGuardarCambios.isEnabled = todosLlenos
        binding.btnGuardarCambios.alpha = if (todosLlenos) 1f else 0.7f
    }

    private fun guardarCambios() {
        val nombre = binding.etNombreEdit.text.toString().trim()
        val precioStr = binding.etPrecioEdit.text.toString().trim()
        val cantidadStr = binding.etCantidadEdit.text.toString().trim()

        val precio = precioStr.toDoubleOrNull()
        val cantidad = cantidadStr.toIntOrNull()

        if (precio == null || cantidad == null) {
            Toast.makeText(requireContext(), "Verifique los valores ingresados", Toast.LENGTH_SHORT).show()
            return
        }

        val actualizado = producto?.copy(
            name = nombre,
            price = precio,
            quantity = cantidad
        )

        if (actualizado != null) {
            viewModel.updateInventory(actualizado)
            Toast.makeText(requireContext(), "Producto actualizado correctamente", Toast.LENGTH_SHORT).show()
            findNavController().navigate(com.example.widgetappbeta.R.id.homeFragment)
        } else {
            Toast.makeText(requireContext(), "Error al actualizar el producto", Toast.LENGTH_SHORT).show()
        }

    }
}
