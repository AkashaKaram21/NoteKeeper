package com.example.notekeeper.Retrofit

data class NotaRequestDTO(
    val title: String,
    val subtitle: String,
    val text: String,
    val category: String = "Simple"
)