package com.example.notekeeper.RecyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.notekeeper.R

class RecyclerViewAdapter(
    private var items: List<NotaItem>,
    private val onItemClick: (NotaItem) -> Unit,
    private val onDeleteClick: ((NotaItem) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.note, parent, false)
        return RecyclerViewHolder(view, onItemClick)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)

        holder.iBtnMenu.setOnClickListener { view ->
            val popup = PopupMenu(view.context, view)

            popup.menuInflater.inflate(R.menu.menu_nota_item, popup.menu)

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {

                    R.id.action_edit_note -> {
                        onItemClick(item)
                        true
                    }
                    R.id.eliminarNota -> {
                        onDeleteClick?.invoke(item)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    fun updateList(newList: List<NotaItem>) {
        items = newList
        notifyDataSetChanged()
    }
}