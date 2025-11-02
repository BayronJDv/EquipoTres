package com.example.widgetappbeta.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.widgetappbeta.R
import com.example.widgetappbeta.databinding.FragmentAddBinding

//import solo para probar la base de datos
import com.example.widgetappbeta.model.Inventory
import com.example.widgetappbeta.repository.InventoryRepository
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


class AddFragment : Fragment(){
    private lateinit var binding: FragmentAddBinding

    //SOLO PARA PROBAR BASE DE DATOS BORRAR
    private val repository: InventoryRepository by lazy {
        InventoryRepository(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controladorBoton()
    }






    //ESTO SE DEBE BORRAR
    // esta funcion esta hecha para crear un producto generico y agregarlo a la base de datos
    // solo pra probar el funcionamiento de la recycler view
    fun controladorBoton(){
        binding.button.setOnClickListener {
            val numero = (1..100).random()
            val item = Inventory(
                name = "zapatos",
                price = 1000.0,
                id = numero,
                quantity = 100)
            lifecycleScope.launch {
                try {
                    repository.saveInventory(item)
                    Toast.makeText(requireContext(), "Artículo insertado correctamente", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error al insertar: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

        }
    }

}