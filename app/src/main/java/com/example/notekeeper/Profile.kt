package com.example.notekeeper

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.example.notekeeper.R

class Profile : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        //Tenemos que indentificar el ImageButton por su ID
        val icChangePassword = view.findViewById<ImageButton>(R.id.icChangePassword)

        val icLogOut = view.findViewById<ImageButton>(R.id.icSalir)

        //Cunado lo des clic
        icLogOut.setOnClickListener {

            //Permite passar de un fragment a otro
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LogOut())
                .addToBackStack(null)
                .commit()
        }


        //Cunado lo des clic
        icChangePassword.setOnClickListener {

            //Permite passar de un fragment a otro
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ChangePassword())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}