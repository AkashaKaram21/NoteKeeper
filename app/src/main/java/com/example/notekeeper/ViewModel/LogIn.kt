package com.notekeeper.ViewModel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.example.notekeeper.R
import com.notekeeper.Profile

class LogIn : Fragment() {
    // Connexió amb el ViewModel
    private val logInViewModel: LogInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Carrega el disseny XML
        return inflater.inflate(R.layout.fragment_log_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Busquem els elements del XML per ID
        val etEmail = view.findViewById<EditText>(R.id.txtEmail)
        val etPassword = view.findViewById<EditText>(R.id.txtPassword)
        val tvEmailLabel = view.findViewById<TextView>(R.id.tvEmailLabel)
        val tvPassLabel = view.findViewById<TextView>(R.id.tvPassLabel)
        val btnLogIn = view.findViewById<Button>(R.id.btnLogIn)
        val btnIniciarSession = view.findViewById<Button>(R.id.btnInciarSession)

        // Quan l'usuari escriu, avisem al ViewModel
        etEmail.addTextChangedListener {
            logInViewModel.onLoginChanged(it.toString(), etPassword.text.toString())
        }
        etPassword.addTextChangedListener {
            logInViewModel.onLoginChanged(etEmail.text.toString(), it.toString())
        }

        // "Observers": Escoltant canvis del ViewModel per actualitzar la UI
        logInViewModel.isLoginEnabled.observe(viewLifecycleOwner) { activo ->
            btnLogIn.isEnabled = activo // Activa/Desactiva botó
        }
        logInViewModel.emailLabelText.observe(viewLifecycleOwner) { nuevoTexto ->
            tvEmailLabel.text = nuevoTexto
        }
        logInViewModel.emailLabelColor.observe(viewLifecycleOwner) { nuevoColor ->
            tvEmailLabel.setTextColor(nuevoColor)
        }
        logInViewModel.passLabelText.observe(viewLifecycleOwner) { nuevoTexto ->
            tvPassLabel.text = nuevoTexto
        }
        logInViewModel.passLabelColor.observe(viewLifecycleOwner) { nuevoColor ->
            tvPassLabel.setTextColor(nuevoColor)
        }

        // Si el login és correcte, anem a la pantalla Profile
        logInViewModel.loginActionEvent.observe(viewLifecycleOwner) { action ->
            when (action) {
                LogInViewModel.LoginAction.LOGIN_OK -> {
                    Toast.makeText(requireContext(), "¡Benvingut!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, Profile())
                        .addToBackStack(null)
                        .commit()
                    logInViewModel.resetEvent()
                }
                LogInViewModel.LoginAction.NONE -> { }
            }
        }

        // Si hi ha un error (contrasenya malament), mostrem un Toast
        logInViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                logInViewModel.resetError()
            }
        }

        // Botó per intentar entrar
        btnLogIn.setOnClickListener {
            logInViewModel.ferLogin(etEmail.text.toString(), etPassword.text.toString())
        }

        // Botó per anar a la pantalla de registre
        btnIniciarSession.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SignIn())
                .addToBackStack(null)
                .commit()
        }
    }
}