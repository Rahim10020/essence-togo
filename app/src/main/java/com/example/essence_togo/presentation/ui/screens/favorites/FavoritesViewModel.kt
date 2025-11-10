package com.example.essence_togo.presentation.ui.screens.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.essence_togo.data.local.PreferencesManager
import com.example.essence_togo.data.model.Station
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val favoriteStations: List<Station> = emptyList(),
    val isLoading: Boolean = true
)

class FavoritesViewModel(
    private val preferencesManager: PreferencesManager
): ViewModel() {
    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "FavoritesViewModel"
    }

    init {
        observeFavoriteStations()
    }

    private fun observeFavoriteStations() {
        viewModelScope.launch {
            preferencesManager.favoriteStations
                .collect { stations ->
                    _uiState.value = _uiState.value.copy(
                        favoriteStations = stations,
                        isLoading = false
                    )
                    Log.d(TAG, "Stations favorites mise a jour: ${stations.size}")
                }
        }
    }

    fun toggleFavorite(station: Station) {
        preferencesManager.toggleFavoriteStation(station)
        Log.d(TAG, "Statut favori basculé pour: ${station.nom}")
    }

    fun clearAllFavorites() {
        viewModelScope.launch {
            preferencesManager.clearFavoriteStations()
            Log.d(TAG, "Toutes les stations favorites effacées")
        }
    }

    fun onStationClick(station: Station) {
        // Ajouter la station à l'historique des visites
        preferencesManager.addVisitedStation(station)
        Log.d(TAG, "Station ajoutée à l'historique: ${station.nom}")
    }
}