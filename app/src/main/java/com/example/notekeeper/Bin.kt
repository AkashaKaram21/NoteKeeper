package com.example.notekeeper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notekeeper.RecyclerView.*
import com.example.notekeeper.ViewModel.NotesViewModel

class Bin : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var search: SearchView

    private val viewModel: NotesViewModel by viewModels()

    var searchedCategory: String = "All"
    var searchedName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_bin, container, false)

        val btnBack = view.findViewById<ImageButton>(R.id.binIbBack)

        // Volver atrás al Home
        btnBack.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, Home())
                .addToBackStack(null)
                .commit()
        }

        recyclerView = view.findViewById(R.id.binNotes)
        search = view.findViewById<SearchView>(R.id.binSearch)

        // Configurar RecyclerView con layout lineal
        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerViewAdapter = RecyclerViewAdapter(
            items = NoteBinList.items,
            isBin = true,

            // No se puede editar una nota que está en la papelera
            onItemClick = { item ->
                Toast.makeText(context, "No es pot editar una nota a la papelera", Toast.LENGTH_SHORT).show()
            },

            // Recuperar nota desde la papelera
            onRecoverClick = { item ->
                NoteBinList.items.remove(item)
                NoteList.items.add(item)
                applyFilter()
                Toast.makeText(context, "Nota recuperada", Toast.LENGTH_SHORT).show()
            },

            // Eliminar nota permanentemente
            onDeleteClick = { item ->
                if (item.id != null) {
                    viewModel.deleteNote(item.id!!)
                    NoteBinList.items.remove(item)
                    applyFilter()
                    Toast.makeText(context, "Nota eliminada permanentment", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Error: la nota no té ID", Toast.LENGTH_SHORT).show()
                }
            }
        )

        recyclerView.adapter = recyclerViewAdapter

        // Búsqueda por texto
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                searchedName = newText?.lowercase() ?: ""
                applyFilter()
                return true
            }
        })

        // Observador de errores
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
        }

        return view
    }

    // Ens mostra un menú emergent per poder filtrar la nota segons el seu tipus
    private fun showCategoryPopupMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.note_filter, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->

            if (menuItem.itemId == R.id.category_normal) {
                searchedCategory = "Simple"
            } else if (menuItem.itemId == R.id.category_agenda) {
                searchedCategory = "Reminder"
            } else if (menuItem.itemId == R.id.category_shared) {
                searchedCategory = "Shared"
            } else {
                searchedCategory = "All"
            }

            applyFilter()
            true
        }

        popup.show()
    }

    // Aplicar filtros de categoría y búsqueda
    private fun applyFilter() {
        val listaFiltrada = ArrayList<NotaItem>()

        for (note in NoteBinList.items) {

            val coincideCategoria: Boolean
            if (searchedCategory == "All") {
                coincideCategoria = true
            } else {
                coincideCategoria = note.category.name == searchedCategory
            }

            val coincideNombre: Boolean
            if (searchedName.isEmpty()) {
                coincideNombre = true
            } else {
                coincideNombre = note.title.lowercase().contains(searchedName)
            }

            if (coincideCategoria && coincideNombre) {
                listaFiltrada.add(note)
            }
        }

        recyclerViewAdapter.updateList(listaFiltrada)
    }
}