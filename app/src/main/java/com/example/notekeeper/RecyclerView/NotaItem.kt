package com.example.notekeeper.RecyclerView

/*
* Aquesta data class representa els elements que ha de tenir la llista del RecyclerView
 */
data class NotaItem(
    //Tenim que declarat el tipus de variable i el valor que guarda dins
    val id: Long,
    val title: String,
    val subtitle: String,
    val text: String,
    val category: TypeNote
)