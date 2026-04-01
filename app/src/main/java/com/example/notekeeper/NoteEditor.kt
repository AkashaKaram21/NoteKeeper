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
import com.example.notekeeper.DataStore.DadesStats
import com.example.notekeeper.RecyclerView.NotaItem
import com.example.notekeeper.RecyclerView.SelectedColor
import com.example.notekeeper.RecyclerView.TypeNote
import com.example.notekeeper.Retrofit.NoteAPI
import com.example.notekeeper.Retrofit.NotaRequestDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteEditor : Fragment() {

    private var colorSeleccionado: SelectedColor = SelectedColor.White
    private var noteIdToEdit: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_note_editor, container, false)

        val etTitle    = view.findViewById<EditText>(R.id.etTitle)
        val etSubtitle = view.findViewById<EditText>(R.id.etSubtitle)
        val etText     = view.findViewById<EditText>(R.id.etText)
        val btnClose   = view.findViewById<ImageButton>(R.id.iBtnClose)

        // Cargar datos si estamos editando
        arguments?.let { bundle ->
            if (bundle.containsKey("NOTE_ID")) {
                noteIdToEdit = bundle.getLong("NOTE_ID")
                etTitle.setText(bundle.getString("NOTE_TITLE"))
                etSubtitle.setText(bundle.getString("NOTE_SUBTITLE"))
                etText.setText(bundle.getString("NOTE_TEXT"))
            }
        }

        val categoriaStr = arguments?.getString("CATEGORIA") ?: "Simple"

        // Guardar o actualizar nota al cerrar
        btnClose.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val response = if (noteIdToEdit != null) {
                        // CORREGIDO: Usar NotaRequestDTO para la actualización
                        val notaActualitzada = NotaRequestDTO(
                            title = etTitle.text.toString(),
                            subtitle = etSubtitle.text.toString(),
                            text = etText.text.toString()
                        )
                        Log.d("EDIT", "Editant nota amb ID: $noteIdToEdit")
                        NoteAPI.API().updateNote(noteIdToEdit!!, notaActualitzada)
                    } else {
                        // CORREGIDO: Usar NotaRequestDTO para la creación
                        val nuevaNota = NotaRequestDTO(
                            title = etTitle.text.toString(),
                            subtitle = etSubtitle.text.toString(),
                            text = etText.text.toString()
                        )
                        Log.d("CREATE", "Creant nova nota")
                        NoteAPI.API().createNote(nuevaNota)
                    }

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val msg = if (noteIdToEdit != null) "Nota actualitzada" else "Nota creada"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            Log.d("SUCCESS", msg)

                            // Esperamos 1 segundo para que la UI se actualice
                            delay(1000)
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        } else {
                            Log.e("API", "Error: ${response.code()} - ${response.message()}")
                            Toast.makeText(context, "Error al desar la nota (${response.code()})", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.e("API_ERROR", "Error: ${e.message}")
                        Toast.makeText(context, "Error de connexió: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return view
    }
}