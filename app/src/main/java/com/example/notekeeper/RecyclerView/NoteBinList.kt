package com.example.notekeeper.RecyclerView

/*
* Aquesta clase és un objecte que ens permet guardar el contingut de la lista de RecyclerView
 */
object NoteBinList {
    val items: MutableList<NotaItem> = mutableListOf(
        NotaItem(
            title = "Anar al metge",
            subtitle = "per la tarda",
            text = "que ésta en el carre 21",
            category = TypeNote.Reminder,
            color = SelectedColor.Pink,
            timeReminder = (9 * 60 + 0).toLong()
        ),
        NotaItem(
            title = "Treball de recerca",
            subtitle = "Projecte X",
            text = "Entrega demà",
            category = TypeNote.Shared,
            color = SelectedColor.Blue,
            userShared = "Joan",
            userShareStatus = SharedStatus.rejected
        )
    )
}