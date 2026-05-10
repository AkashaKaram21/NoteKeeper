package com.example.notekeeper

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.content.Intent
import androidx.fragment.app.viewModels
import com.example.notekeeper.RecyclerView.TypeNote
import com.example.notekeeper.Retrofit.NotesViewModel

class Add : Fragment() {

    private val viewModel: NotesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)


        // Botón Simple
        val btnSimpleNote = view.findViewById<Button>(R.id.btnSimpleNote)
        btnSimpleNote.setOnClickListener {
            viewModel.setCategoria(TypeNote.SIMPLE)
            cambiarFragment()

        }

        // Botón Compartida
        val btnSharedNote = view.findViewById<Button>(R.id.btnSharedNote)
        btnSharedNote.setOnClickListener {
            viewModel.setCategoria(TypeNote.SHARED)
            cambiarFragment()
        }

        // Botón Recordatorio
        val btnReminderNote = view.findViewById<Button>(R.id.btnRemiderNote)
        btnReminderNote.setOnClickListener {
            viewModel.setCategoria(TypeNote.REMINDER)
            cambiarFragment()
        }

        return view
    }

    public fun cambiarFragment(){
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, NoteEditor())
            .addToBackStack(null)
            .commit()
    }
}