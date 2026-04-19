package com.example.notekeeper

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notekeeper.RecyclerView.*
import com.example.notekeeper.ViewModel.NotesViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class Home : Fragment() {

    // RecyclerView y adaptador
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var search: SearchView

    // Reconocimiento de voz
    private lateinit var recognizer: SpeechRecognizer
    private lateinit var recognizerIntent: Intent

    // ViewModel
    private val viewModel: NotesViewModel by viewModels()

    // Variables de filtro
    var searchedCategory: String = "All"
    var searchedName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflar layout
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Botones
        val bin = view.findViewById<ImageButton>(R.id.iBtnBin)
        val voiceBtn = view.findViewById<ImageButton>(R.id.voiceRecognition)
        val filterBtn = view.findViewById<ImageButton>(R.id.filter)

        // Ir a la papelera
        bin.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, Bin())
                .addToBackStack(null)
                .commit()
        }

        // Configurar RecyclerView
        recyclerView = view.findViewById(R.id.notes)
        search = view.findViewById(R.id.search)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Adaptador con acciones
        recyclerViewAdapter = RecyclerViewAdapter(
            items = NoteList.items,

            // Abrir editor
            onItemClick = { item ->
                val editorFragment = NoteEditor()
                val bundle = Bundle().apply {
                    item.id?.let { putLong("NOTE_ID", it) }
                    putString("NOTE_TITLE", item.title)
                    putString("NOTE_SUBTITLE", item.subtitle)
                    putString("NOTE_TEXT", item.text)
                    putString("CATEGORIA", item.category.name)
                }
                editorFragment.arguments = bundle

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, editorFragment)
                    .addToBackStack(null)
                    .commit()
            },

            // Mover a papelera
            onMoveToBinClick = { item ->
                NoteList.items.remove(item)
                NoteBinList.items.add(item)
                applyFilter()
            },

            // Eliminar definitivamente
            onDeleteClick = { item ->
                item.id?.let { viewModel.deleteNote(it) }
                NoteList.items.remove(item)
                applyFilter()
            }
        )

        recyclerView.adapter = recyclerViewAdapter

        // Filtro por texto
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                searchedName = newText?.lowercase() ?: ""
                applyFilter()
                return true
            }
        })

        // Botón de filtro
        filterBtn.setOnClickListener { showCategoryPopupMenu(it) }

        // Inicializar reconocimiento de voz
        initVoiceRecognition()

        // Botón de micrófono
        voiceBtn.setOnClickListener {
            // Pedir permiso si no está concedido
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    200
                )
            } else {
                startVoiceRecognition()
            }
        }

        // Cargar notas desde ViewModel
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
            applyFilter()
        }

        return view
    }

    // Configurar reconocimiento de voz
    private fun initVoiceRecognition() {
        recognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())

        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ca-ES")
        }

        // Listener del reconocimiento
        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}

            // Resultado final
            override fun onResults(results: Bundle?) {
                val spokenText = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.get(0)
                    ?.lowercase()

                handleVoiceCommand(spokenText)
            }
        })
    }

    // Procesar comandos de voz
    private fun handleVoiceCommand(command: String?) {
        if (command == null) return

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNav)

        when {
            command.contains("inici") || command.contains("home") ->
                bottomNav.selectedItemId = R.id.nav_home

            command.contains("afegir") || command.contains("add") || command.contains("nou") ->
                bottomNav.selectedItemId = R.id.nav_add

            command.contains("ajustos") || command.contains("settings") || command.contains("configuració") ->
                bottomNav.selectedItemId = R.id.nav_settings

            command.contains("perfil") || command.contains("profile") ->
                bottomNav.selectedItemId = R.id.nav_profile
        }
    }

    // Iniciar escucha del micrófono
    private fun startVoiceRecognition() {
        recognizer.startListening(recognizerIntent)
    }

    // Filtro por categoría y nombre
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

    // Menú emergente para filtrar por categoría
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

    // Destruir recognizer
    override fun onDestroy() {
        super.onDestroy()
        recognizer.destroy()
    }
}
