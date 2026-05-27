package com.example.notekeeper

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import com.example.notekeeper.Retrofit.NotesViewModel
import com.example.notekeeper.ViewModel.LogIn
import kotlin.getValue

class LogOut : Fragment() {

    private val viewModel: NotesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_log_out, container, false)

        // Botón para volver a iniciar sesión
        val btnInciarSession = view.findViewById<Button>(R.id.btnInciarSession)

        btnInciarSession.setOnClickListener {
            // Cambiar al fragmento de inicio de sesión
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LogIn())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        viewModel.incrementarVisita("profile")
    }

}
