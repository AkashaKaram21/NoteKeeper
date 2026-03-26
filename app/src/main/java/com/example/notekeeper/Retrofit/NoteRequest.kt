package com.notekeeper.Retrofit
import com.notekeeper.RecyclerView.SelectedColor
import com.notekeeper.RecyclerView.TypeNote

/*
* Es un dto creat per rebre el post de l'API
 */
data class NoteRequest(
    val title: String,
    val subtitle: String,
    val text: String,
    val category: TypeNote,
    val color: SelectedColor
)