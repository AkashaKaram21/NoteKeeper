package com.example.notekeeper

import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.notekeeper.DataStore.DadesStats
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.notekeeper.Home
import com.notekeeper.LogOut
import com.notekeeper.Settings

class MainActivity : AppCompatActivity() {

    // Per mesurar el temps d'inici de sessió
    private var startTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rootView = findViewById<View>(android.R.id.content)
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 1500
        rootView.startAnimation(fadeIn)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, Home())
                .commit()
        }

        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNav)

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

    // Quan l'usuari entra a l'app, guardem l'hora actual
    override fun onResume() {
        super.onResume()
        startTime = System.currentTimeMillis()
    }

    // Quan l'usuari surt o minimitza, calculem el temps que ha passat
    override fun onPause() {
        super.onPause()
        val endTime = System.currentTimeMillis()
        val sessionTimeMillis = endTime - startTime

        // Convertim a hores per al càlcul de CO2 posterior
        val sessionHours = sessionTimeMillis.toFloat() / (1000 * 60 * 60)
        DadesStats.horesUs += sessionHours
    }
}