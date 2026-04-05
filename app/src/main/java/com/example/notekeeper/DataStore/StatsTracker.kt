package com.example.notekeeper.DataStore

import android.util.Log

object StatsTracker {

    private const val TAG = "StatsTracker"

    var creates = 0
    var updates = 0
    var deletes = 0

    @Volatile
    var totalTimeMs: Long = 0L
        private set

    @Volatile
    private var sessionStart: Long = 0L

    @Synchronized
    fun startSession() {
        if (sessionStart == 0L) {
            sessionStart = System.currentTimeMillis()
            Log.d(TAG, "startSession -> sessionStart=$sessionStart")
        } else {
            Log.d(TAG, "startSession ignorado (ya iniciado): sessionStart=$sessionStart")
        }
    }

    @Synchronized
    fun endSession() {
        if (sessionStart != 0L) {
            val end = System.currentTimeMillis()
            val delta = end - sessionStart
            totalTimeMs += delta
            Log.d(TAG, "endSession -> end=$end delta=$delta totalTimeMs=$totalTimeMs")
            sessionStart = 0L
        } else {
            Log.d(TAG, "endSession ignorado (no había sesión activa)")
        }
    }

    /**
     * Acumula tiempo desde DataStore/Firestore sin sobrescribir el tiempo actual
     */
    @Synchronized
    fun addLoadedTimeMs(value: Long) {
        totalTimeMs += value
        Log.d(TAG, "addLoadedTimeMs -> added=$value totalTimeMs=$totalTimeMs")
    }

    /**
     * Establece totalTimeMs directamente (usar con cuidado, solo para inicialización)
     */
    @Synchronized
    fun setTotalTimeMs(value: Long) {
        totalTimeMs = value
        Log.d(TAG, "setTotalTimeMs -> totalTimeMs=$totalTimeMs")
    }

    fun hoursUsed(): Float {
        val current = if (sessionStart != 0L) (System.currentTimeMillis() - sessionStart) else 0L
        val total = totalTimeMs + current
        return total / 1000f / 60f / 60f
    }

    fun totalActions() = creates + updates + deletes

    fun trackCreate() { creates++; Log.d(TAG, "trackCreate -> creates=$creates") }
    fun trackUpdate() { updates++; Log.d(TAG, "trackUpdate -> updates=$updates") }
    fun trackDelete() { deletes++; Log.d(TAG, "trackDelete -> deletes=$deletes") }
}