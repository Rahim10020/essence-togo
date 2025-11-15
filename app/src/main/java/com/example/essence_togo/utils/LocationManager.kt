package com.example.essence_togo.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

class LocationManager(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    companion object {
        private const val TAG = "LocationManager"
        private const val LOCATION_TIMEOUT_MS = 30000L // 30 secondes
    }

    // verifions si les permissions de localisation sont accordees
    fun hasLocationPermission(): Boolean {
        return ContextCompat.
        checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // recuperation de la derniere postion connue de l'utilisateur
    // retourne null si pas de permission ou de localisation disponible
    suspend fun getCurrentLocation(): Location? = withTimeoutOrNull(LOCATION_TIMEOUT_MS) {
        suspendCancellableCoroutine { continuation ->
        if (!hasLocationPermission()){
            Log.w(TAG, "Permission de localisation non accordee")
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        // Cleanup on cancellation
        continuation.invokeOnCancellation {
            Log.d(TAG, "Coroutine annulée, nettoyage du callback")
            cleanup()
        }

        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        Log.d(TAG, "Localisation obtenue ${location.latitude}, ${location.longitude}")
                        if (continuation.isActive) {
                            continuation.resume(location)
                        }
                    } else {
                        Log.w(TAG, "Aucune localisation disponible")
                        // re-demander une nouvelle localisation
                        requestNewLocation{ newLocation ->
                            if (continuation.isActive) {
                                continuation.resume(newLocation)
                            }
                        }
                    }
                }
                .addOnFailureListener{ exception ->
                    Log.e(TAG, "Erreur lors de la recuperation de la localisation", exception)
                    if (continuation.isActive) {
                        continuation.resume(null)
                    }
                }
        } catch (e: SecurityException) {
            Log.e(TAG, "Erreur de securite lors de la recuperation de la localisation", e)
            if (continuation.isActive) {
                continuation.resume(null)
            }
        }

        }
    } ?: run {
        Log.w(TAG, "Timeout lors de la récupération de la localisation")
        cleanup()
        null
    }

    // fonction pour demander une nouvelle localisation en temps reel
    private var activeCallback: LocationCallback? = null

    // Dans requestNewLocation, stockez le callback
    private fun requestNewLocation(callback: (Location?) -> Unit) {
        if (!hasLocationPermission()) {
            callback(null)
            return
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000
        ).apply {
            setMinUpdateDistanceMeters(100f)
            setMaxUpdateDelayMillis(20000)
        }.build()

        val locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                Log.d(TAG, "Nouvelle localisation obtenue ${location?.latitude}, ${location?.longitude}")
                cleanup() // Nettoyage automatique
                callback(location)
            }
        }

        activeCallback = locationCallback // Stocker pour cleanup

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        } catch (e: SecurityException){
            Log.e(TAG, "Erreur de securite lors de la demande de nouvelle localisation",e)
            callback(null)
        }
    }

    // méthode de cleanup
    fun cleanup() {
        activeCallback?.let { callback ->
            fusedLocationClient.removeLocationUpdates(callback)
            activeCallback = null
            Log.d(TAG, "LocationManager nettoyé")
        }
    }

    // coordonnees par defaut si aucune localisation n'est disponible
    fun getDefaultLocation(): Location {
        val defaultLocation = Location("default")
        defaultLocation.latitude = 6.1375 // Lomé latitude
        defaultLocation.longitude = 1.2123 // Lomé longitude
        Log.d(TAG, "Utilisation de la localisation par defaut Lome-Togo")
        return defaultLocation
    }

}