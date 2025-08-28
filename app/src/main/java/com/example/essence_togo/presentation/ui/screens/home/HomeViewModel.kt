package com.example.essence_togo.presentation.ui.screens.home

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

// classe servant a representer l'etat de l'ui
data class HomeUiState(
    val stations: List<Station>     = emptyList(),
    val isLoading: Boolean          = true,
    val error: String?              = null,
    val userLocation: Location?     = null,
)


class HomeViewModel(
    private val stationRepository: StationRepository,
    private val locationManager: LocationManager,
    private val preferencesManager: PreferencesManager
): ViewModel() {
    private val _uiState                = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "HomeViewModel"
    }

    init {
        loadData()
    }

    private fun loadData(){
        viewModelScope.launch {
            try {
                // recuperation de la localisation de l'utilisateur
                val location    = getUserLocation()
                _uiState.value  = _uiState.value.copy(userLocation = location)
                // observer les stations depuis firebase
                stationRepository.getAllStations()
                    .catch { exception ->
                        Log.e(TAG, "Erreur lors du chargement des stations", exception)
                        _uiState.value  = _uiState.value.copy(
                            isLoading   = false,
                            error       = "Erreur lors du chargement des stations"
                        )
                    }
                    .collect{stations ->
                        val processedStations = if (location != null) {
                            // calculer les distances et trier par proximite
                            stationRepository.calculateDistancesForStations(
                                stations,
                                location.latitude,
                                location.longitude
                            ).let { stationsWithDistance ->
                                stationRepository.sortStationsByDistance(stationsWithDistance)
                            }
                        } else {
                            stations
                        }
                        // mise a jour des stations visitees avec les details
                        preferencesManager.updateVisitedStationsWithDetails(stations)

                        _uiState.value  = _uiState.value.copy(
                            stations    = processedStations,
                            isLoading   = false,
                            error       =  null,
                        )
                        Log.d(TAG, "Stations chargees et triees ${processedStations.size}")
                    }
            } catch (exception: Exception) {
                Log.e(TAG, "Ereur generale dans loadDate", exception)
                _uiState.value  = _uiState.value.copy(
                    isLoading   = false,
                    error       = "Erreur de connexion"
                )
            }
        }
    }

    // fonction pour obternir la localisation d'un utilisateur
    private suspend fun getUserLocation(): Location? {
        return try {
            if (locationManager.hasLocationPermission()) {
                locationManager.getCurrentLocation() ?: locationManager.getDefaultLocation()
            } else {
                Log.w(TAG, "Permission de localisation non accordee, utilisation de la position par defaut")
                locationManager.getDefaultLocation()
            }
        } catch(exception: Exception) {
            Log.e(TAG, "Errur lors de la recuperation de la localisation", exception)
            locationManager.getDefaultLocation()
        }
    }
}