package com.example.notekeeper.Retrofit

import retrofit2.Response
import retrofit2.http.*

interface NotesService {
    @POST("notes")
    suspend fun createNote(@Body notaRequestDTO: NotaRequestDTO): Response<NotaResponseDTO>

    @GET("notes")
    suspend fun getAllNotes(): Response<List<NotaResponseDTO>>

    @GET("notes/{id}")
    suspend fun getNoteById(@Path("id") id: Long): Response<NotaResponseDTO>

    @PUT("notes/{id}")
    suspend fun updateNote(
        @Path("id") id: Long,
        @Body nota: NotaRequestDTO
    ): Response<NotaResponseDTO>

    @DELETE("notes/{id}")
    suspend fun deleteNote(@Path("id") id: Long): Response<Unit>
}