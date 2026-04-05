package com.example.notekeeper.DataStore

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.notekeeper.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class Stats : Fragment() {

    // Referencias a los elementos de la UI
    private lateinit var tvTotal: TextView
    private lateinit var tvHores: TextView
    private lateinit var tvCo2: TextView
    private lateinit var barChart: BarChart
    private lateinit var pieChart: PieChart
    private lateinit var btnSave: Button

    // Handler para actualizar la UI cada segundo
    private val handler = Handler(Looper.getMainLooper())

    // Runnable que actualiza estadísticas y gráficos periódicamente
    private val updateRunnable = object : Runnable {
        override fun run() {
            loadStatsUI()
            setupBarChart()
            setupPieChart()
            handler.postDelayed(this, 1000) // Actualiza cada 1 segundo
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Infla el layout del fragmento
        val view = inflater.inflate(R.layout.fragment_stats, container, false)

        // Inicializa vistas
        tvTotal = view.findViewById(R.id.tvTotalActions)
        tvHores = view.findViewById(R.id.tvHoresUs)
        tvCo2 = view.findViewById(R.id.tvCo2)
        barChart = view.findViewById(R.id.barChart)
        pieChart = view.findViewById(R.id.pieChart)
        btnSave = view.findViewById(R.id.btnSaveFirebase)

        // Carga datos desde Firestore y actualiza UI
        loadFromFirestore()
        loadStatsUI()
        setupBarChart()
        setupPieChart()
        setupFirebaseButton()

        return view
    }

    override fun onResume() {
        super.onResume()
        // Inicia el contador de tiempo de uso
        StatsTracker.startSession()
        // Empieza actualizaciones periódicas
        handler.post(updateRunnable)
    }

    override fun onPause() {
        super.onPause()
        // Finaliza la sesión de uso
        StatsTracker.endSession()
        // Detiene actualizaciones
        handler.removeCallbacks(updateRunnable)
    }

    // Carga estadísticas guardadas en Firestore
    private fun loadFromFirestore() {
        val db = Firebase.firestore

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val doc = db.collection("stats").document("usuari1").get().await()

                if (doc.exists()) {
                    // Recupera valores de Firestore
                    StatsTracker.creates = doc.getLong("creates")?.toInt() ?: 0
                    StatsTracker.updates = doc.getLong("updates")?.toInt() ?: 0
                    StatsTracker.deletes = doc.getLong("deletes")?.toInt() ?: 0

                    // Convierte horas guardadas a milisegundos
                    val hours = doc.getDouble("hours")?.toFloat() ?: 0f
                    val timeMs = (hours * 60 * 60 * 1000).toLong()
                    StatsTracker.setTotalTimeMs(timeMs)
                }

                // Actualiza UI en el hilo principal
                launch(Dispatchers.Main) {
                    loadStatsUI()
                    setupBarChart()
                    setupPieChart()
                }

            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "Error carregant Firestore", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Actualiza los textos de estadísticas
    private fun loadStatsUI() {
        tvTotal.text = "Total d'operacions: ${StatsTracker.totalActions()}"

        val hores = StatsTracker.hoursUsed()
        tvHores.text = "Hores d'ús: ${"%.4f".format(hores)}"

        // Cálculo simple de CO₂ estimado
        val co2 = hores * 0.002f
        tvCo2.text = "Estimació CO₂: ${"%.8f".format(co2)} kg"
    }

    // Configura el gráfico de barras
    private fun setupBarChart() {
        val entries = listOf(
            BarEntry(1f, StatsTracker.creates.toFloat()),
            BarEntry(2f, StatsTracker.updates.toFloat()),
            BarEntry(3f, StatsTracker.deletes.toFloat())
        )

        val dataSet = BarDataSet(entries, "Accions")
        dataSet.color = Color.CYAN
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 12f

        barChart.data = BarData(dataSet)
        barChart.description.text = ""
        barChart.axisLeft.textColor = Color.WHITE
        barChart.axisRight.textColor = Color.WHITE
        barChart.xAxis.textColor = Color.WHITE
        barChart.legend.textColor = Color.WHITE
        barChart.invalidate()
    }

    // Configura el gráfico circular
    private fun setupPieChart() {
        val total = StatsTracker.totalActions().takeIf { it > 0 } ?: 1

        val entries = listOf(
            PieEntry(StatsTracker.creates.toFloat() / total, "Creates"),
            PieEntry(StatsTracker.updates.toFloat() / total, "Updates"),
            PieEntry(StatsTracker.deletes.toFloat() / total, "Deletes")
        )

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(Color.GREEN, Color.YELLOW, Color.RED)
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 12f

        pieChart.data = PieData(dataSet)
        pieChart.setUsePercentValues(true)
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.centerText = "Accions"
        pieChart.setCenterTextColor(Color.WHITE)
        pieChart.description.isEnabled = false
        pieChart.legend.textColor = Color.WHITE
        pieChart.invalidate()
    }

    // Configura el botón para guardar estadísticas en Firestore
    private fun setupFirebaseButton() {
        btnSave.setOnClickListener {

            btnSave.isEnabled = false
            btnSave.text = "Guardant..."
            Toast.makeText(context, "Guardant dades...", Toast.LENGTH_SHORT).show()

            val db = Firebase.firestore

            // Datos a guardar
            val data = hashMapOf(
                "creates" to StatsTracker.creates,
                "updates" to StatsTracker.updates,
                "deletes" to StatsTracker.deletes,
                "hours" to StatsTracker.hoursUsed()
            )

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    // Guarda en Firestore
                    db.collection("stats").document("usuari1").set(data).await()

                    launch(Dispatchers.Main) {
                        btnSave.isEnabled = true
                        btnSave.text = "Guardar estadístiques a Firestore"
                        Toast.makeText(context, "Dades guardades correctament!", Toast.LENGTH_LONG).show()
                    }

                } catch (e: Exception) {
                    launch(Dispatchers.Main) {
                        btnSave.isEnabled = true
                        btnSave.text = "Guardar estadístiques a Firestore"
                        Toast.makeText(context, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
