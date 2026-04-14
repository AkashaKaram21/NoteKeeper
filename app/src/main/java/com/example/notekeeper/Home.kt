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

class Home : Fragment() {

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

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val bin = view.findViewById<ImageButton>(R.id.iBtnBin)

        // Abrir la papelera
        bin.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, Bin())
                .addToBackStack(null)
                .commit()
        }

        recyclerView = view.findViewById(R.id.notes)
        search = view.findViewById<SearchView>(R.id.search)

        // Configurar RecyclerView con layout lineal
        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerViewAdapter = RecyclerViewAdapter(
            items = NoteList.items,

            // Abrir editor de notas
            onItemClick = { item ->
                val editorFragment = NoteEditor()
                val bundle = Bundle()

                item.id?.let { bundle.putLong("NOTE_ID", it) }
                bundle.putString("NOTE_TITLE", item.title)
                bundle.putString("NOTE_SUBTITLE", item.subtitle)
                bundle.putString("NOTE_TEXT", item.text)
                bundle.putString("CATEGORIA", item.category.name)

                editorFragment.arguments = bundle

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, editorFragment)
                    .addToBackStack(null)
                    .commit()
            },

            // Mover nota a la papelera
            onMoveToBinClick = { item ->
                NoteList.items.remove(item)
                NoteBinList.items.add(item)
                applyFilter()
                Toast.makeText(context, "Nota moguda a papelera", Toast.LENGTH_SHORT).show()
            },

            // Eliminar nota definitivamente
            onDeleteClick = { item ->
                if (item.id != null) {
                    viewModel.deleteNote(item.id!!)
                    NoteList.items.remove(item)
                    applyFilter()
                    Toast.makeText(context, "Nota eliminada", Toast.LENGTH_SHORT).show()
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

        // Botón de filtro por categoría
        view.findViewById<ImageButton>(R.id.filter).setOnClickListener {
            showCategoryPopupMenu(it)
        }

        // Observadores del ViewModel para cargar la API
        viewModel.notasLoaded.observe(viewLifecycleOwner) { notasDTO ->
            NoteList.items.clear()
            val notasItem = notasDTO.map { dto ->
                NotaItem(
                    id = dto.id,
                    title = dto.title,
                    subtitle = dto.subtitle,
                    text = dto.text,
                    category = TypeNote.Simple,
                    color = SelectedColor.White,
                    isPinned = false,
                    timeReminder = null,
                    userShared = null,
                    userShareStatus = SharedStatus.pending,
                    ownerId = null
                )
            }
            NoteList.items.addAll(notasItem)
            recyclerViewAdapter.updateList(NoteList.items)
            applyFilter()
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
        }

        return view
    }
    // Aquesta funció és un filtre per buscar per nom i categoria dins de la llista principal
    private fun applyFilter() {
        val listaFiltrada = ArrayList<NotaItem>()

        for (note in NoteList.items) {

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

    // Mostrar menú de categorías
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

    //funció que te permite navegar entre fragmento con la voz
    fun filtraPorTitulo(query: String) {
        Toast.makeText(context, "Buscando nota: $query", Toast.LENGTH_SHORT).show()

        search.setQuery(query, true)
        searchedName = query.lowercase()
        applyFilter()
    }
}