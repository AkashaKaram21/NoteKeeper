package com.example.notekeeper.Retrofit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notekeeper.RecyclerView.NotaItem
import com.example.notekeeper.RecyclerView.TypeNote
import kotlinx.coroutines.launch

class NotesViewModel : ViewModel() {

    // 1.- Variable privada per guardar la llista de notes
    private val _notes = MutableLiveData<List<NotaItem>>(emptyList())
    val notes: LiveData<List<NotaItem>> = _notes

    // 2.- Variable privada i pública per la categoria
    private val _categoria = MutableLiveData<TypeNote>(TypeNote.SIMPLE)
    val categoria: LiveData<TypeNote> = _categoria

    // 3.- Contingut de NoteEditor
    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text

    private val _subtitle = MutableLiveData<String>()
    val subtitle: LiveData<String> = _subtitle

    // 4.- Funció que carrega les notes
    fun cargarNotas() {
        viewModelScope.launch {
            try {
                val response = NotesAPI.API().getAllNotes()

                if (response.isSuccessful) {
                    _notes.value = response.body() ?: emptyList()
                } else {
                    println("Error del servidor: ${response.code()}")
                }
            } catch (e: Exception) {
                println("Fallo de conexión: ${e.message}")
            }
        }
    }

    // 5.- Funció que guarda la nota amb try-catch
    fun guardarNota(title: String, subtitle: String, text: String) {
        viewModelScope.launch {
            try {
                val notaRequest = NotaRequestDTO(
                    title = title,
                    subtitle = subtitle,
                    text = text,
                    category = _categoria.value ?: TypeNote.SIMPLE
                )

                val response = NotesAPI.API().createNote(notaRequest)

                if (response.isSuccessful) {
                    Log.d("DEBUG_RETROFIT", "Nota guardada correctamente")
                } else {
                    Log.e("DEBUG_RETROFIT", "Error del servidor: ${response.code()} - ${response.errorBody()?.string()}")                }
            } catch (e: Exception) {
                Log.e("DEBUG_RETROFIT", "Fallo de conexión: ${e.message}")
            }
        }
    }

    fun setCategoria(type: TypeNote) {
        _categoria.value = type
    }

}