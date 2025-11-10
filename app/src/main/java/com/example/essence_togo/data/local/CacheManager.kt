package com.example.essence_togo.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.essence_togo.data.model.Station
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CacheManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val TAG = "CacheManager"
        private const val PREFS_NAME = "stations_cache"
        private const val KEY_STATIONS = "cached_stations"
        private const val KEY_LAST_UPDATE = "last_update_timestamp"
        private const val CACHE_VALIDITY_HOURS = 24 // Cache valide pendant 24h
    }

    /**
     * Sauvegarde les stations dans le cache
     */
    fun cacheStations(stations: List<Station>) {
        try {
            val json = gson.toJson(stations)
            prefs.edit()
                .putString(KEY_STATIONS, json)
                .putLong(KEY_LAST_UPDATE, System.currentTimeMillis())
                .apply()
            Log.d(TAG, "Stations mises en cache: ${stations.size}")
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de la mise en cache des stations", e)
        }
    }

    /**
     * Récupère les stations depuis le cache
     */
    fun getCachedStations(): List<Station>? {
        try {
            val json = prefs.getString(KEY_STATIONS, null) ?: return null
            val type = object : TypeToken<List<Station>>() {}.type
            val stations: List<Station> = gson.fromJson(json, type)
            Log.d(TAG, "Stations récupérées du cache: ${stations.size}")
            return stations
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de la récupération du cache", e)
            return null
        }
    }

    /**
     * Vérifie si le cache est valide (pas trop ancien)
     */
    fun isCacheValid(): Boolean {
        val lastUpdate = prefs.getLong(KEY_LAST_UPDATE, 0)
        if (lastUpdate == 0L) return false

        val currentTime = System.currentTimeMillis()
        val elapsedHours = (currentTime - lastUpdate) / (1000 * 60 * 60)

        val isValid = elapsedHours < CACHE_VALIDITY_HOURS
        Log.d(TAG, "Cache valide: $isValid (âge: ${elapsedHours}h)")
        return isValid
    }

    /**
     * Efface le cache
     */
    fun clearCache() {
        prefs.edit()
            .remove(KEY_STATIONS)
            .remove(KEY_LAST_UPDATE)
            .apply()
        Log.d(TAG, "Cache effacé")
    }

    /**
     * Obtient la date de dernière mise à jour du cache
     */
    fun getLastUpdateTimestamp(): Long {
        return prefs.getLong(KEY_LAST_UPDATE, 0)
    }

    /**
     * Vérifie si le cache existe
     */
    fun hasCachedData(): Boolean {
        return prefs.contains(KEY_STATIONS)
    }
}