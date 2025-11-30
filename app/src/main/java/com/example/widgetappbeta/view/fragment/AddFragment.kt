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
import com.example.widgetappbeta.databinding.FragmentAddBinding
import com.example.widgetappbeta.model.Inventory
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

        // Flecha atras del toolbar
        val navIcon = binding.toolbarAdd.navigationIcon
        navIcon?.setTint(resources.getColor(android.R.color.white, null))
        binding.toolbarAdd.navigationIcon = navIcon

        binding.toolbarAdd.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        configurarValidacionCampos()
        configurarBotonGuardar()

        // Desactiva botón guardar
        binding.btnGuardar.isEnabled = false
        binding.btnGuardar.alpha = 0.5f
    }

    // Habilita el botón guardar solo cuando todos los campos tengan texto
    private fun configurarValidacionCampos() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnGuardar.isEnabled = camposCompletos()
                // Cambia la opacdad
                binding.btnGuardar.alpha = if (binding.btnGuardar.isEnabled) 1f else 0.5f
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etCodigo.addTextChangedListener(textWatcher)
        binding.etNombre.addTextChangedListener(textWatcher)
        binding.etPrecio.addTextChangedListener(textWatcher)
        binding.etCantidad.addTextChangedListener(textWatcher)
    }

    private fun camposCompletos(): Boolean {
        return binding.etCodigo.text?.isNotEmpty() == true &&
                binding.etNombre.text?.isNotEmpty() == true &&
                binding.etPrecio.text?.isNotEmpty() == true &&
                binding.etCantidad.text?.isNotEmpty() == true
    }

    //Guardar el producto
    private fun configurarBotonGuardar() {
        binding.btnGuardar.setOnClickListener {
            try {
                val codigo = binding.etCodigo.text.toString().toInt()
                val nombre = binding.etNombre.text.toString()
                val precio = binding.etPrecio.text.toString().toDouble()
                val cantidad = binding.etCantidad.text.toString().toInt()

                val nuevoItem = Inventory(
                    id = codigo,
                    name = nombre,
                    price = precio,
                    quantity = cantidad
                )

                lifecycleScope.launch {
                    try {
                        inventoryViewModel.saveInventory(nuevoItem)
                        Toast.makeText(requireContext(), "Producto agregado correctamente", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp() // volver al Home
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error al insertar: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Datos inválidos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
