package com.example.notekeeper

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.notekeeper.ViewModel.LogIn

class LogOut : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_log_out, container, false)

        val btnInciarSession = view.findViewById<Button>(R.id.btnInciarSession)

        btnInciarSession.setOnClickListener {
            //Permite passar de un fragment a otro
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LogIn())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}