package com.example.notekeeper

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.notekeeper.DataStore.StatsTracker
import com.example.notekeeper.RecyclerView.SelectedColor
import com.example.notekeeper.Retrofit.NotaRequestDTO
import com.example.notekeeper.Retrofit.NotesAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteEditor : Fragment() {

    private var colorSeleccionado: SelectedColor = SelectedColor.White
    private var noteIdToEdit: Long? = null
    private var isNewNote: Boolean = false
    private var categoriaSeleccionada: String = "Simple"  // Categoría por defecto

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

            lifecycleScope.launch(Dispatchers.IO) {
                try {

                    val notaDTO = NotaRequestDTO(
                        title,
                        subtitle,
                        text,
                        categoriaSeleccionada
                    )

                    if (!isNewNote && noteIdToEdit != null) {

                        // Actualizar nota existente
                        val response = NotesAPI.API().updateNote(noteIdToEdit!!, notaDTO)

                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                StatsTracker.trackUpdate()
                                Toast.makeText(context, "Nota actualizada", Toast.LENGTH_SHORT).show()
                                requireActivity().onBackPressedDispatcher.onBackPressed()
                            } else {
                                Toast.makeText(context, "Error al actualizar (${response.code()})", Toast.LENGTH_SHORT).show()
                            }
                        }

                    } else {

                        // Crear nueva nota
                        val response = NotesAPI.API().createNote(notaDTO)

                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {

                                val createdNote = response.body()

                                if (createdNote != null) {
                                    // Guardar ID devuelto por la API
                                    noteIdToEdit = createdNote.id
                                    StatsTracker.trackCreate()
                                    Toast.makeText(context, "Nota creada (ID: ${createdNote.id})", Toast.LENGTH_SHORT).show()
                                }

                                requireActivity().onBackPressedDispatcher.onBackPressed()

                            } else {
                                Toast.makeText(context, "Error al crear (${response.code()})", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error de conexión: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return view
    }
}
