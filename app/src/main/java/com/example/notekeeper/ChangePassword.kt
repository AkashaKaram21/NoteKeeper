package com.example.notekeeper

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.notekeeper.R

class ChangePassword : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_change_password, container, false)

        // Botón para confirmar el cambio de contraseña
        val btnChangePassword = view.findViewById<Button>(R.id.btnCambiar)

        btnChangePassword.setOnClickListener {
            // Volver al perfil después de pulsar el botón
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, Profile())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}
