package com.example.notekeeper

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.notekeeper.DataStore.StatsTracker
import com.example.notekeeper.DataStore.UserStatsDataStore
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.speech.RecognitionListener

class MainActivity : AppCompatActivity() {

    private lateinit var recognizer: SpeechRecognizer
    private lateinit var recognizerIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupVoiceRecognizer()

        FirebaseApp.initializeApp(this)

        // Cargar estadísticas guardadas en DataStore y Firestore
        lifecycleScope.launch(Dispatchers.IO) {

            // Cargar datos locales
            UserStatsDataStore.load(this@MainActivity)

            // Cargar datos remotos y sumarlos
            try {
                val db = Firebase.firestore
                val doc = db.collection("stats").document("usuari1").get().await()

                if (doc.exists()) {
                    val creates = doc.getLong("creates")?.toInt() ?: 0
                    val updates = doc.getLong("updates")?.toInt() ?: 0
                    val deletes = doc.getLong("deletes")?.toInt() ?: 0
                    val hours = doc.getDouble("hours")?.toFloat() ?: 0f

                    StatsTracker.creates = creates
                    StatsTracker.updates = updates
                    StatsTracker.deletes = deletes

                    // Convertir horas a milisegundos y sumarlas
                    val firebaseTimeMs = (hours * 60 * 60 * 1000).toLong()
                    StatsTracker.addLoadedTimeMs(firebaseTimeMs)
                }

            } catch (e: Exception) {
                // Si falla Firestore, seguimos con los datos locales
                android.util.Log.e("Firestore", "Error loading stats: ${e.message}")
            }
        }

        // Animación de entrada
        val rootView = findViewById<View>(android.R.id.content)
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 1500
        rootView.startAnimation(fadeIn)

        // Cargar Home al iniciar la app
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, Home())
                .commit()
        }

        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNav)

        // Navegación inferior
        bottomNav.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment? = when (item.itemId) {
                R.id.nav_home -> Home()
                R.id.nav_add -> Add()
                R.id.nav_settings -> Settings()
                R.id.nav_profile -> LogOut()
                else -> null
            }

            selectedFragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, it)
                    .commit()
            }

            true
        }
    }

    fun startVoice() {
        recognizer.startListening(recognizerIntent)
    }

    private fun setupVoiceRecognizer() {
        recognizer = SpeechRecognizer.createSpeechRecognizer(this)

        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
        }

        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val text = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    ?.lowercase()

                handleVoiceCommand(text)
            }

            override fun onError(error: Int) {}
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    private fun handleVoiceCommand(command: String?) {

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        when {
            command?.contains("home", ignoreCase = true) == true -> {
                bottomNav.selectedItemId = R.id.nav_home
            }
            command?.contains("add", ignoreCase = true) == true ||
                    command?.contains("añadir", ignoreCase = true) == true -> {
                bottomNav.selectedItemId = R.id.nav_add
            }
            command?.contains("settings", ignoreCase = true) == true ||
                    command?.contains("ajustes", ignoreCase = true) == true -> {
                bottomNav.selectedItemId = R.id.nav_settings
            }
            command?.contains("profile", ignoreCase = true) == true ||
                    command?.contains("perfil", ignoreCase = true) == true -> {
                bottomNav.selectedItemId = R.id.nav_profile
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        recognizer.destroy()
    }

    override fun onResume() {
        super.onResume()
        // Iniciar conteo de tiempo en pantalla
        StatsTracker.startSession()
    }

    override fun onPause() {
        super.onPause()
        // Guardar tiempo al salir
        StatsTracker.endSession()

        // Guardar estadísticas en DataStore
        lifecycleScope.launch(Dispatchers.IO) {
            UserStatsDataStore.save(this@MainActivity)
        }
    }

}
