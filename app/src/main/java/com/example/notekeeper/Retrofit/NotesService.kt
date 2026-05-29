package com.example.notekeeper.Retrofit

import com.example.notekeeper.RecyclerView.NotaItem
import retrofit2.Response
import retrofit2.http.*

interface NotesService {
    @POST("notes")
    suspend fun createNote(@Body notaRequestDTO: NotaRequestDTO): Response<NotaItem>

    @GET("notes")
    suspend fun getAllNotes(): Response<List<NotaItem>>

    @GET("notes/{id}")
    suspend fun getNoteById(@Path("id") id: Long): Response<NotaItem>

    @PUT("notes/{id}")
    suspend fun updateNote(
        @Path("id") id: Long,
        @Body nota: NotaRequestDTO
    ): Response<String>

    @DELETE("notes/{id}")
    suspend fun deleteNote(@Path("id") id: Long): Response<Unit>
}