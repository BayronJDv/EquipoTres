package com.example.widgetappbeta.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.widgetappbeta.R
import com.example.widgetappbeta.databinding.FragmentHomeBinding
import com.example.widgetappbeta.sharedprefs.PrefsManager
import com.example.widgetappbeta.view.adapter.InventoryAdapter
import com.example.widgetappbeta.viewmodel.InventoryViewModel
import com.example.widgetappbeta.widget.InventoryWidgetProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val inventoryViewModel: InventoryViewModel by viewModels()
    private lateinit var adapter: InventoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupFloatingButton()
        setupRecyclerView()
        setupObservers()

        inventoryViewModel.getListInventory() // Cargar Firestore
    }

    private fun setupToolbar() {
        binding.materialToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    Log.d("HomeFragment", "Logout tocado")

                    PrefsManager.setLoggedIn(false)

                    requireContext().applicationContext?.let { context ->
                        InventoryWidgetProvider.sendLogoutResetBroadcast(context)
                    }

                    findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupFloatingButton() {
        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addFragment)
        }
    }

    private fun setupRecyclerView() {
        adapter = InventoryAdapter(mutableListOf(), findNavController())

        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HomeFragment.adapter
        }
    }

    private fun setupObservers() {
        inventoryViewModel.listInventory.observe(viewLifecycleOwner) { list ->
            adapter.updateList(list)  // método nuevo en tu adapter
        }

        inventoryViewModel.progressState.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.isVisible = loading
        }
    }

    override fun onResume() {
        super.onResume()
        inventoryViewModel.getListInventory() // refrescar datos al volver
    }
}
