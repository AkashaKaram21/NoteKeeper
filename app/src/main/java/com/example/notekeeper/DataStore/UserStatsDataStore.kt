package com.example.notekeeper.DataStore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Crea automáticamente un DataStore llamado "stats_prefs" asociado al Context
val Context.statsDataStore by preferencesDataStore("stats_prefs")

object UserStatsDataStore {

    // Claves para guardar valores en DataStore
    private val CREATES = intPreferencesKey("creates")
    private val UPDATES = intPreferencesKey("updates")
    private val DELETES = intPreferencesKey("deletes")
    private val HOURS = floatPreferencesKey("hours")

    /**
     * Guarda las estadísticas actuales del StatsTracker en DataStore.
     * Se ejecuta en una corrutina (suspend).
     */
    suspend fun save(context: Context) {
        context.statsDataStore.edit { prefs ->
            prefs[CREATES] = StatsTracker.creates
            prefs[UPDATES] = StatsTracker.updates
            prefs[DELETES] = StatsTracker.deletes
            prefs[HOURS] = StatsTracker.hoursUsed() // Se guarda en horas como Float
        }
    }

    /**
     * Carga las estadísticas desde DataStore y las aplica al StatsTracker.
     * Si no existen valores, usa 0 por defecto.
     */
    suspend fun load(context: Context) {
        // Obtiene los valores almacenados en DataStore
        val prefs = context.statsDataStore.data
            .map { it }
            .first()

        // Recupera valores o usa 0 si no existen
        StatsTracker.creates = prefs[CREATES] ?: 0
        StatsTracker.updates = prefs[UPDATES] ?: 0
        StatsTracker.deletes = prefs[DELETES] ?: 0

        // Convierte las horas guardadas a milisegundos para StatsTracker
        val hours = prefs[HOURS] ?: 0f
        val loadedTimeMs = (hours * 60 * 60 * 1000).toLong()
        StatsTracker.setTotalTimeMs(loadedTimeMs)
    }
}
