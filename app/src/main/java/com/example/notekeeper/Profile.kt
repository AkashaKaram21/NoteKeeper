package com.example.notekeeper

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.example.notekeeper.DataStore.StatsTracker
import com.example.notekeeper.R

class Profile : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val icChangePassword = view.findViewById<ImageButton>(R.id.icChangePassword)
        val icLogOut = view.findViewById<ImageButton>(R.id.icSalir)

        // Botón para cerrar sesión
        icLogOut.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LogOut())
                .addToBackStack(null)
                .commit()
        }

        // Botón para cambiar contraseña
        icChangePassword.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ChangePassword())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // Iniciar conteo de tiempo en pantalla
        StatsTracker.startSession()
    }

    override fun onPause() {
        super.onPause()
        // Guardar tiempo al salir
        StatsTracker.endSession()
    }
}
