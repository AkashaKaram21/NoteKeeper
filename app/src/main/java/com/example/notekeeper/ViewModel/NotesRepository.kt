package com.example.notekeeper.Repository

import com.example.notekeeper.Retrofit.NotaRequestDTO
import com.example.notekeeper.Retrofit.NotesAPI

class NotesRepository {
    private val api = NotesAPI.API()

    suspend fun getAllNotes() = api.getAllNotes()
    suspend fun createNote(nota: NotaRequestDTO) = api.createNote(nota)
    suspend fun updateNote(id: Long, nota: NotaRequestDTO) = api.updateNote(id, nota)
    suspend fun deleteNote(id: Long) = api.deleteNote(id)
}