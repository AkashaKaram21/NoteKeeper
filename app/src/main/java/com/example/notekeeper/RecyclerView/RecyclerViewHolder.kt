package com.notekeeper.RecyclerView

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.notekeeper.R

/*
 * La classe Holder guarda els elements dels ítems i els pinta.
 */
class RecyclerViewHolder(
    itemView: View,
    private val onItemClick: (NotaItem) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    // Recuperem els elements de l'ítem
    val cardNota: CardView = itemView.findViewById(R.id.cardNota)
    val ivPin: ImageView = itemView.findViewById(R.id.ivPin)
    val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
    val tvSubtitle: TextView = itemView.findViewById(R.id.tvSubtitle)
    val tvText: TextView = itemView.findViewById(R.id.tvText)
    val tvHora: TextView = itemView.findViewById(R.id.tvHora)
    val tvPersona: TextView = itemView.findViewById(R.id.tvPersona)
    val iBtnMenu: ImageButton = itemView.findViewById(R.id.iBtnMenu)

    fun bind(item: NotaItem) {
        tvTitle.text = item.title
        tvSubtitle.text = item.subtitle
        tvText.text = item.text
        cardNota.setCardBackgroundColor(itemView.context.getColor(item.color.colorDisponible))

        // Si és cert (true), mostrem la icona de fixar (l'anclat)
        if (item.isPinned == true) {
            ivPin.visibility = View.VISIBLE
        } else {
            // En cas contrari, l'ocultem
            ivPin.visibility = View.GONE
        }

        // Si la nota és de tipus Recordatori (Reminder)
        // REVISA: Que TypeNote estigui accessible
        if (item.category == TypeNote.Reminder) {
            // El timeReminder no està buit
            if (item.timeReminder != null) {
                // Mostrem la seva icona i el timeReminder
                tvHora.visibility = View.VISIBLE
                // Transformem el timeReminder, que és un valor que es desa en Long,
                // a un format d'hores i minuts amb una funció anomenada TimeTools per a l'usuari
                tvHora.text = TimeTools.formatLongToTimeString(item.timeReminder!!)
            } else {
                tvHora.visibility = View.GONE
            }
        } else {
            tvHora.visibility = View.GONE
        }

        // Si la nota és Compartida (Shared)
        if (item.category == TypeNote.Shared) {
            // I l'usuari no és nul
            if (item.userShared != null) {
                // Mostrem la icona d'una persona
                tvPersona.visibility = View.VISIBLE

                var textoEstado = ""
                // Si l'estat coincideix amb SharedStatus
                if (item.userShareStatus == SharedStatus.accepted) {
                    // Retornem un text
                    textoEstado = "(Acceptada)"
                } else if (item.userShareStatus == SharedStatus.rejected) {
                    textoEstado = "(Rebutjada)"
                } else {
                    textoEstado = "(Pendent)"
                }
                tvPersona.text = item.userShared + " " + textoEstado
            } else {
                tvPersona.visibility = View.GONE
            }
        } else {
            tvPersona.visibility = View.GONE
        }

        cardNota.setOnClickListener {
            onItemClick(item)
        }
    }
}