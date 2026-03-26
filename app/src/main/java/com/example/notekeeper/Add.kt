package com.example.notekeeper

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.notekeeper.NoteEditor
import com.notekeeper.RecyclerView.TypeNote

/*
* com.example.notekeeper.Add té botons que ens permet crear diferents tipus de notes
 */
class Add : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)
        //Recuperem el bótons
        val simpleNote = view.findViewById<Button>(R.id.btnSimpleNote)
        val reminderNote = view.findViewById<Button>(R.id.btnRemiderNote)
        val sharedNote = view.findViewById<Button>(R.id.btnSharedNote)

        //Aquest funció s'encarga de passar el tipos de categoria de la nota
        fun abrirEditor(categoriaElegida: TypeNote) {
            val noteEditor = NoteEditor()
            val bundle = Bundle()
            bundle.putString("CATEGORIA", categoriaElegida.name)
            noteEditor.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, noteEditor)
                .addToBackStack(null)
                .commit()
        }

        //Obrir la nota segons la categoria
        simpleNote.setOnClickListener {
            abrirEditor(TypeNote.Simple)
        }

        reminderNote.setOnClickListener {
            abrirEditor(TypeNote.Reminder)
        }

        sharedNote.setOnClickListener {
            abrirEditor(TypeNote.Shared)
        }

        return view
    }
}