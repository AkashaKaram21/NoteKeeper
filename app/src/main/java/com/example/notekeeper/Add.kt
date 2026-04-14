package com.example.notekeeper

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.notekeeper.RecyclerView.TypeNote

class Add : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        // Passem els següents arguments a la funció
        obrirPerCategoria(view, R.id.btnSimpleNote, TypeNote.Simple)
        obrirPerCategoria(view, R.id.btnRemiderNote, TypeNote.Reminder)
        obrirPerCategoria(view, R.id.btnSharedNote, TypeNote.Shared)

        return view
    }

    /*
    * Aquesta funció passa la categoria al botón per obrir el NoteEditor d'aquesta manera segons la categoria
    * en el NoteEditor es mostra una cosa o altra.
     */
    private fun obrirPerCategoria(view: View, botonId: Int, tipoNota: TypeNote) {
        view.findViewById<Button>(botonId).setOnClickListener {

            //Passem la categoria
            val noteEditor = NoteEditor().apply {
                arguments = Bundle().apply {
                    putString("CATEGORIA", tipoNota.name)
                }
            }

            //Cambiem de pantalla
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, noteEditor)
                .addToBackStack(null)
                .commit()
        }
    }
}