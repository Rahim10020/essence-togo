package com.example.essence_togo.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.essence_togo.data.local.CacheManager
import com.example.essence_togo.data.local.PreferencesManager
import com.example.essence_togo.data.repository.StationRepository
import com.example.essence_togo.presentation.ui.screens.details.StationDetailScreen
import com.example.essence_togo.presentation.ui.screens.details.StationDetailsViewModel
import com.example.essence_togo.presentation.ui.screens.favorites.FavoritesScreen
import com.example.essence_togo.presentation.ui.screens.favorites.FavoritesViewModel
import com.example.essence_togo.presentation.ui.screens.filter.FilterScreen
import com.example.essence_togo.presentation.ui.screens.filter.FilterViewModel
import com.example.essence_togo.presentation.ui.screens.history.HistoryScreen
import com.example.essence_togo.presentation.ui.screens.history.HistoryViewModel
import com.example.essence_togo.presentation.ui.screens.home.HomeScreen
import com.example.essence_togo.presentation.ui.screens.home.HomeViewModel
import com.example.essence_togo.presentation.ui.screens.settings.SettingsScreen
import com.example.essence_togo.presentation.ui.screens.settings.SettingsViewModel
import com.example.essence_togo.utils.LocationManager
import com.example.essence_togo.utils.NetworkManager

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = BottomNavDestination.Home.route,
){

    val context             = LocalContext.current
    val networkManager      = NetworkManager(context)
    val cacheManager        = CacheManager(context)
    val stationRepository   = StationRepository(cacheManager, networkManager)
    val preferencesManager  = PreferencesManager(context)
    val locationManager     = LocationManager(context)

    NavHost(navController = navController, startDestination = startDestination) {
        // ecran d'acceuil
        composable(BottomNavDestination.Home.route) {
            val viewModel: HomeViewModel = viewModel {
                HomeViewModel(
                    stationRepository   = stationRepository,
                    locationManager     = locationManager,
                    preferencesManager  = preferencesManager,
                    networkManager      = networkManager
                )
            }
            HomeScreen(
                viewModel       = viewModel,
                onStationClick  = { stationId ->
                    navController.navigate(Destination.StationDetails.createRoute(stationId))
                },
                onSettingsClick = {
                    navController.navigate(Destination.Settings.route)
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
                },
                onSettingsClick = {
                    navController.navigate(Destination.Settings.route)
                }
            )
        }

        // ecran des favoris
        composable(BottomNavDestination.Favorites.route) {
            val viewModel: FavoritesViewModel = viewModel {
                FavoritesViewModel(
                    preferencesManager = preferencesManager
                )
            }
            FavoritesScreen(
                viewModel = viewModel,
                onStationClick = { stationId ->
                    navController.navigate(Destination.StationDetails.createRoute(stationId))
                },
                onSettingsClick = {
                    navController.navigate(Destination.Settings.route)
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
                },
                onSettingsClick = {
                    navController.navigate(Destination.Settings.route)
                }
            )
        }

        // ecran des parametres (pas dans bottom nav)
        composable(Destination.Settings.route) {
            val viewModel: SettingsViewModel = viewModel {
                SettingsViewModel(context = context)
            }
            SettingsScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // ecran de detail d'une station
        composable(
            route = Destination.StationDetails.route,
            arguments = listOf(
                navArgument(NavArgs.STATION_ID) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val stationId = backStackEntry.arguments?.getInt(NavArgs.STATION_ID) ?: 0
            val viewModel: StationDetailsViewModel = viewModel {
                StationDetailsViewModel(
                    stationRepository = stationRepository,
                    stationId = stationId,
                    preferencesManager = preferencesManager
                )
            }
            StationDetailScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}