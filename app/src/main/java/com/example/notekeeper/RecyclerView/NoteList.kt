package com.notekeeper.RecyclerView
/*
* Aquesta clase és un objecte que ens permet guardar el contingut de la lista de RecyclerView
 */
object NoteList {
    val items: MutableList<NotaItem> = mutableListOf(
        NotaItem(
            title = "Compra semanal",
            subtitle = "Supermercat",
            text = "Llet, ous, pa i fruita",
            category = TypeNote.Simple,
            color = SelectedColor.White
        ),
        NotaItem(
            title = "Cita Dentista",
            subtitle = "Mèdic",
            text = "Revisió anual",
            category = TypeNote.Reminder,
            color = SelectedColor.Yellow,
            timeReminder = (16 * 60 + 30).toLong()
        ),
        NotaItem(
            title = "Projecte Android",
            subtitle = "Treball",
            text = "Acabar el RecyclerView",
            category = TypeNote.Shared,
            color = SelectedColor.Blue,
            isPinned = true,
            userShared = "Marc",
            userShareStatus = SharedStatus.accepted
        ),
        NotaItem(
            title = "Aniversari",
            subtitle = "Festa",
            text = "Comprar el regal",
            category = TypeNote.Shared,
            color = SelectedColor.Blue,
            userShared = "Laia",
            userShareStatus = SharedStatus.pending
        ),
        NotaItem(
            title = "Gimnàs",
            subtitle = "Rutina",
            text = "Avui toca cames",
            category = TypeNote.Simple,
            color = SelectedColor.White
        ),
        NotaItem(
            title = "Trucar mare",
            subtitle = "Familiar",
            text = "Demanar recepta",
            category = TypeNote.Reminder,
            color = SelectedColor.Yellow,
            isPinned = true,
            timeReminder = (20 * 60 + 0).toLong()
        )
    )
}