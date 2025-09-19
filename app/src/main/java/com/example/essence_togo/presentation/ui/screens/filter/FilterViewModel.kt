package com.example.essence_togo.presentation.ui.screens.filter

import android.location.Location
import androidx.lifecycle.ViewModel
import com.example.essence_togo.data.local.PreferencesManager
import com.example.essence_togo.data.model.Station
import com.example.essence_togo.data.repository.StationRepository
import com.example.essence_togo.utils.LocationManager

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

}