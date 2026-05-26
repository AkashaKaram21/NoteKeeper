package com.example.notekeeper

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class StatsActivity : AppCompatActivity() {

    // Connexió amb Firestore
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        // Botó per tornar enrere
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }
    }

    // Cada vegada que es torna a la pantalla, recarreguem les dades
    override fun onResume() {
        super.onResume()
        cargarEstadistiques()
        cargarVisites()
    }

    // Carrega les dades de les operacions CRUD desde Firestore
    private fun cargarEstadistiques() {
        db.collection("stats").document("crud")
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val creades = doc.getLong("creades").toString().toFloat()
                    val editades = doc.getLong("editades").toString().toFloat()
                    val eliminades = doc.getLong("eliminades").toString().toFloat()
                    mostrarGraficCrud(creades, editades, eliminades)
                    mostrarCO2(creades, editades, eliminades)
                } else {
                    // Si no hi ha dades, mostrem el gràfic buit
                    mostrarGraficCrud(0f, 0f, 0f)
                    mostrarCO2(0f, 0f, 0f)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Stats", "Error carregant: ${e.message}")
            }
    }

    // Carrega les visites de cada fragment desde Firestore
    private fun cargarVisites() {
        db.collection("stats").document("visites")
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val home = doc.getLong("home").toString().toFloat()
                    val add = doc.getLong("add").toString().toFloat()
                    val settings = doc.getLong("settings").toString().toFloat()
                    val profile = doc.getLong("profile").toString().toFloat()
                    mostrarGraficVisites(home, add, settings, profile)
                } else {
                    // Si no hi ha dades, mostrem el gràfic buit
                    mostrarGraficVisites(0f, 0f, 0f, 0f)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Stats", "Error carregant visites: ${e.message}")
            }
    }

    // Mostra el gràfic de barres de les operacions CRUD
    private fun mostrarGraficCrud(creades: Float, editades: Float, eliminades: Float) {
        val barChart = findViewById<BarChart>(R.id.barChart)

        // Un DataSet per cada barra per tenir colors i llegenda separats
        val dsCreades = BarDataSet(listOf(BarEntry(0f, creades)), "Creades")
        dsCreades.color = Color.GREEN

        val dsEditades = BarDataSet(listOf(BarEntry(1f, editades)), "Editades")
        dsEditades.color = Color.BLUE

        val dsEliminades = BarDataSet(listOf(BarEntry(2f, eliminades)), "Eliminades")
        dsEliminades.color = Color.RED

        barChart.data = BarData(dsCreades, dsEditades, dsEliminades)
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(
            listOf("Creades", "Editades", "Eliminades")
        )
        barChart.description.text = "Notes per operació"
        barChart.legend.isEnabled = true
        barChart.legend.textColor = Color.WHITE
        barChart.xAxis.textColor = Color.WHITE
        barChart.axisLeft.textColor = Color.WHITE
        barChart.axisRight.textColor = Color.WHITE
        barChart.description.textColor = Color.WHITE
        barChart.animateY(1000)
        barChart.invalidate()
    }

    // Mostra el gràfic de barres de les visites per fragment
    private fun mostrarGraficVisites(home: Float, add: Float, settings: Float, profile: Float) {
        val barChart = findViewById<BarChart>(R.id.barChartVisites)

        // Un DataSet per cada barra per tenir colors i llegenda separats
        val dsHome = BarDataSet(listOf(BarEntry(0f, home)), "Home")
        dsHome.color = Color.CYAN

        val dsAdd = BarDataSet(listOf(BarEntry(1f, add)), "Add")
        dsAdd.color = Color.MAGENTA

        val dsSettings = BarDataSet(listOf(BarEntry(2f, settings)), "Settings")
        dsSettings.color = Color.YELLOW

        val dsProfile = BarDataSet(listOf(BarEntry(3f, profile)), "Profile")
        dsProfile.color = Color.GREEN

        barChart.data = BarData(dsHome, dsAdd, dsSettings, dsProfile)
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(
            listOf("Home", "Add", "Settings", "Profile")
        )
        barChart.description.text = "Visites per fragment"
        barChart.legend.isEnabled = true
        barChart.legend.textColor = Color.WHITE
        barChart.xAxis.textColor = Color.WHITE
        barChart.axisLeft.textColor = Color.WHITE
        barChart.axisRight.textColor = Color.WHITE
        barChart.description.textColor = Color.WHITE
        barChart.animateY(1000)
        barChart.invalidate()
    }

    // Calcula i mostra l'estimació de CO₂
    private fun mostrarCO2(creades: Float, editades: Float, eliminades: Float) {
        val tvCO2 = findViewById<TextView>(R.id.tvCO2)
        val totalOps = creades + editades + eliminades
        val co2 = totalOps * 0.0001f
        tvCO2.text = "Estimació CO₂: ${"%.4f".format(co2)} kg CO₂"
    }
}