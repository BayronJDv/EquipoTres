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
import com.example.widgetappbeta.model.Inventory
import com.example.widgetappbeta.viewmodel.InventoryViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    private val viewModel: InventoryViewModel by viewModels()

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

        // Obtener producto recibido desde HomeFragment
        val producto = arguments?.getSerializable("inventory") as? Inventory

        // Mostrar los datos del producto
        producto?.let { mostrarDetalleProducto(it) }

        // Botón eliminar con diálogo
        binding.btnEliminar.setOnClickListener {
            producto?.let {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Desea eliminar este producto?")
                    .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                    .setPositiveButton("Si") { _, _ ->
                        lifecycleScope.launch {
                            viewModel.deleteInventory(it)
                            findNavController().navigateUp()
                        }
                    }
                    .show()
            }
        }

        // Botón flotante para editar
        binding.fabEditar.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("productoEditar", producto)
            findNavController().navigate(
                com.example.widgetappbeta.R.id.editFragment,
                bundle
            )
        }
    }

    private fun mostrarDetalleProducto(producto: Inventory) {
        val formato = NumberFormat.getNumberInstance(Locale("es", "CO"))
        formato.minimumFractionDigits = 2

        val total = producto.price * producto.quantity

        binding.tvNombre.text = "${producto.name}"
        binding.tvPrecio.text = "$ ${formato.format(producto.price)}"
        binding.tvCantidad.text = "${producto.quantity}"
        binding.tvTotal.text = "$ ${formato.format(total)}"
    }
}
