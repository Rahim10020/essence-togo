package com.example.essence_togo

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.essence_togo.presentation.ui.navigation.BottomNavDestination
import com.example.essence_togo.presentation.ui.navigation.NavGraph
import com.example.essence_togo.presentation.ui.navigation.bottomNavDestinations
import com.example.essence_togo.presentation.ui.theme.EssenceTogoTheme
import com.example.essence_togo.utils.LocaleManager
import com.example.essence_togo.utils.LocationManager

class MainActivity : ComponentActivity() {
    private lateinit var locationManager: LocationManager

    // Gestionnaire des permissions
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // permission precise accordee
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // permission approximative accordee
            }
            else -> {
                // aucune permission accordee, rediriger vers parametres
                showPermissionSettingsDialog()
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        // Appliquer la langue sauvegardée avant de créer l'activité
        super.attachBaseContext(LocaleManager.applySavedLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationManager = LocationManager(this)

        // demander les permissions de localisation au demarrage
        requestLocationPermissions()
        setContent {
            EssenceTogoTheme {
                EssenceTogoApp()
            }
        }
    }

    private fun requestLocationPermissions() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun showPermissionSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_required_title))
            .setMessage(getString(R.string.permission_location_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EssenceTogoApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // determiner si on doit afficher la bottom navigation
    val showBottomNav = currentRoute in bottomNavDestinations.map { it.route }
    Scaffold(
        modifier    = Modifier.fillMaxSize(),
        bottomBar   = {
            if (showBottomNav) {
                NavigationBar {
                    bottomNavDestinations.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.title
                                )
                            },
                            label = {
                                Text(text = destination.title)
                            },
                            onClick = {
                                if (currentRoute != destination.route) {
                                    navController.navigate(destination.route) {
                                        // eviter l'accumulation des destinations dans la pile
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            startDestination = BottomNavDestination.Home.route
        )
    }
}