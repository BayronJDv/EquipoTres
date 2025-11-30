package com.example.widgetappbeta.view.viewholder

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.navigation.NavController
import com.example.widgetappbeta.R
import androidx.recyclerview.widget.RecyclerView
import com.example.widgetappbeta.databinding.ItemInventoryBinding
import com.example.widgetappbeta.model.InventoryF


class InventoryViewHolder(binding: ItemInventoryBinding, navController: NavController)
    : RecyclerView.ViewHolder(binding.root) {
        val bindingItem = binding
        val navController = navController

        @SuppressLint("SetTextI18n", "DefaultLocale")
        fun setItemInventory(inventory: InventoryF){
            bindingItem.tvName.text = inventory.name
            bindingItem.tvId.text = "id: ${inventory.id}"
            bindingItem.tvPrice.text = priceFormat(String.format("%.2f", inventory.price))


            // navegacion desde cada item al fragment details
            bindingItem.cvInventory.setOnClickListener {
                val bundle = Bundle()
                bundle.putSerializable("inventory", inventory)
                navController.navigate(R.id.action_homeFragment_to_detailFragment, bundle)
            }

        }

    fun priceFormat(price: String): String {
        val partes = price.split(".")
        val entera = partes[0]
        val decimal = partes[1]

        val enteraReversed = entera.reversed()
        val enteraConPuntos = StringBuilder()

        //Mi poderosa funcion de Widget
        for (i in enteraReversed.indices) {
            if (i > 0 && i % 3 == 0) enteraConPuntos.append(".")
            enteraConPuntos.append(enteraReversed[i])
        }

        return "$ ${enteraConPuntos.reverse()},$decimal"
    }


}