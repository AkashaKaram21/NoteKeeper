package com.example.notekeeper.RecyclerView

/*
* Aquesta data class representa els elements que ha de tenir la llista del RecyclerView
 */
data class NotaItem(
    //Tenim que declarat el tipus de variable i el valor que guarda dins
    val id: Long? = null,
    val title: String,
    val subtitle: String,
    val text: String,
    val category: TypeNote,
    val color: SelectedColor = SelectedColor.White,
    var isPinned: Boolean? = false,
    val timeReminder: Long? = null,
    val userShared: String? = null,
    val userShareStatus: SharedStatus = SharedStatus.pending,
    val ownerId: String? = null
)