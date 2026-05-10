package com.example.notekeeper.RecyclerView

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
    val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
    val tvSubtitle: TextView = itemView.findViewById(R.id.tvSubtitle)
    val tvText: TextView = itemView.findViewById(R.id.tvText)
    val iBtnMenu: ImageButton = itemView.findViewById(R.id.iBtnMenu)

    fun bind(item: NotaItem) {
        tvTitle.text = item.title
        tvSubtitle.text = item.subtitle
        tvText.text = item.text

        itemView.setOnClickListener {
            onItemClick(item)
        }
    }
}