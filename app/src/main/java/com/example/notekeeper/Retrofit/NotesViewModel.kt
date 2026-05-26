package com.example.notekeeper.Retrofit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notekeeper.RecyclerView.NotaItem
import com.example.notekeeper.RecyclerView.TypeNote
import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesViewModel : ViewModel() {

    // Connexió amb Firestore
    private val db = Firebase.firestore

    // Llista de notes
    private val _notes = MutableLiveData<List<NotaItem>>()
    val notes: LiveData<List<NotaItem>> get() = _notes

    // Categoria seleccionada
    private val _categoria = MutableLiveData<TypeNote>()
    val categoria: LiveData<TypeNote> get() = _categoria

    // Camps del editor de notes
    private val _title = MutableLiveData<String>()
    val title: LiveData<String> get() = _title

    private val _subtitle = MutableLiveData<String>()
    val subtitle: LiveData<String> get() = _subtitle

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> get() = _text

    // Nota que s'està editant (null si és nova)
    private val _notaEditar = MutableLiveData<NotaItem?>()
    val notaEditar: LiveData<NotaItem?> get() = _notaEditar

    fun setNotaEditar(nota: NotaItem?) {
        _notaEditar.value = nota
    }

    // Carrega totes les notes desde la API
    fun cargarNotas() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = NotesAPI.API().getAllNotes()
                if (response.isSuccessful) {
                    _notes.postValue(response.body())
                } else {
                    Log.e("NotesViewModel", "Error del servidor: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Fallo de conexión: ${e.message}")
            }
        }
    }

    // Guarda una nota nova a la API i incrementa el comptador de Firestore
    fun guardarNota(title: String, subtitle: String, text: String) {
        val notaRequest = NotaRequestDTO(
            title = title,
            subtitle = subtitle,
            text = text,
            category = _categoria.value ?: TypeNote.SIMPLE
        )
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = NotesAPI.API().createNote(notaRequest)
                if (response.isSuccessful) {
                    Log.d("NotesViewModel", "Nota guardada correctament")
                    incrementarStat("creades")
                    cargarNotas()
                } else {
                    Log.e("NotesViewModel", "Error del servidor: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Fallo de conexión: ${e.message}")
            }
        }
    }

    // Edita una nota existent a la API i incrementa el comptador de Firestore
    fun editarNota(id: Long, title: String, subtitle: String, text: String) {
        val notaRequest = NotaRequestDTO(
            title = title,
            subtitle = subtitle,
            text = text,
            category = _categoria.value ?: TypeNote.SIMPLE
        )
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = NotesAPI.API().updateNote(id, notaRequest)
                if (response.isSuccessful) {
                    Log.d("NotesViewModel", "Nota editada correctament")
                    incrementarStat("editades")
                    cargarNotas()
                } else {
                    Log.e("NotesViewModel", "Error del servidor: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Fallo de conexión: ${e.message}")
            }
        }
    }

    // Elimina una nota de la API i incrementa el comptador de Firestore
    fun eliminarNota(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = NotesAPI.API().deleteNote(id)
                if (response.isSuccessful) {
                    Log.d("NotesViewModel", "Nota eliminada correctament")
                    incrementarStat("eliminades")
                    cargarNotas()
                } else {
                    Log.e("NotesViewModel", "Error del servidor: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Fallo de conexión: ${e.message}")
            }
        }
    }

    fun setCategoria(type: TypeNote) {
        _categoria.value = type
    }

    // Incrementa el comptador d'operacions CRUD a Firestore
    private fun incrementarStat(camp: String) {
        val ref = db.collection("stats").document("crud")

        ref.get().addOnSuccessListener { doc ->

            // Si el document existeix, agafem el valor actual i sumem 1
            if (doc.exists()) {
                val actual = doc.getLong(camp)
                ref.set(mapOf(camp to actual!! + 1), SetOptions.merge())

                // Si no existeix, el creem amb tots els camps a 0
            } else {
                ref.set(mapOf(
                    "creades" to 0,
                    "editades" to 0,
                    "eliminades" to 0
                ))
            }
        }.addOnFailureListener { e ->
            Log.e("Firestore", "Error: ${e.message}")
        }
    }

    // Incrementa el comptador de visites d'un fragment a Firestore
    fun incrementarVisita(fragment: String) {
        val ref = db.collection("stats").document("visites")

        ref.get().addOnSuccessListener { doc ->

            // Si el document existeix, agafem el valor actual i sumem 1
            if (doc.exists()) {
                val actual = doc.getLong(fragment)
                ref.update(fragment, actual!! + 1)

                // Si no existeix, el creem amb tots els fragments a 0
            } else {
                ref.set(mapOf(
                    "home" to 0,
                    "add" to 0,
                    "settings" to 0,
                    "profile" to 0
                ))
            }
        }.addOnFailureListener { e ->
            Log.e("Firestore", "Error: ${e.message}")
        }
    }
}