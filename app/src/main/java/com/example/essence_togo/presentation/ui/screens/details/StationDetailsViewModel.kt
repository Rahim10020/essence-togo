package com.example.essence_togo.presentation.ui.screens.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.essence_togo.data.model.Station
import com.example.essence_togo.data.repository.StationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


data class StationDetailUiState(
    val station: Station?   = null,
    val isLoading: Boolean  = true,
    val error: String?      = null
)


class StationDetailsViewModel(
    private val stationRepository: StationRepository,
    private val stationId: Int,
): ViewModel() {
    private val _uiState = MutableStateFlow(StationDetailUiState())
    val uiState: StateFlow<StationDetailUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "StationDetailsViewModel"
    }

    init {
        loadStationDetails()
    }

    private fun loadStationDetails() {
        viewModelScope.launch {
            try {
                _uiState.value  = _uiState.value.copy(
                    isLoading   = true,
                    error       = null
                )

                // on observe toutes les stations pour trouver celle avec l'id correrspondant
                stationRepository.getAllStations()
                    .catch { exception ->
                        Log.e(TAG, "Erreur lors du chargement de la station", exception)
                        _uiState.value  = _uiState.value.copy(
                            isLoading   = false,
                            error       = "Erreur lors du chargement des details"
                        )
                    }
                    .collect { stations ->
                        val station = stations.find { it.id == stationId}
                        if (station != null) {
                            _uiState.value = _uiState.value.copy(
                                station     = station,
                                isLoading   = false,
                                error       = null
                            )
                            Log.d(TAG, "Station trouvee: ${station.nom}")
                        } else {
                            _uiState.value  = _uiState.value.copy(
                                isLoading   = false,
                                error       = "Station inrouvable"
                            )
                            Log.e(TAG, "Station avec ID $stationId introuvable")
                        }
                    }
            } catch (exception: Exception) {
                Log.e(TAG, "Erreur generale dans loadStationDetails", exception)
                _uiState.value  = _uiState.value.copy(
                    isLoading   = false,
                    error       = "Erreur de connexion"
                )
            }
        }
    }

    fun retry() {
        loadStationDetails()
    }
}