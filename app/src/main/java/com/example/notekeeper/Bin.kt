package com.notekeeper

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notekeeper.DataStore.DadesStats
import com.example.notekeeper.R
import com.notekeeper.Retrofit.NoteAPI
import com.notekeeper.RecyclerView.NotaItem
import com.notekeeper.RecyclerView.NoteBinList
import com.notekeeper.RecyclerView.NoteList
import com.notekeeper.RecyclerView.RecyclerViewAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Bin : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_bin, container, false)

        val iBtnBack = view.findViewById<ImageButton>(R.id.iBtnBack)
        recyclerView = view.findViewById(R.id.notes)
        search = view.findViewById(R.id.search)

        // Botó per tornar a la pantalla principal (Home)
        iBtnBack.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, Home())
                .addToBackStack(null)
                .commit()
        }

        recyclerView.layoutManager = LinearLayoutManager(context)

        // Configuració de l'adaptador per a la paperera
        recyclerViewAdapter = RecyclerViewAdapter(
            // En l'adapter tenim les notes que estan a la paperera
            items = NoteBinList.items,

            // Si cliquem una nota a la paperera, cridem a la funció per esborrar-la definitivament
            onItemClick = { item ->
                deleteNote(item)
            },

            isBin = true,

            // Si recuperem la nota, es treu de la paperera i es torna a enviar al Home
            onRecoverClick = { item ->
                NoteBinList.items.remove(item)
                NoteList.items.add(item)
                applyFilter()
                Toast.makeText(context, "Nota recuperada", Toast.LENGTH_SHORT).show()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, Home())
                    .addToBackStack(null)
                    .commit()
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

        // Botó per mostrar el menú i filtrar per categoria dins de la paperera
        view.findViewById<ImageButton>(R.id.filter).setOnClickListener {
            showCategoryPopupMenu(it)
        }

        return view
    }
    // Aquesta funció s'encarrega de fer un delete definitiu de la nota mitjançant l'API
    private fun deleteNote(item: NotaItem) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = item.id?.let { NoteAPI.API().deleteNote(it) }

                withContext(Dispatchers.Main) {
                    if (response?.isSuccessful == true) {
                        // Augmentem el comptador d'eliminat
                        DadesStats.deletes++

                        NoteBinList.items.remove(item)
                        applyFilter()
                        Toast.makeText(context, "Nota eliminada", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("API", "Error al borrar: ${response?.code()}")
                        Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                Log.e("API", "Error de xarxa: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error de connexió", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Aquesta funció és un filtre per buscar per nom i categoria dins de la llista de la paperera
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