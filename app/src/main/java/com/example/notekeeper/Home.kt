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
import com.example.notekeeper.Retrofit.NotesViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class Home : Fragment() {

    //Tenim que crear variables per poder accedir las clases que volem

    //RecyclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter

    // Reconeixament de veu
    private lateinit var recognizer: SpeechRecognizer
    private lateinit var recognizerIntent: Intent

    //ViewModel
    private val viewModel: NotesViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Obtenir referència
        val bin = view.findViewById<ImageButton>(R.id.iBtnBin)
        val voiceBtn = view.findViewById<ImageButton>(R.id.voiceRecognition)
        val filterBtn = view.findViewById<ImageButton>(R.id.filter)

        bin.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, Bin())
                .addToBackStack(null)
                .commit()
        }

        // 1. Obtenir referència al RecyclerView del layout
        recyclerView = view.findViewById(R.id.notes)

        // 2. Configurar LayoutManager (com es col·loquen les files)
        recyclerView.layoutManager = LinearLayoutManager(context)


        // 3. Crear l'Adapter passant les dades + funció de callback per clics
        recyclerViewAdapter = RecyclerViewAdapter(
            items = emptyList(),
            // Abrir editor
            onItemClick = { item ->
            },

            // Mover a papelera
            onMoveToBinClick = { item ->
            },

            // Eliminar definitivamente
            onDeleteClick = { item ->
            }
        )

        // 5. Assignar l'Adapter al RecyclerView
        recyclerView.adapter = recyclerViewAdapter

        // 6.- Avisem a l'adapter que rebra una llista
        viewModel.notes.observe(viewLifecycleOwner) { listaNotas ->
            recyclerViewAdapter.updateList(listaNotas)
        }

        //7.- Carguem la llista
        viewModel.cargarNotas()

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

    // Destruir recognizer
    override fun onDestroy() {
        super.onDestroy()
        recognizer.destroy()
    }
}
