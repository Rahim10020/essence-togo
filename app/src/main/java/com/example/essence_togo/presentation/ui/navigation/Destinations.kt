package com.example.essence_togo.presentation.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

// Destinations principales de l'application (Bottom Navigation)
sealed class BottomNavDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Home: BottomNavDestination(
        route = "home",
        title = "Acceuil",
        icon = Icons.Default.Home
    )

    data object Filter: BottomNavDestination(
        route = "filter",
        title = "Filtrer",
        icon = Icons.Default.FilterList
    )

    data object History: BottomNavDestination(
        route = "history",
        title = "Historique",
        icon = Icons.Default.History
    )
}

// autres destinations
sealed class Destination(val route: String) {
    data object StationDetails: Destination("station_details/{stationId}") {
        fun createRoute(stationId: Int) = "station_details/$stationId"
    }
}

// liste de toutes les destinations de la bottomNavigation
val bottomNavDestinations = listOf(
    BottomNavDestination.Home,
    BottomNavDestination.Filter,
    BottomNavDestination.History
)

object NavArgs {
    const val STATION_ID = "stationId"
}