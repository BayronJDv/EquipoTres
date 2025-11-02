package com.example.widgetappbeta.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.widgetappbeta.R
import com.example.widgetappbeta.databinding.FragmentHomeBinding


class HomeFragment : Fragment(){
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //configurar el toolbar
        setupToolbar()
        // agragando evento al boton =
        setupFloatingButton()

    }

    private fun setupToolbar() {
        // Escuchar clicks del menú del toolbar
        binding.materialToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    // aqui se debe implementar la logica de logout con viewmodel
                    Log.d("HomeFragment", "log out tocado  ")
                    true // indica que el evento fue manejado
                }
                else -> false
            }
        }
    }
    private fun setupFloatingButton(){
        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addFragment)
        }
    }

}