package com.example.widgetappbeta.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.widgetappbeta.R
import com.example.widgetappbeta.databinding.FragmentDetailBinding
import com.example.widgetappbeta.model.InventoryF
import com.example.widgetappbeta.viewmodel.InventoryViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InventoryViewModel by viewModels()
    private var producto: InventoryF? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupObservers()
        loadProductData()
        setupListeners()
    }

    private fun setupToolbar() {
        binding.toolbarDetail.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupObservers() {
        // Observar estado de carga (opcional)
        viewModel.progressState.observe(viewLifecycleOwner) { isLoading ->
            // Deshabilitar botones durante operación
            binding.btnEliminar.isEnabled = !isLoading
            binding.fabEditar.isEnabled = !isLoading
        }
    }

    private fun loadProductData() {
        // Recibir objeto Serializable
        producto = arguments?.getSerializable("inventory_item") as? InventoryF

        producto?.let {
            mostrarDetalleProducto(it)
        } ?: run {
            // Si no hay producto, volver atrás
            Toast.makeText(requireContext(), "Error: Producto no encontrado", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    private fun setupListeners() {
        // ELIMINAR
        binding.btnEliminar.setOnClickListener {
            producto?.let { item ->
                mostrarDialogoEliminar(item)
            }
        }

        // EDITAR
        binding.fabEditar.setOnClickListener {
            producto?.let { item ->
                navegarAEditar(item)
            }
        }
    }

    private fun mostrarDialogoEliminar(item: InventoryF) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirmar eliminación")
            .setMessage("¿Desea eliminar \"${item.name}\"?")
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarProducto(item)
            }
            .show()
    }

    private fun eliminarProducto(item: InventoryF) {
        // ✅ updateList = false porque el Fragment se va a cerrar
        viewModel.deleteInventory(
            inventory = item,
            updateList = false  // ← Clave: NO actualizar lista aquí
        ) { success, error ->
            // Verificar que el Fragment aún esté activo
            if (!isAdded) return@deleteInventory

            if (success) {
                Toast.makeText(
                    requireContext(),
                    "✅ Producto eliminado",
                    Toast.LENGTH_SHORT
                ).show()
                // Volver al HomeFragment (la lista se actualizará en onResume)
                findNavController().navigateUp()
            } else {
                Toast.makeText(
                    requireContext(),
                    "❌ Error al eliminar: $error",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun navegarAEditar(item: InventoryF) {
        val bundle = Bundle().apply {
            putSerializable("productoEditar", item)
        }
        findNavController().navigate(R.id.editFragment, bundle)
    }

    private fun mostrarDetalleProducto(prod: InventoryF) {
        val formato = NumberFormat.getNumberInstance(Locale("es", "CO")).apply {
            minimumFractionDigits = 2
        }

        val total = prod.price * prod.quantity

        binding.tvNombre.text = prod.name
        binding.tvPrecio.text = "$ ${formato.format(prod.price)}"
        binding.tvCantidad.text = prod.quantity.toString()
        binding.tvTotal.text = "$ ${formato.format(total)}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}