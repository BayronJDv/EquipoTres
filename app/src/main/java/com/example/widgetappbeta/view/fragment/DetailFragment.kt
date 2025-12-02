package com.example.widgetappbeta.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.widgetappbeta.databinding.FragmentDetailBinding
import com.example.widgetappbeta.model.InventoryF
import com.example.widgetappbeta.viewmodel.InventoryViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    private val viewModel: InventoryViewModel by viewModels()

    private var producto: InventoryF? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Botón atrás
        binding.toolbarDetail.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Recibir objeto Serializable
        producto = arguments?.getSerializable("inventory_item") as? InventoryF

        // Mostrar datos
        producto?.let { mostrarDetalleProducto(it) }

        // ELIMINAR
        binding.btnEliminar.setOnClickListener {
            producto?.let { item ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Desea eliminar este producto?")
                    .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                    .setPositiveButton("Sí") { _, _ ->
                        lifecycleScope.launch {
                            viewModel.deleteInventory(item)
                            findNavController().navigateUp()
                        }
                    }
                    .show()
            }
        }

        // EDITAR
        binding.fabEditar.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("productoEditar", producto)
            }
            findNavController().navigate(
                com.example.widgetappbeta.R.id.editFragment,
                bundle
            )
        }
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
}
