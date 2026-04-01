package com.example.notekeeper.DataStore

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.notekeeper.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class Stats : Fragment() {

    private val db = Firebase.firestore
    private lateinit var tvCo2: TextView
    private lateinit var barChart: BarChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stats, container, false)

        barChart = view.findViewById(R.id.barChart)
        tvCo2 = view.findViewById(R.id.tvCo2)
        val btnSave = view.findViewById<Button>(R.id.btnSaveFirebase)

        // 1. Recuperar dades de Firebase al iniciar el fragment (Punt 4 apunts)
        obtenirDadesFirebase()

        // 2. Botó per guardar les dades actuals a Firestore (Punt 3 apunts)
        btnSave.setOnClickListener {
            guardarAFirebase()
        }

        return view
    }

    private fun configurarGrafic() {
        // Creem el dataset usant el mètode de DadesStats (Punt 5 apunts)
        val dataSet = BarDataSet(DadesStats.getEntries(), "Accions: 1=Crear, 2=Editar, 3=Eliminar")

        // Personalització de colors per a la nota màxima
        dataSet.colors = listOf(Color.CYAN, Color.YELLOW, Color.RED)
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 12f

        // Configuració d'eixos per a fons fosc
        barChart.xAxis.textColor = Color.WHITE
        barChart.axisLeft.textColor = Color.WHITE
        barChart.axisRight.textColor = Color.WHITE
        barChart.legend.textColor = Color.WHITE
        barChart.description.textColor = Color.WHITE

        val data = BarData(dataSet)
        barChart.data = data
        barChart.invalidate() // Actualitzar el dibuix

        // Càlcul d'energia i CO2 (Tasques de l'activitat)
        // Estimació: 0.2kg CO2 per cada hora d'ús
        val kgCo2 = DadesStats.horesUs * 0.2f
        tvCo2.text = "Estimació: ${String.format("%.6f", kgCo2)} kg CO2 equivalents"
    }

    private fun guardarAFirebase() {
        // Creem un mapa amb les dades de l'objecte global
        val dadesAMandar = hashMapOf(
            "creates" to DadesStats.creates,
            "updates" to DadesStats.updates,
            "deletes" to DadesStats.deletes,
            "horesUs" to DadesStats.horesUs
        )

        // Ho guardem a Firestore (Punt 3 apunts)
        db.collection("estadistiques").document("usuari_actual")
            .set(dadesAMandar)
            .addOnSuccessListener {
                Toast.makeText(context, "Sincronitzat amb Firebase!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al sincronitzar", Toast.LENGTH_SHORT).show()
            }
    }

    private fun obtenirDadesFirebase() {
        // Recuperem el document de la col·lecció (Punt 4 apunts)
        db.collection("estadistiques").document("usuari_actual")
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    // Passem les dades del núvol a la nostra memòria local
                    DadesStats.creates = doc.getDouble("creates")?.toFloat() ?: 0f
                    DadesStats.updates = doc.getDouble("updates")?.toFloat() ?: 0f
                    DadesStats.deletes = doc.getDouble("deletes")?.toFloat() ?: 0f
                    DadesStats.horesUs = doc.getDouble("horesUs")?.toFloat() ?: 0f
                }
                configurarGrafic()
            }
            .addOnFailureListener {
                configurarGrafic()
            }
    }
}