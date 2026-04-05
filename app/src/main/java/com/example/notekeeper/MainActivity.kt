package com.example.notekeeper

import android.os.Bundle
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

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
