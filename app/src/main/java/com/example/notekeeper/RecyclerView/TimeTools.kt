package com.notekeeper.RecyclerView

/*
* Función que transforma un valor long a hora y minutos.
 */
object TimeTools {
    fun formatLongToTimeString(minutes: Long?): String {
        //Si el número es null
        if (minutes == null) return ""
        //Retorna 00:00

        //1 hora es un total de 60 minuts
        val h = minutes / 60
        //minut és el residuo de 60
        //es dir es el temps que falta per que pasi a ser hora
        val m = minutes % 60
        //Formatem al long perquè retorn 2 unitas : 2 unitats
        return String.format("%02d:%02d", h, m)
    }
}