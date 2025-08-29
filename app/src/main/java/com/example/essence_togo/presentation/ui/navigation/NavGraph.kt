package com.example.essence_togo.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.essence_togo.data.local.PreferencesManager
import com.example.essence_togo.data.repository.StationRepository
import com.example.essence_togo.presentation.ui.screens.home.HomeScreen
import com.example.essence_togo.presentation.ui.screens.home.HomeViewModel
import com.example.essence_togo.utils.LocationManager

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = BottomNavDestination.Home.route,
){

    val context             = LocalContext.current
    val stationRepository   = StationRepository()
    val preferencesManager  = PreferencesManager(context)
    val locationManager     = LocationManager(context)

    NavHost(navController = navController, startDestination = startDestination) {
        // ecran d'acceuil
        composable(BottomNavDestination.Home.route) {
            val viewModel: HomeViewModel = viewModel {
                HomeViewModel(
                    stationRepository = stationRepository,
                    locationManager = locationManager,
                    preferencesManager = preferencesManager
                )
            }
            HomeScreen()
        }
    }
}