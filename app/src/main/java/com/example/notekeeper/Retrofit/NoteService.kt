package com.notekeeper.Retrofit

import com.notekeeper.RecyclerView.NotaItem
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/*
* el NoteService s'encarga de fer consultes i rebre.
 */
interface NoteService {
    @GET("api/notes")
    suspend fun getNotes(): Response<List<NotaItem>>

    @GET("api/notes/{id}")
    suspend fun getNoteById(@Path("id") id: Long): Response<NotaItem>

    @POST("api/notes")
    suspend fun createNote(@Body note: NoteRequest): Response<NotaItem>

    @PUT("api/notes/{id}/update")
    suspend fun updateNote(
        @Path("id") id: Long,
        @Body note: NotaItem
    ): Response<ResponseBody>

    @DELETE("api/notes/{id}/delete")
    suspend fun deleteNote(@Path("id") id: Long): Response<Unit>
}