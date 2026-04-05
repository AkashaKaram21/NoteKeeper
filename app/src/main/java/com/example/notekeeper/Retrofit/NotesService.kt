package com.example.notekeeper.Retrofit

import com.example.notekeeper.RecyclerView.NotaItem
import retrofit2.Response
import retrofit2.http.*

/*
 * Interface que defineix els endpoints de l'API REST per gestionar notes
 * Totes les funcions són suspend perquè es cridaran desde corrutines
 */
interface NotesService {

    // POST /api/notes - Crear una nota nova
    @POST("api/notes")
    suspend fun createNote(@Body notaRequestDTO: NotaRequestDTO): Response<NotaResponseDTO>

    // GET /api/notes - Obtenir totes les notes
    @GET("api/notes")
    suspend fun getAllNotes(): Response<List<NotaResponseDTO>>

    // GET /api/notes/{id} - Obtenir una nota per ID
    @GET("api/notes/{id}")
    suspend fun getNoteById(@Path("id") id: Long): Response<NotaResponseDTO>

    // PUT /api/notes/{id} - Actualitzar una nota
    // CORRECCIÓ: Usar NotaRequestDTO en lloc de NotaItem
    @PUT("api/notes/{id}")
    suspend fun updateNote(
        @Path("id") id: Long,
        @Body nota: NotaRequestDTO
    ): Response<Unit> // ✅ Unit es el equivalente a Void en Kotlin. Así Retrofit no intentará parsear nada.

    // DELETE /api/notes/{id} - Eliminar una nota
    @DELETE("api/notes/{id}")
    suspend fun deleteNote(@Path("id") id: Long): Response<Void>
}