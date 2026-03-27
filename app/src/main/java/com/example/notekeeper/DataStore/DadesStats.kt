package com.example.notekeeper.DataStore

import com.github.mikephil.charting.data.BarEntry

// Objecte per guardar les dades seguint l'estil dels apunts (Punt 4)
object DadesStats {
    var creates = 0f
    var updates = 0f
    var deletes = 0f
    var horesUs = 0f

    // Funció que retorna la llista d'entrades per al BarChart
    fun getEntries(): List<BarEntry> {
        return listOf(
            BarEntry(1f, creates),
            BarEntry(2f, updates),
            BarEntry(3f, deletes)
        )
    }
}