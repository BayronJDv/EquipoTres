package com.example.widgetappbeta.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.widgetappbeta.model.InventoryF
import com.example.widgetappbeta.R
import com.example.widgetappbeta.databinding.FragmentHomeBinding
import com.example.widgetappbeta.sharedprefs.PrefsManager
import com.example.widgetappbeta.view.adapter.InventoryAdapter
import com.example.widgetappbeta.viewmodel.InventoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InventoryViewModel by viewModels()
    private lateinit var adapter: InventoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupFloatingButton()
        setupRecyclerView()
        setupObservers()

        // ✅ Cargar lista inicial
        viewModel.getListInventory()
    }

    private fun setupToolbar() {
        binding.materialToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    Log.d("HomeFragment", "Logout tocado")
                    handleLogout()
                    true
                }
                else -> false
            }
        }
    }

    private fun handleLogout() {
        PrefsManager.setLoggedIn(false)
        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
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
        // Observar cambios en la lista
        viewModel.listInventory.observe(viewLifecycleOwner) { list ->
            updateUIWithList(list)
        }

        // Observar estado de carga
        viewModel.progressState.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading

            binding.floatingActionButton.isEnabled = !isLoading
        }
    }

    private fun updateUIWithList(list: MutableList<InventoryF>) {
        adapter.updateList(list)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getListInventory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}