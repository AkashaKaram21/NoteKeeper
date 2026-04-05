package com.example.notekeeper.Retrofit

data class NotaResponseDTO(
    val id: Long,
    val title: String,
    val subtitle: String,
    val text: String,
    val category: String = "Simple"
)