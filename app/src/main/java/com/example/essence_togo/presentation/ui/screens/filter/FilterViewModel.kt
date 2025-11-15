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
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.FlowPreview


enum class ErrorType {
    LOADING_STATIONS,
    NO_CACHE,
    UNKNOWN,
    CONNECTION
}

data class FilterUiState(
    val allStations: List<Station>      = emptyList(),
    val filtredStations: List<Station>  = emptyList(),
    val searchQuery: String             = "",
    val isLoading: Boolean              = true,
    val errorType: ErrorType?           = null,
    val userLocation: Location?         = null
)

class FilterViewModel(
    private val stationRepository: StationRepository,
    private val locationManager: LocationManager,
    private val preferencesManager: PreferencesManager,
): ViewModel() {
    private val _uiState                    = MutableStateFlow(FilterUiState())
    val uiState : StateFlow<FilterUiState>  = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    companion object {
        private const val TAG = "FilterViewModel"
        private const val SEARCH_DEBOUNCE_MS = 300L
    }

    init {
        loadData()
        setupSearchDebounce()
    }

    private fun setupSearchDebounce() {
        viewModelScope.launch {
            @OptIn(FlowPreview::class)
            _searchQuery
                .debounce(SEARCH_DEBOUNCE_MS)
                .collect { query ->
                    filterStations(query)
                }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                // Recuperer la localisation de l'utilisateur
                val location    = getUserLocation()
                _uiState.value  = _uiState.value.copy(userLocation = location)

                // Observer les stations depuis Firebase
                stationRepository.getAllStations()
                    .catch { exception ->
                        Log.e(TAG, "Erreur lors du chargement des stations", exception)
                        _uiState.value  = _uiState.value.copy(
                            isLoading   = false,
                            errorType   = ErrorType.LOADING_STATIONS
                        )
                    }
                    .collect { result ->
                        result.fold(
                            onSuccess = { stations ->
                                val processedStations = if (location != null) {
                                    // Calculer les distances
                                    stationRepository.calculateDistancesForStations(
                                        stations, location.latitude, location.longitude
                                    )
                                } else {
                                    stations
                                }

                                // Mettre à jour le statut favori de toutes les stations
                                val stationsWithFavorites = preferencesManager.updateStationsWithFavoriteStatus(processedStations)

                                _uiState.value = _uiState.value.copy(
                                    allStations     = stationsWithFavorites,
                                    filtredStations = stationsWithFavorites,
                                    isLoading       = false,
                                    errorType           = null
                                )
                                Log.d(TAG, "Stations chargees pour le filtrage: ${stationsWithFavorites.size}")
                            },
                            onFailure = { exception ->
                                Log.e(TAG, "Échec du chargement des stations", exception)
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    errorType = ErrorType.UNKNOWN
                                )
                            }
                        )
                    }
            } catch (exception: Exception) {
                Log.e(TAG, "Erreur generale dans loadData", exception)
                _uiState.value  = _uiState.value.copy(
                    isLoading   = false,
                    errorType   = ErrorType.CONNECTION
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
        _searchQuery.value = query
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
        // Ajouter la station aux stations visitees
        preferencesManager.addVisitedStation(station)
        Log.d(TAG, "Station ajoutee a l'historique: ${station.nom}")
    }

    fun retry() {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorType = null
        )
        loadData()
    }

    fun clearSearch() {
        onSearchQueryChange("")
    }
}