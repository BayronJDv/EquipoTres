package com.example.widgetappbeta.view.viewholder

import android.os.Bundle
import androidx.navigation.NavController
import com.example.widgetappbeta.R
import androidx.recyclerview.widget.RecyclerView
import com.example.widgetappbeta.databinding.ItemInventoryBinding
import com.example.widgetappbeta.model.Inventory




class InventoryViewHolder(binding: ItemInventoryBinding, navController: NavController)
    : RecyclerView.ViewHolder(binding.root) {
        val bindingItem = binding
        val navController = navController

        fun setItemInventory(inventory: Inventory){
            bindingItem.tvName.text = inventory.name
            bindingItem.tvId.text = "id: ${inventory.id}"
            bindingItem.tvPrice.text = "$${inventory.price}"


            // navegacion desde cada item al fragment details
            bindingItem.cvInventory.setOnClickListener {
                val bundle = Bundle()
                bundle.putSerializable("inventory", inventory)
                navController.navigate(R.id.action_homeFragment_to_detailFragment, bundle)
            }

        }


}