package com.example.notekeeper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notekeeper.RecyclerView.*
import com.example.notekeeper.Retrofit.NotesViewModel

class Bin : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter


    private val viewModel: NotesViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_bin, container, false)

        val btnBack = view.findViewById<ImageButton>(R.id.binIbBack)

        // Volver atrás al Home
        btnBack.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, Home())
                .addToBackStack(null)
                .commit()
        }

        // 1. Obtenir referència al RecyclerView del layout
        recyclerView = view.findViewById(R.id.binNotes)

        // 2. Configurar LayoutManager (com es col·loquen les files)
        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerViewAdapter = RecyclerViewAdapter(
            items = emptyList(),
            isBin = true,

            // No se puede editar una nota que está en la papelera
            onItemClick = { item ->
            },

            // Recuperar nota desde la papelera
            onRecoverClick = { item ->
            },

            // Eliminar nota permanentemente
            onDeleteClick = { item ->
            }
        )

        // 5. Assignar l'Adapter al RecyclerView
        recyclerView.adapter = recyclerViewAdapter

        // 6.- Avisem a l'adapter que rebra una llista
        viewModel.notes.observe(viewLifecycleOwner) { listaNotas ->
            recyclerViewAdapter.updateList(listaNotas)
        }

        //7.- Carguem la llista
        viewModel.cargarNotas()

        return view
    }

}