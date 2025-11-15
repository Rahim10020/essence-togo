package com.example.essence_togo.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.essence_togo.data.model.Station
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // state flow pour les stations visitees
    private val _visitedStations = MutableStateFlow<List<Station>>(emptyList())
    val visitedStations: StateFlow<List<Station>> = _visitedStations.asStateFlow()

    // state flow pour les stations favorites
    private val _favoriteStations = MutableStateFlow<List<Station>>(emptyList())
    val favoriteStations: StateFlow<List<Station>> = _favoriteStations.asStateFlow()

    // state flow pour les IDs des stations favorites
    private val _favoriteStationIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteStationIds: StateFlow<Set<Int>> = _favoriteStationIds.asStateFlow()

    companion object {
        private const val TAG = "PreferencesManager"
        private const val PREFS_NAME = "essence_togo_prefs"
        private const val KEY_VISITED_STATIONS = "visited_stations"
        private const val KEY_FAVORITE_STATIONS = "favorite_stations"
    }

    init {
        // charger les stations visitees au demarrage
        loadVisitedStations()
        // charger les stations favorites au demarrage
        loadFavoriteStations()
    }

    // ========== GESTION DES STATIONS VISITEES ==========

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
        try {
            val stationIdsString = prefs.getString(KEY_VISITED_STATIONS, "") ?: ""
            if (stationIdsString.isNotEmpty()){
                val stationIds = stationIdsString.split(",").mapNotNull { it.toIntOrNull() }
                // Les IDs sont chargés au démarrage
                // Les détails complets des stations sont récupérés via updateVisitedStationsWithDetails()
                // qui est appelé automatiquement depuis HomeViewModel après le chargement Firebase
                // Ce système permet de conserver l'ordre de l'historique même après redémarrage
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors du chargement des stations visitées", e)
            // En cas d'erreur, réinitialiser les données
            prefs.edit().remove(KEY_VISITED_STATIONS).apply()
        }
    }

    // mise a jour de la liste des stations visitees avec les details
    // a appeler quand on a recupere les details depuis firebase
    fun updateVisitedStationsWithDetails(allStations: List<Station>) {
        try {
            val stationIdsString = prefs.getString(KEY_VISITED_STATIONS, "") ?: ""
            if (stationIdsString.isNotEmpty()) {
                val stationIds = stationIdsString.split(",").mapNotNull { it.toIntOrNull() }
                val visitedStationsWithDetails = stationIds.mapNotNull { id ->
                    allStations.find { it.id == id }
                }
                _visitedStations.value  = visitedStationsWithDetails
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de la mise à jour des stations visitées", e)
            _visitedStations.value = emptyList()
        }
    }

    // ========== GESTION DES STATIONS FAVORITES ==========

    // ajout d'une station aux favoris
    fun addFavoriteStation(station: Station) {
        val currentFavorites = _favoriteStations.value.toMutableList()
        val currentIds = _favoriteStationIds.value.toMutableSet()

        // eviter les doublons
        if (!currentIds.contains(station.id)) {
            currentFavorites.add(station)
            currentIds.add(station.id)

            _favoriteStations.value = currentFavorites
            _favoriteStationIds.value = currentIds
            saveFavoriteStations(currentIds)
        }
    }

    // suppression d'une station des favoris
    fun removeFavoriteStation(stationId: Int) {
        val currentFavorites = _favoriteStations.value.toMutableList()
        val currentIds = _favoriteStationIds.value.toMutableSet()

        currentFavorites.removeAll { it.id == stationId }
        currentIds.remove(stationId)

        _favoriteStations.value = currentFavorites
        _favoriteStationIds.value = currentIds
        saveFavoriteStations(currentIds)
    }

    // basculer le statut favori d'une station
    fun toggleFavoriteStation(station: Station) {
        if (_favoriteStationIds.value.contains(station.id)) {
            removeFavoriteStation(station.id)
        } else {
            addFavoriteStation(station)
        }
    }

    // verifier si une station est favorite
    fun isStationFavorite(stationId: Int): Boolean {
        return _favoriteStationIds.value.contains(stationId)
    }

    // suppression de toutes les stations favorites
    fun clearFavoriteStations() {
        _favoriteStations.value = emptyList()
        _favoriteStationIds.value = emptySet()
        prefs.edit().remove(KEY_FAVORITE_STATIONS).apply()
    }

    // sauvegarde des stations favorites dans sharedPreferences
    private fun saveFavoriteStations(stationIds: Set<Int>) {
        val idsString = stationIds.joinToString(",")
        prefs.edit().putString(KEY_FAVORITE_STATIONS, idsString).apply()
    }

    // chargement des stations favorites depuis sharedPreferences
    private fun loadFavoriteStations() {
        try {
            val stationIdsString = prefs.getString(KEY_FAVORITE_STATIONS, "") ?: ""
            if (stationIdsString.isNotEmpty()) {
                val stationIds = stationIdsString.split(",").mapNotNull { it.toIntOrNull() }.toSet()
                _favoriteStationIds.value = stationIds
                // Les détails complets seront récupérés via updateFavoriteStationsWithDetails()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors du chargement des stations favorites", e)
            // En cas d'erreur, réinitialiser les données
            _favoriteStationIds.value = emptySet()
            prefs.edit().remove(KEY_FAVORITE_STATIONS).apply()
        }
    }

    // mise a jour de la liste des stations favorites avec les details complets
    // a appeler quand on a recupere les details depuis firebase
    fun updateFavoriteStationsWithDetails(allStations: List<Station>) {
        try {
            val favoriteIds = _favoriteStationIds.value
            if (favoriteIds.isNotEmpty()) {
                val favoriteStationsWithDetails = allStations.filter { station ->
                    favoriteIds.contains(station.id)
                }.map { it.copy(isFavorite = true) }
                _favoriteStations.value = favoriteStationsWithDetails
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de la mise à jour des stations favorites", e)
            _favoriteStations.value = emptyList()
        }
    }

    // mettre à jour le statut favori de toutes les stations
    fun updateStationsWithFavoriteStatus(stations: List<Station>): List<Station> {
        val favoriteIds = _favoriteStationIds.value
        return stations.map { station ->
            station.copy(isFavorite = favoriteIds.contains(station.id))
        }
    }
}