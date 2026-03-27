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

class SignIn : Fragment() {

    private val signInViewModel: SignInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Enllacem les vistes amb els IDs del XML
        val etEmail = view.findViewById<EditText>(R.id.email)
        val etPassword = view.findViewById<EditText>(R.id.password)
        val tvEmailLabel = view.findViewById<TextView>(R.id.tvEmailLabel)
        val tvPassLabel = view.findViewById<TextView>(R.id.tvPassLabel)
        val btnSignIn = view.findViewById<Button>(R.id.btnInciarSession)

        // Listeners de text per validar en temps real
        etEmail.addTextChangedListener {
            signInViewModel.onSignInChanged(it.toString(), etPassword.text.toString())
        }
        etPassword.addTextChangedListener {
            signInViewModel.onSignInChanged(etEmail.text.toString(), it.toString())
        }

        // Observers per actualitzar colors, textos i el botó
        signInViewModel.isSignInEnabled.observe(viewLifecycleOwner) { activo ->
            btnSignIn.isEnabled = activo
        }
        signInViewModel.emailLabelText.observe(viewLifecycleOwner) { nuevoTexto ->
            tvEmailLabel.text = nuevoTexto
        }
        signInViewModel.emailLabelColor.observe(viewLifecycleOwner) { nuevoColor ->
            tvEmailLabel.setTextColor(nuevoColor)
        }
        signInViewModel.passLabelText.observe(viewLifecycleOwner) { nuevoTexto ->
            tvPassLabel.text = nuevoTexto
        }
        signInViewModel.passLabelColor.observe(viewLifecycleOwner) { nuevoColor ->
            tvPassLabel.setTextColor(nuevoColor)
        }

        // Quan el registre és correcte (REGISTRAT), anem a Profile
        signInViewModel.signInActionEvent.observe(viewLifecycleOwner) { action ->
            when (action) {
                SignInViewModel.SignInAction.REGISTRAT -> {
                    Toast.makeText(requireContext(), "¡Compte creat!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, Profile())
                        .addToBackStack(null)
                        .commit()
                    signInViewModel.resetEvent()
                }
                SignInViewModel.SignInAction.NONE -> { }
            }
        }

        // Si hi ha algun error en el registre
        signInViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                signInViewModel.resetError()
            }
        }

        // Clic al botó per registrar l'usuari
        btnSignIn.setOnClickListener {
            signInViewModel.registrarUsuari(
                etEmail.text.toString(),
                etPassword.text.toString()
            )
        }
    }
}