package com.example.essence_togo.presentation.ui.screens.home

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.essence_togo.data.local.PreferencesManager
import com.example.essence_togo.data.model.Station
import com.example.essence_togo.data.repository.StationRepository
import com.example.essence_togo.utils.LocationManager
import com.example.essence_togo.utils.NetworkManager
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
    val isOffline: Boolean          = false  // Nouveau champ
)

class HomeViewModel(
    private val stationRepository: StationRepository,
    private val locationManager: LocationManager,
    private val preferencesManager: PreferencesManager,
    private val networkManager: NetworkManager
): ViewModel() {
    private val _uiState                = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "HomeViewModel"
    }

    init {
        observeNetworkState()
        loadData()
    }

    /**
     * Observer l'état de la connexion réseau
     */
    private fun observeNetworkState() {
        viewModelScope.launch {
            networkManager.observeNetworkState()
                .collect { isOnline ->
                    _uiState.value = _uiState.value.copy(isOffline = !isOnline)
                    Log.d(TAG, "État réseau: ${if (isOnline) "En ligne" else "Hors ligne"}")

                    // Recharger les données quand la connexion revient
                    if (isOnline && _uiState.value.error != null) {
                        Log.d(TAG, "Connexion rétablie, rechargement des données")
                        loadData()
                    }
                }
        }
    }

    private fun loadData(){
        viewModelScope.launch {
            try {
                // recuperation de la localisation de l'utilisateur
                val location    = getUserLocation()
                _uiState.value  = _uiState.value.copy(userLocation = location)

                // observer les stations depuis firebase ou cache
                stationRepository.getAllStations()
                    .catch { exception ->
                        Log.e(TAG, "Erreur lors du chargement des stations", exception)
                        _uiState.value  = _uiState.value.copy(
                            isLoading   = false,
                            error       = if (_uiState.value.isOffline)
                                "Pas de connexion et aucune donnée en cache"
                            else
                                "Erreur lors du chargement des stations"
                        )
                    }
                    .collect { result ->
                        result.fold(
                            onSuccess = { stations ->
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

                                // Mettre à jour le statut favori de toutes les stations
                                val stationsWithFavorites = preferencesManager.updateStationsWithFavoriteStatus(processedStations)

                                // mise a jour des stations visitees avec les details
                                preferencesManager.updateVisitedStationsWithDetails(stationsWithFavorites)

                                // mise à jour des stations favorites avec les détails
                                preferencesManager.updateFavoriteStationsWithDetails(stationsWithFavorites)

                                _uiState.value  = _uiState.value.copy(
                                    stations    = stationsWithFavorites,
                                    isLoading   = false,
                                    error       = null,
                                )
                                Log.d(TAG, "Stations chargées: ${stationsWithFavorites.size} (Offline: ${_uiState.value.isOffline})")
                            },
                            onFailure = { exception ->
                                Log.e(TAG, "Échec du chargement", exception)
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    error = exception.message ?: "Erreur inconnue"
                                )
                            }
                        )
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

    // fonction pour obtenir la localisation d'un utilisateur
    private suspend fun getUserLocation(): Location? {
        return try {
            if (locationManager.hasLocationPermission()) {
                locationManager.getCurrentLocation() ?: locationManager.getDefaultLocation()
            } else {
                Log.w(TAG, "Permission de localisation non accordee, utilisation de la position par defaut")
                locationManager.getDefaultLocation()
            }
        } catch(exception: Exception) {
            Log.e(TAG, "Erreur lors de la recuperation de la localisation", exception)
            locationManager.getDefaultLocation()
        }
    }

    fun onStationClick(station: Station){
        // ajouter la station aux stations visitees
        preferencesManager.addVisitedStation(station)
        Log.d(TAG, "Station ajoutee a l'historique ${station.nom}")
    }

    fun toggleFavorite(station: Station) {
        preferencesManager.toggleFavoriteStation(station)
        Log.d(TAG, "Statut favori basculé pour: ${station.nom}")
    }

    fun retry() {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null
        )
        loadData()
    }
}