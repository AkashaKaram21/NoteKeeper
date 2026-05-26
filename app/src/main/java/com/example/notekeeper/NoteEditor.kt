package com.example.notekeeper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.notekeeper.Retrofit.NotesViewModel

class NoteEditor : Fragment() {

    private val viewModel: NotesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_note_editor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etSubtitle = view.findViewById<EditText>(R.id.etSubtitle)
        val etText = view.findViewById<EditText>(R.id.etText)
        val btnClose = view.findViewById<ImageButton>(R.id.iBtnClose)

        // Si hay nota, rellenamos los campos (editar)
        val nota = viewModel.notaEditar.value
        nota?.let {
            etTitle.setText(it.title)
            etSubtitle.setText(it.subtitle)
            etText.setText(it.text)
        }

        btnClose.setOnClickListener {
            //Pasamos el contenido de la nota
            val title = etTitle.text.toString()
            val subtitle = etSubtitle.text.toString()
            val text = etText.text.toString()

            if (nota != null) {
                // Tiene ID → PUT
                viewModel.editarNota(nota.id, title, subtitle, text)
            } else {
                // Sin ID → POST
                viewModel.guardarNota(title, subtitle, text)
            }

            viewModel.setNotaEditar(null)
            parentFragmentManager.popBackStack()
        }

        viewModel.incrementarVisita("add")
    }
}