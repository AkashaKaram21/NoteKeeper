package com.notekeeper

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notekeeper.R
import com.notekeeper.Retrofit.NoteAPI
import com.notekeeper.RecyclerView.NotaItem
import com.notekeeper.RecyclerView.NoteBinList
import com.notekeeper.RecyclerView.NoteList
import com.notekeeper.RecyclerView.RecyclerViewAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Home : Fragment() {

    // Variables per a la llista, l'adaptador i el cercador
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var search: SearchView

    var searchedCategory: String = "All"
    var searchedName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val bin = view.findViewById<ImageButton>(R.id.iBtnBin)
        // Botó per anar a gràfics
        val stats = view.findViewById<ImageButton>(R.id.iBtnStats)
        recyclerView = view.findViewById(R.id.notes)
        search = view.findViewById(R.id.search)

        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerViewAdapter = RecyclerViewAdapter(
            // En l'adapter tenim les notes actives (NoteList)
            items = NoteList.items,

            // Acció d'editar: Passem les dades de la nota al NoteEditor
            onItemClick = { item ->
                val editorFragment = NoteEditor()
                val bundle = Bundle()

                // Passem l'ID com a Long i la resta de strings
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

            // Si cliquem per moure a la papelera, es treu de Home i s'afegeix al Bin localment
            onMoveToBinClick = { item ->
                NoteList.items.remove(item)
                NoteBinList.items.add(item)
                applyFilter()
            }
        )

        recyclerView.adapter = recyclerViewAdapter

        // Es un filtre que s'encarrega de buscar la nota si el nom coincideix amb el que es busca
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                searchedName = newText?.lowercase() ?: ""
                applyFilter()
                return true
            }
        })

        // Botó per mostrar el menú i filtrar per categoria
        view.findViewById<ImageButton>(R.id.filter).setOnClickListener {
            showCategoryPopupMenu(it)
        }

        // Navegació a la papelera (Bin)
        bin.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, Bin())
                .addToBackStack(null)
                .commit()
        }

        // Navegació a les estadístiques (Stats) - NOU
        stats.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, Stats())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // Carreguem les notes de l'API per actualitzar la llista principal
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = NoteAPI.API().getNotes()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val notes = response.body()
                        if (notes != null) {
                            NoteList.items.clear()
                            NoteList.items.addAll(notes)
                            recyclerViewAdapter.updateList(NoteList.items)
                            applyFilter()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.message}")
            }
        }
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
}