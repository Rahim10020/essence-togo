package com.example.essence_togo.presentation.ui.screens.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.essence_togo.data.local.PreferencesManager
import com.example.essence_togo.data.model.Station
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HistoryUiState(
    val visitedStations: List<Station> = emptyList(),
    val isLoading: Boolean = true
)

class HistoryViewModel(
    private val preferencesManager: PreferencesManager
): ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "HistoryViewModel"
    }

    init {
        observeVisitedStations()
    }

    private fun observeVisitedStations() {
        viewModelScope.launch {
            preferencesManager.visitedStations
                .collect{ stations ->
                    _uiState.value  = _uiState.value.copy(
                        visitedStations = stations,
                        isLoading = false
                    )
                    Log.d(TAG, "Stations visitees mise a jour :${stations.size}")
                }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            preferencesManager.clearVisitedStations()
            Log.d(TAG, "Historique des stations visitees efface")
        }
    }

    fun onStationClick(station: Station) {
        // Remettre la station en haut de la liste (en la re-visitant)
        preferencesManager.addVisitedStation(station)
        Log.d(TAG, "Station remise en haut de l'historique ${station.nom}")
    }
}