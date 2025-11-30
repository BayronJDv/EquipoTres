package com.example.widgetappbeta.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.widgetappbeta.R
import com.example.widgetappbeta.databinding.FragmentLoginBinding
import com.example.widgetappbeta.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private var isPasswordVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Si hay sesión activa, ir a Home
        if (viewModel.verififySession()) {
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            return
        }

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // Configurar filtros de entrada
        setupInputFilters()

        // Configurar visibilidad de contraseña
        setupPasswordVisibility()

        // Configurar validación de campos en tiempo real
        setupFieldValidation()

        // Botón Login
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            viewModel.login(email, password)
        }

        // Botón Registrarse
        binding.tvRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            viewModel.register(email, password)
        }
    }

    private fun setupInputFilters() {
        // Limitar Email a 40 caracteres
        binding.etEmail.filters = arrayOf(InputFilter.LengthFilter(40))

        // Limitar Password a 10 dígitos
        binding.etPassword.filters = arrayOf(InputFilter.LengthFilter(10))
    }

    private fun setupPasswordVisibility() {
        binding.ivPasswordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                // Mostrar contraseña
                binding.etPassword.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
                binding.ivPasswordToggle.setImageResource(R.drawable.visibility_on)
            } else {
                // Ocultar contraseña
                binding.etPassword.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
                binding.ivPasswordToggle.setImageResource(R.drawable.visibility_off)
            }

            // Mover el cursor al final del texto
            binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
        }
    }

    private fun setupFieldValidation() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateFields()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etEmail.addTextChangedListener(textWatcher)
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePassword(s.toString())
                validateFields()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validatePassword(password: String) {
        if (password.isNotEmpty() && password.length < 6) {
            binding.tvPasswordError.visibility = View.VISIBLE
            binding.tilPassword.boxStrokeColor = ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
        } else {
            binding.tvPasswordError.visibility = View.GONE
            binding.tilPassword.boxStrokeColor = ContextCompat.getColor(requireContext(), android.R.color.white)
        }
    }

    private fun validateFields() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        val isValid = email.isNotEmpty() && password.length >= 6

        // Habilitar/deshabilitar botones
        binding.btnLogin.isEnabled = isValid
        binding.tvRegister.isEnabled = isValid

        // Cambiar estilos
        binding.btnLogin.alpha = if (isValid) 1f else 0.5f

        if (isValid) {
            binding.tvRegister.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            binding.tvRegister.setTypeface(null, android.graphics.Typeface.BOLD)
        } else {
            binding.tvRegister.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_gray))
            binding.tvRegister.setTypeface(null, android.graphics.Typeface.NORMAL)
        }
    }

    private fun observeViewModel() {
        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

            state.error?.let { error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }

            if (state.isSuccess) {
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }
        }
    }
}
