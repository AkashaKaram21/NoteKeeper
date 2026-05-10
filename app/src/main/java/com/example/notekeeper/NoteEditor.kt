package com.example.notekeeper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.notekeeper.Retrofit.NotesViewModel

class NoteEditor : Fragment() {

    private val viewModel: NotesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_note_editor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener referencias
        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etSubtitle = view.findViewById<EditText>(R.id.etSubtitle)
        val etText = view.findViewById<EditText>(R.id.etText)
        val btnClose = view.findViewById<ImageButton>(R.id.iBtnClose)

        // Click en botón cerrar - Guardar y cerrar
        btnClose.setOnClickListener {
            viewModel.guardarNota(
                etTitle.text.toString(),
                etSubtitle.text.toString(),
                etText.text.toString()
            )
            parentFragmentManager.popBackStack()
        }
    }
}