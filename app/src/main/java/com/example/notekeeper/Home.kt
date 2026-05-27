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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notekeeper.RecyclerView.RecyclerViewAdapter
import com.example.notekeeper.Retrofit.NotesViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class Home : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var recognizer: SpeechRecognizer
    private lateinit var recognizerIntent: Intent
    private val viewModel: NotesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val voiceBtn = view.findViewById<ImageButton>(R.id.voiceRecognition)
        val btnFirebase = view.findViewById<ImageButton>(R.id.firebase)

        // Navegar a StatsActivity
        btnFirebase.setOnClickListener {
            startActivity(Intent(requireContext(), StatsActivity::class.java))
        }

        recyclerView = view.findViewById(R.id.notes)
        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerViewAdapter = RecyclerViewAdapter(
            items = emptyList(),

            onItemClick = { item ->
                viewModel.setNotaEditar(item)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, NoteEditor())
                    .addToBackStack(null)
                    .commit()
            },
            // Eliminar nota permanentemente
            onDeleteClick = { item ->
                viewModel.eliminarNota(item.id)
            }
        )

        recyclerView.adapter = recyclerViewAdapter

        viewModel.notes.observe(viewLifecycleOwner) { listaNotas ->
            recyclerViewAdapter.updateList(listaNotas)
        }

        viewModel.cargarNotas()

        initVoiceRecognition()

        voiceBtn.setOnClickListener {
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

    private fun initVoiceRecognition() {
        recognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())

        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ca-ES")
        }

        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}

            override fun onResults(results: Bundle?) {
                val spokenText = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.get(0)
                    ?.lowercase()
                handleVoiceCommand(spokenText)
            }
        })
    }

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

    private fun startVoiceRecognition() {
        recognizer.startListening(recognizerIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        recognizer.destroy()
    }

    override fun onResume() {
        super.onResume()
        viewModel.incrementarVisita("home")
    }
}