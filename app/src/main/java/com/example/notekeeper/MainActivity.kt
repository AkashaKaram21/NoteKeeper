package com.example.notekeeper

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.speech.RecognitionListener
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var recognizer: SpeechRecognizer
    private lateinit var recognizerIntent: Intent

    //Para identificar la petición de permisos
    private val RECORD_AUDIO_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Pedir permiso
        checkAudioPermission()
        setupVoiceRecognizer()

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

    private fun checkAudioPermission() {
        //Es necesario darle permisos para que pueda escuchar
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_REQUEST_CODE)
        }
    }

    fun startVoice() {
        // Iniciamos el voice recognizer
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            recognizer.startListening(recognizerIntent)
            Toast.makeText(this, "Escuchando...", Toast.LENGTH_SHORT).show()
        } else {
            checkAudioPermission()
        }
    }

    private fun setupVoiceRecognizer() {
        //Configuramos para que detecte nuestra voz y el idoma
        recognizer = SpeechRecognizer.createSpeechRecognizer(this)

        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            //En catalan no funcióna
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
        }

        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val text = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    ?.lowercase()

                text?.let { handleVoiceCommand(it) }
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

    //Función que te permite mover por voz en el bottom navigation
    private fun handleVoiceCommand(command: String) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        when {
            //Es necessario poner el ignoreCase prq no detecta los accentos
            command?.contains("home", ignoreCase = true) == true ||
                    command?.contains("inicio", ignoreCase = true) == true -> {
                bottomNav.selectedItemId = R.id.nav_home
                return
            }
            command?.contains("add", ignoreCase = true) == true ||
                    command?.contains("añadir", ignoreCase = true) == true -> {
                bottomNav.selectedItemId = R.id.nav_add
                return
            }
            command?.contains("settings", ignoreCase = true) == true ||
                    command?.contains("ajustes", ignoreCase = true) == true -> {
                bottomNav.selectedItemId = R.id.nav_settings
                return
            }
            command?.contains("profile", ignoreCase = true) == true ||
                    command?.contains("perfil", ignoreCase = true) == true -> {
                bottomNav.selectedItemId = R.id.nav_profile
                return
            }

            command?.contains("buscar", ignoreCase = true) == true -> {
                val query = command.replace("buscar", "").trim()
                val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
                if (currentFragment is Home) {
                    currentFragment.filtraPorTitulo(query)
                }
            }
        }
    }

    //Apagamos el recognizer cuando la app se apaga
    override fun onDestroy() {
        super.onDestroy()
        recognizer.destroy()
    }
}
