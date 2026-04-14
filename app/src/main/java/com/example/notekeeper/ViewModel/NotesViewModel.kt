package com.example.notekeeper.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notekeeper.Repository.NotesRepository
import com.example.notekeeper.Retrofit.NotaRequestDTO
import com.example.notekeeper.Retrofit.NotaResponseDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesViewModel : ViewModel() {

    private val repository = NotesRepository()

    val notasLoaded = MutableLiveData<List<NotaResponseDTO>>()
    val operationSuccess = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()
    val navigateBack = MutableLiveData<Boolean>()

    fun loadAllNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getAllNotes()
                if (response.isSuccessful) {
                    notasLoaded.postValue(response.body() ?: emptyList())
                } else {
                    errorMessage.postValue("Error al cargar: ${response.message()}")
                }
            } catch (e: Exception) {
                errorMessage.postValue("Error de connexió: ${e.localizedMessage}")
            }
        }
    }

    fun saveNote(id: Long?, title: String, subtitle: String, text: String, category: String) {
        val dto = NotaRequestDTO(title, subtitle, text, category)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (id != null) {
                    // PUT - Actualizar
                    val response = repository.updateNote(id, dto)
                    if (response.isSuccessful) {
                        operationSuccess.postValue("Nota actualizada")
                        navigateBack.postValue(true)
                    } else {
                        errorMessage.postValue("Error al actualizar (${response.code()})")
                    }
                } else {
                    // POST - Crear
                    val response = repository.createNote(dto)
                    if (response.isSuccessful) {
                        val createdNote = response.body()
                        operationSuccess.postValue("Nota creada (ID: ${createdNote?.id})")
                        navigateBack.postValue(true)
                    } else {
                        errorMessage.postValue("Error al crear (${response.code()})")
                    }
                }
            } catch (e: Exception) {
                errorMessage.postValue("Error de connexió: ${e.localizedMessage}")
            }
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.deleteNote(id)
                if (!response.isSuccessful) {
                    errorMessage.postValue("Error al eliminar (${response.code()})")
                }
            } catch (e: Exception) {
                errorMessage.postValue("Error de connexió: ${e.localizedMessage}")
            }
        }
    }
}