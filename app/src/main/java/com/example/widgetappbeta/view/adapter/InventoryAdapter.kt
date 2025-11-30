package com.example.widgetappbeta.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.widgetappbeta.databinding.ItemInventoryBinding
import com.example.widgetappbeta.model.Inventory
import com.example.widgetappbeta.model.InventoryF
import com.example.widgetappbeta.view.viewholder.InventoryViewHolder

class InventoryAdapter(private val ListInventory: MutableList<InventoryF>, private val navController: NavController)
    : RecyclerView.Adapter<InventoryViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InventoryViewHolder {
        val binding = ItemInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InventoryViewHolder(binding, navController)
    }

    override fun onBindViewHolder(
        holder: InventoryViewHolder,
        position: Int
    ) {
        val inventory = ListInventory[position]
        holder.setItemInventory(inventory)
    }

    override fun getItemCount(): Int {
        return ListInventory.size
    }

}

