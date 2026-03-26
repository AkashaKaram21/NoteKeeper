package com.notekeeper

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.example.notekeeper.R
import com.notekeeper.Retrofit.NoteAPI
import com.notekeeper.Retrofit.NoteRequest
import com.notekeeper.RecyclerView.NotaItem
import com.notekeeper.RecyclerView.SelectedColor
import com.notekeeper.RecyclerView.TypeNote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteEditor : Fragment() {

    private var colorSeleccionado: SelectedColor = SelectedColor.White

    // Variable per emmagatzemar l'ID si estem editant
    private var noteIdToEdit: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_note_editor, container, false)

        // Recuperem els elements de la UI
        val etTitle    = view.findViewById<EditText>(R.id.etTitle)
        val etSubtitle = view.findViewById<EditText>(R.id.etSubtitle)
        val etText     = view.findViewById<EditText>(R.id.etText)
        val btnClose   = view.findViewById<ImageButton>(R.id.iBtnClose)

        // comprovem si tenim arguments per omplir els camps (Mode Edició)
        arguments?.let { bundle ->
            if (bundle.containsKey("NOTE_ID")) {
                noteIdToEdit = bundle.getLong("NOTE_ID")
                etTitle.setText(bundle.getString("NOTE_TITLE"))
                etSubtitle.setText(bundle.getString("NOTE_SUBTITLE"))
                etText.setText(bundle.getString("NOTE_TEXT"))
            }
        }

        val categoriaStr = arguments?.getString("CATEGORIA") ?: "Simple"

        btnClose.setOnClickListener {
            // Decidim si crear o actualitzar segons si tenim ID
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val response = if (noteIdToEdit != null) {
                        // Creem l'objecte NotaItem per a l'actualització (@PUT)
                        val notaActualitzada = NotaItem(
                            id = noteIdToEdit,
                            title = etTitle.text.toString(),
                            subtitle = etSubtitle.text.toString(),
                            text = etText.text.toString(),
                            category = TypeNote.valueOf(categoriaStr)
                        )
                        NoteAPI.API().updateNote(noteIdToEdit!!, notaActualitzada)
                    } else {
                        // Creem l'objecte NoteRequest per a nota nova (@POST)
                        val nuevaNota = NoteRequest(
                            title = etTitle.text.toString(),
                            subtitle = etSubtitle.text.toString(),
                            text = etText.text.toString(),
                            category = TypeNote.valueOf(categoriaStr),
                            color = colorSeleccionado
                        )
                        NoteAPI.API().createNote(nuevaNota)
                    }

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val msg = if (noteIdToEdit != null) "Nota actualitzada" else "Nota creada"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        } else {
                            Log.e("API", "Error: ${response.code()}")
                            Toast.makeText(context, "Error al desar la nota", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("API_ERROR", "Error: ${e.message}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error de connexió amb el servidor", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return view
    }
}