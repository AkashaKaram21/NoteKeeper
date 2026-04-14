package com.example.notekeeper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.notekeeper.RecyclerView.SelectedColor
import com.example.notekeeper.ViewModel.NotesViewModel

class NoteEditor : Fragment() {

    private val viewModel: NotesViewModel by viewModels()

    private var colorSeleccionado: SelectedColor = SelectedColor.White
    private var noteIdToEdit: Long? = null
    private var isNewNote: Boolean = false
    private var categoriaSeleccionada: String = "Simple"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_note_editor, container, false)

        val etTitle    = view.findViewById<EditText>(R.id.etTitle)
        val etSubtitle = view.findViewById<EditText>(R.id.etSubtitle)
        val etText     = view.findViewById<EditText>(R.id.etText)
        val btnClose   = view.findViewById<ImageButton>(R.id.iBtnClose)

        // Comprobar si estamos editando una nota o creando una nueva
        arguments?.let { bundle ->
            if (bundle.containsKey("NOTE_ID")) {
                // Modo edición
                noteIdToEdit = bundle.getLong("NOTE_ID")
                etTitle.setText(bundle.getString("NOTE_TITLE"))
                etSubtitle.setText(bundle.getString("NOTE_SUBTITLE"))
                etText.setText(bundle.getString("NOTE_TEXT"))
                categoriaSeleccionada = bundle.getString("CATEGORIA", "Simple")
                isNewNote = false
            } else {
                // Modo creación
                categoriaSeleccionada = bundle?.getString("CATEGORIA", "Simple") ?: "Simple"
                isNewNote = true
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
        }

        viewModel.navigateBack.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate) {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        btnClose.setOnClickListener {

            val title = etTitle.text.toString().trim()
            val subtitle = etSubtitle.text.toString().trim()
            val text = etText.text.toString().trim()

            // Evitar crear notas completamente vacías
            if (isNewNote && title.isEmpty() && text.isEmpty()) {
                Toast.makeText(context, "Nota vacía descartada", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
                return@setOnClickListener
            }

            // Llamar al ViewModel
            viewModel.saveNote(noteIdToEdit, title, subtitle, text, categoriaSeleccionada)
        }

        return view
    }
}