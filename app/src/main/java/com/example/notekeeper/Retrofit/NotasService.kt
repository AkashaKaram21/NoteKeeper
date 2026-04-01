package com.example.notekeeper.Retrofit

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotasService {
    
    @GET("api/notes")
    suspend fun getAllNotes(): Response<List<NotaResponseDTO>>

    @GET("api/notes/{id}")
    suspend fun getNoteById(@Path("id") id: Long): Response<NotaResponseDTO>

    @POST("api/notes")
    suspend fun createNote(@Body nota: NotaRequestDTO): Response<NotaResponseDTO>

    @PUT("api/notes/{id}")
    suspend fun updateNote(
        @Path("id") id: Long,
        @Body nota: NotaRequestDTO
    ): Response<String>

    @DELETE("api/notes/{id}")
    suspend fun deleteNote(@Path("id") id: Long): Response<String>
}