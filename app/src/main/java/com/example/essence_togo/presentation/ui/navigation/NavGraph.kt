package com.example.essence_togo.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.essence_togo.data.local.PreferencesManager
import com.example.essence_togo.data.repository.StationRepository
import com.example.essence_togo.presentation.ui.screens.filter.FilterScreen
import com.example.essence_togo.presentation.ui.screens.filter.FilterViewModel
import com.example.essence_togo.presentation.ui.screens.history.HistoryScreen
import com.example.essence_togo.presentation.ui.screens.history.HistoryViewModel
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
                    stationRepository   = stationRepository,
                    locationManager     = locationManager,
                    preferencesManager  = preferencesManager
                )
            }
            HomeScreen(
                viewModel       = viewModel,
                onStationClick  = { stationId ->
                    navController.navigate(Destination.StationDetails.createRoute(stationId))
                }
            )
        }

        // ecran de filtrage/recherche
        composable(BottomNavDestination.Filter.route){
            val viewModel: FilterViewModel = viewModel {
                FilterViewModel(
                    stationRepository   = stationRepository,
                    locationManager     = locationManager,
                    preferencesManager  = preferencesManager
                )
            }
            FilterScreen(
                viewModel       = viewModel,
                onStationClick  = { stationId ->
                    navController.navigate(Destination.StationDetails.createRoute(stationId))
                }
            )
        }

        // ecran pour l'historique
        composable(BottomNavDestination.History.route) {
            val viewModel: HistoryViewModel = viewModel {
                HistoryViewModel(
                    preferencesManager = preferencesManager
                )
            }
            HistoryScreen(
                viewModel = viewModel,
                onStationClick = {stationId ->
                    navController.navigate(Destination.StationDetails.createRoute(stationId))
                }
            )
        }
    }
}