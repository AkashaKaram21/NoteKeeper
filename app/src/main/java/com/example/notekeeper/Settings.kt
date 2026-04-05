package com.example.notekeeper

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.notekeeper.DataStore.StatsTracker
import com.example.notekeeper.ViewModel.LogIn

class Settings : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Botón para ir a la pantalla de inicio de sesión
        val btnSignIn = view.findViewById<Button>(R.id.btnSignIn)

        btnSignIn.setOnClickListener {
            // Cambiar al fragmento de LogIn
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LogIn())
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
