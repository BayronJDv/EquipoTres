package com.example.widgetappbeta.view.viewholder

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.widgetappbeta.R
import com.example.widgetappbeta.databinding.ItemInventoryBinding
import com.example.widgetappbeta.model.InventoryF

class InventoryViewHolder(
    private val binding: ItemInventoryBinding,
    private val navController: NavController
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun setItemInventory(item: InventoryF) {

        // Asignar datos
        binding.tvName.text = item.name
        binding.tvId.text = "Id: ${item.id}"
        binding.tvPrice.text = formatPrice(item.price)

        // Navegar al detalle enviando el objeto directamente (Serializable)
        binding.cvInventory.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("inventory_item", item)
            }
            navController.navigate(
                R.id.action_homeFragment_to_detailFragment,
                bundle
            )
        }
    }

    /** Formato de precio tipo moneda */
    private fun formatPrice(price: Double): String {
        val entero = price.toInt()
        val decimal = ((price - entero) * 100).toInt()

        val enteroConPuntos = entero.toString()
            .reversed()
            .chunked(3)
            .joinToString(".")
            .reversed()

        val decimalStr = decimal.toString().padStart(2, '0')

        return "$ $enteroConPuntos,$decimalStr"
    }
}
