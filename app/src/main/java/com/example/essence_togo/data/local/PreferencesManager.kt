package com.example.essence_togo.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.essence_togo.data.model.Station
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // state flow pour les stations visitees
    private val _visitedStations = MutableStateFlow<List<Station>>(emptyList())
    val visitedStations: StateFlow<List<Station>> = _visitedStations.asStateFlow()

    companion object {
        private const val PREFS_NAME = "essence_togo_prefs"
        private const val KEY_VISITED_STATIONS = "visited_stations"
    }

    init {
        // charger les stations visitees au demarrage
        loadVisitedStations()
    }

    // ajout d'une station a l'historique des stations visitees
    fun addVisitedStation(station: Station) {
        val currentStations = _visitedStations.value.toMutableList()
        // eviter les doublons
        val existingStation = currentStations.find { it.id == station.id }
        if (existingStation != null) {
            currentStations.remove(existingStation)
        }

        // ajouter au debut de la liste ( les plus recents en premier)
        currentStations.add(0, station)

        // limiter a 50 stations maximum
        if (currentStations.size > 50) {
            currentStations.removeAt(currentStations.size - 1)
        }

        _visitedStations.value = currentStations
        saveVisitedStations(currentStations)
    }

    // suppression de toutes les stations visitees
    fun clearVisitedStations() {
        _visitedStations.value = emptyList()
        prefs.edit().remove(KEY_VISITED_STATIONS).apply()
    }

    // sauvegarde des stations visitees dans sharedPreferences
    private fun saveVisitedStations(stations: List<Station>) {
        val stationIds = stations.map { it.id }.joinToString(",")
        prefs.edit().putString(KEY_VISITED_STATIONS, stationIds).apply()
    }

    // chargement des stations visitees depuis sharedPreferences
    private fun loadVisitedStations() {
        val stationIdsString = prefs.getString(KEY_VISITED_STATIONS, "") ?: ""
        if (stationIdsString.isNotEmpty()){
            val stationIds = stationIdsString.split(",").mapNotNull { it.toIntOrNull() }
            // Les IDs sont chargés au démarrage
            // Les détails complets des stations sont récupérés via updateVisitedStationsWithDetails()
            // qui est appelé automatiquement depuis HomeViewModel après le chargement Firebase
            // Ce système permet de conserver l'ordre de l'historique même après redémarrage
        }
    }

    // mise a jour de la liste des stations visitees avec les details
    // a appeler quand on a recupere les details depuis firebase
    fun updateVisitedStationsWithDetails(allStations: List<Station>) {
        val stationIdsString = prefs.getString(KEY_VISITED_STATIONS, "") ?: ""
        if (stationIdsString.isNotEmpty()) {
            val stationIds = stationIdsString.split(",").mapNotNull { it.toIntOrNull() }
            val visitedStationsWithDetails = stationIds.mapNotNull { id ->
                allStations.find { it.id == id }
            }
            _visitedStations.value  = visitedStationsWithDetails
        }
    }
}