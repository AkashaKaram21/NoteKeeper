package com.example.notekeeper.Retrofit

import com.example.notekeeper.RecyclerView.TypeNote

/**
 * DTO para las peticiones POST/PUT al servidor
 *
 * Se envía cuando creas o editas una nota
 */
data class NotaRequestDTO(
    val title: String,
    val subtitle: String,
    val text: String,
    val category: TypeNote
)