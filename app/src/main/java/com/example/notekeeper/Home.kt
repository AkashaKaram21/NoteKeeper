package com.example.notekeeper

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notekeeper.DataStore.StatsTracker
import com.example.notekeeper.RecyclerView.NotaItem
import com.example.notekeeper.RecyclerView.NoteBinList
import com.example.notekeeper.RecyclerView.NoteList
import com.example.notekeeper.RecyclerView.RecyclerViewAdapter
import com.example.notekeeper.RecyclerView.SelectedColor
import com.example.notekeeper.RecyclerView.SharedStatus
import com.example.notekeeper.RecyclerView.TypeNote
import com.example.notekeeper.Retrofit.NotesAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Home : Fragment() {

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

        // Abrir la papelera
        bin.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, Bin())
                .addToBackStack(null)
                .commit()
        }

        val btnStats = view.findViewById<ImageButton>(R.id.iBtnStats)

        // Abrir estadísticas
        btnStats.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, com.example.notekeeper.DataStore.Stats())
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
                deleteNote(item)
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

        return view
    }

    override fun onResume() {
        super.onResume()

        // Iniciar tracking de sesión
        StatsTracker.startSession()

        // Esperar un poco antes de recargar notas
        recyclerView.postDelayed({
            loadNotesFromAPI()
        }, 300)
    }

    override fun onPause() {
        super.onPause()
        // Finalizar tracking de sesión
        StatsTracker.endSession()
    }

    // Cargar notas desde la API
    private fun loadNotesFromAPI() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = NotesAPI.API().getAllNotes()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {

                        val notasResponseDTO = response.body()

                        if (notasResponseDTO != null) {

                            // Limpiar lista actual
                            NoteList.items.clear()

                            // Convertir DTO a NotaItem
                            val notasItem = notasResponseDTO.map { dto ->
                                NotaItem(
                                    id = dto.id,
                                    title = dto.title,
                                    subtitle = dto.subtitle,
                                    text = dto.text,
                                    category = TypeNote.valueOf(dto.category),
                                    color = SelectedColor.White,
                                    isPinned = false,
                                    timeReminder = null,
                                    userShared = null,
                                    userShareStatus = SharedStatus.pending,
                                    ownerId = null
                                )
                            }

                            // Añadir notas cargadas
                            NoteList.items.addAll(notasItem)

                            recyclerViewAdapter.updateList(NoteList.items)
                            applyFilter()
                        }

                    } else {
                        Log.e("API", "Error al cargar: ${response.code()}")
                        Toast.makeText(context, "Error al cargar les notes", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("API", "Error: ${e.message}")
                    Toast.makeText(context, "Error de connexió", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Aplicar filtros de categoría y búsqueda
    private fun applyFilter() {
        val listaFiltrada = ArrayList<NotaItem>()

        for (note in NoteList.items) {

            val coincideCategoria =
                if (searchedCategory == "All") true
                else note.category.name == searchedCategory

            val coincideNombre =
                if (searchedName.isEmpty()) true
                else note.title.lowercase().contains(searchedName)

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

            searchedCategory = when (menuItem.itemId) {
                R.id.category_normal -> "Simple"
                R.id.category_agenda -> "Reminder"
                R.id.category_shared -> "Shared"
                else -> "All"
            }

            applyFilter()
            true
        }

        popup.show()
    }

    // Eliminar nota desde la API
    private fun deleteNote(item: NotaItem) {

        if (item.id == null) {
            Toast.makeText(context, "Error: la nota no té ID", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = NotesAPI.API().deleteNote(item.id!!)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {

                        // Track solo si se eliminó correctamente
                        StatsTracker.trackDelete()

                        NoteList.items.remove(item)
                        NoteBinList.items.remove(item)

                        recyclerViewAdapter.updateList(NoteList.items)
                        applyFilter()

                        Toast.makeText(context, "Nota eliminada", Toast.LENGTH_SHORT).show()
                        Log.d("DELETE", "Nota eliminada: ${item.id}")

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
