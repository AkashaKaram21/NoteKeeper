package com.example.notekeeper.Retrofit

/**
 * DTO para la respuesta del servidor al obtener notas
 */
data class NotaResponseDTO(
    val id: Long,
    val title: String,
    val subtitle: String,
    val text: String,
)