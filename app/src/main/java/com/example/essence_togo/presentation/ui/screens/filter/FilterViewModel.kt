package com.example.essence_togo.presentation.ui.screens.filter

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.essence_togo.data.local.PreferencesManager
import com.example.essence_togo.data.model.Station
import com.example.essence_togo.data.repository.StationRepository
import com.example.essence_togo.utils.LocationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class FilterUiState(
    val allStations: List<Station>      = emptyList(),
    val filtredStations: List<Station>  = emptyList(),
    val searchQuery: String             = "",
    val isLoading: Boolean              = true,
    val error: String?                  = null,
    val userLocation: Location?         = null
)

class FilterViewModel(
    private val stationRepository: StationRepository,
    private  val locationManager: LocationManager,
    private val preferencesManager: PreferencesManager
): ViewModel() {
    private val _uiState                    = MutableStateFlow(FilterUiState())
    val uiState : StateFlow<FilterUiState>  = _uiState.asStateFlow()

    companion object {
        private const val TAG = "FilterViewModel"
    }

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                // recuperer la localisation de l'utilisateur
                val location    = getUserLocation()
                _uiState.value  = _uiState.value.copy(userLocation = location)

                // Observer les stations depuis firebase
                stationRepository.getAllStations()
                    .catch {exception ->
                        Log.e(TAG, "Erreur lors du chargement des stations", exception)
                        _uiState.value  = _uiState.value.copy(
                            isLoading   = false,
                            error       = "Erreur lors du chargement des stations"
                        )
                    }
                    .collect { stations ->
                        val processedStations = if (location != null) {
                            // calculer les distances
                            stationRepository.calculateDistancesForStations(
                                stations, location.latitude, location.longitude
                            )
                        } else {
                            stations
                        }

                        _uiState.value = _uiState.value.copy(
                            allStations     = processedStations,
                            filtredStations = processedStations,
                            isLoading       = false,
                            error           = null
                        )
                        Log.d(TAG, "Stations chargees pour le filtrage: ${processedStations.size}")
                    }
            } catch (exception: Exception) {
                Log.e(TAG, "Erreur generale dans loadData", exception)
                _uiState.value  = _uiState.value.copy(
                    isLoading   = false,
                    error       = "Erreur de connexion"
                )
            }
        }
    }

    private suspend fun getUserLocation(): Location? {
        return try {
            if (locationManager.hasLocationPermission()) {
                locationManager.getCurrentLocation() ?: locationManager.getDefaultLocation()
            } else {
                Log.w(TAG, "Permission de localisation non accordee, utilisation de la position par defaut")
                locationManager.getDefaultLocation()
            }
        } catch (exception: Exception) {
            Log.e(TAG, "Erreur lors de la recuperation de la localisation", exception)
            locationManager.getDefaultLocation()
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        filterStations(query)
    }

    private fun filterStations(query: String) {
        val currentState    = _uiState.value
        val filtredStations = if (query.isBlank()) {
            currentState.allStations
        } else {
            stationRepository.filterStations(currentState.allStations, query)
        }
        _uiState.value = _uiState.value.copy(
            filtredStations = filtredStations
        )
        Log.d(TAG, "Filtrage: '$query' -> ${filtredStations.size} stations")
    }

    fun onStationClick(station: Station) {
        // ajouter la station aux stations visitees
        preferencesManager.addVisitedStation(station)
        Log.d(TAG, "Station ajoutee a l'historique: ${station.nom}")
    }

    fun retry() {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null
        )
        loadData()
    }

    fun clearSearch() {
        onSearchQueryChange("")
    }
}