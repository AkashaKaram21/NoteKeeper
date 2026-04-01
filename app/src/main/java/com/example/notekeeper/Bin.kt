package com.example.notekeeper

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notekeeper.RecyclerView.NotaItem
import com.example.notekeeper.RecyclerView.NoteBinList
import com.example.notekeeper.RecyclerView.NoteList
import com.example.notekeeper.RecyclerView.RecyclerViewAdapter
import com.example.notekeeper.Retrofit.NoteAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Bin : Fragment() {

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

        val btnBack = view.findViewById<ImageButton>(R.id.iBtnBin)
        recyclerView = view.findViewById(R.id.notes)
        search = view.findViewById<SearchView>(R.id.search)

        // Configurar el RecyclerView con layout lineal
        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerViewAdapter = RecyclerViewAdapter(
            items = NoteBinList.items,
            isBin = true,

            // Mostrar toast cuando se intenta editar una nota en la papelera
            onItemClick = { item ->
                Toast.makeText(context, "No es pot editar una nota a la papelera", Toast.LENGTH_SHORT).show()
            },

            // Recuperar la nota de la papelera
            onRecoverClick = { item ->
                NoteBinList.items.remove(item)
                NoteList.items.add(item)
                applyFilter()
                Toast.makeText(context, "Nota recuperada", Toast.LENGTH_SHORT).show()
            },

            // Eliminar permanentemente la nota
            onDeleteClick = { item ->
                deleteNotePermanently(item)
            }
        )

        recyclerView.adapter = recyclerViewAdapter

        // Configurar la búsqueda
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                searchedName = newText?.lowercase() ?: ""
                applyFilter()
                return true
            }
        })

        // Volver atrás
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    // Aplicar filtros de categoría y búsqueda
    private fun applyFilter() {
        val listaFiltrada = ArrayList<NotaItem>()

        for (note in NoteBinList.items) {

            val coincideCategoria: Boolean
            if (searchedCategory == "All") {
                coincideCategoria = true
            } else {
                coincideCategoria = note.category?.name == searchedCategory
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

    // CORREGIDO: Eliminar nota permanentemente desde la API
    private fun deleteNotePermanently(item: NotaItem) {
        if (item.id == null) {
            Toast.makeText(context, "Error: la nota no té ID", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Llamada a la API para eliminar
                val response = NoteAPI.API().deleteNote(item.id!!)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        NoteBinList.items.remove(item)
                        recyclerViewAdapter.updateList(NoteBinList.items)
                        applyFilter()
                        Toast.makeText(context, "Nota eliminada permanentment", Toast.LENGTH_SHORT).show()
                        Log.d("DELETE", "Nota eliminada permanentment: ${item.id}")
                    } else {
                        Log.e("API", "Error al eliminar: ${response.code()} - ${response.message()}")
                        Toast.makeText(context, "Error al eliminar (${response.code()})", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("API_DELETE", "Error: ${e.message}")
                    Toast.makeText(context, "Error de connexió: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}