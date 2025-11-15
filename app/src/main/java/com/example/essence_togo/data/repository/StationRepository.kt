package com.example.essence_togo.data.repository

import android.util.Log
import com.example.essence_togo.data.local.CacheManager
import com.example.essence_togo.data.model.Station
import com.example.essence_togo.utils.NetworkManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class StationRepository(
    private val cacheManager: CacheManager,
    private val networkManager: NetworkManager
) {
    private val database        = FirebaseDatabase.getInstance()
    private val stationsRef     = database.getReference("stations")
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val TAG = "StationRepository"
    }

    /**
     * Récupère les stations depuis Firebase ou depuis le cache si offline
     * Retourne un Flow pour une observation continue des données
     */
    fun getAllStations(): Flow<Result<List<Station>>> = callbackFlow {
        // Vérifier la connexion internet
        val isOnline = networkManager.isNetworkAvailable()

        if (!isOnline) {
            Log.w(TAG, "Pas de connexion internet, utilisation du cache")
            // Mode offline : utiliser le cache
            repositoryScope.launch {
                val cachedStations = cacheManager.getCachedStations()
                if (cachedStations != null && cachedStations.isNotEmpty()) {
                    Log.d(TAG, "Stations chargées depuis le cache: ${cachedStations.size}")
                    trySend(Result.success(cachedStations))
                } else {
                    Log.e(TAG, "Aucune donnée en cache disponible")
                    trySend(Result.failure(Exception("Pas de connexion et aucune donnée en cache")))
                }
            }
            // Ne pas fermer le flow, au cas où la connexion revient
            awaitClose { }
            return@callbackFlow
        }

        // Mode online : charger depuis Firebase
        Log.d(TAG, "Connexion internet disponible, chargement depuis Firebase")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val stations = mutableListOf<Station>()
                for (dataSnapshot in snapshot.children) {
                    try {
                        val station = dataSnapshot.getValue(Station::class.java)
                        station?.let {
                            stations.add(it)
                            Log.d(TAG, "Station chargée : ${it.nom} - ID ${it.id}")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Erreur lors du parsing de la station", e)
                    }
                }
                Log.d(TAG, "Total stations chargées depuis Firebase: ${stations.size}")

                // Mettre à jour le cache avec les nouvelles données
                if (stations.isNotEmpty()) {
                    repositoryScope.launch {
                        cacheManager.cacheStations(stations)
                        Log.d(TAG, "Cache mis à jour avec ${stations.size} stations")
                    }
                }

                trySend(Result.success(stations))
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Erreur Firebase : ${error.message}")

                // En cas d'erreur Firebase, essayer d'utiliser le cache
                repositoryScope.launch {
                    val cachedStations = cacheManager.getCachedStations()
                    if (cachedStations != null && cachedStations.isNotEmpty()) {
                        Log.d(TAG, "Erreur Firebase, utilisation du cache de secours")
                        trySend(Result.success(cachedStations))
                    } else {
                        trySend(Result.failure(error.toException()))
                    }
                }
            }
        }

        stationsRef.addValueEventListener(listener)
        awaitClose { stationsRef.removeEventListener(listener) }
    }

    /**
     * Récupère une station par son id (avec cache)
     */
    suspend fun getStationById(id: Int): Station? {
        return try {
            // Essayer d'abord le cache
            val cachedStations = cacheManager.getCachedStations()
            cachedStations?.find { it.id == id }
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de la récupération de la station ID $id", e)
            null
        }
    }

    /**
     * Filtrage des stations par noms ou par adresses
     */
    fun filterStations(stations: List<Station>, query: String): List<Station> {
        if (query.isBlank()) return stations
        return stations.filter { station ->
            station.nom.contains(query, ignoreCase = true) ||
                    station.address.contains(query, ignoreCase = true)
        }
    }

    /**
     * Tri des stations par distance
     */
    fun sortStationsByDistance(stations: List<Station>): List<Station> {
        return stations.sortedBy { it.distance }
    }

    /**
     * Calcul de la distance pour toutes les stations
     */
    fun calculateDistancesForStations(
        stations: List<Station>,
        userLat: Double,
        userLong: Double
    ): List<Station> {
        return stations.map { station -> station.withDistance(userLat, userLong) }
    }

    /**
     * Vérifie si des données en cache sont disponibles
     */
    fun hasCachedData(): Boolean {
        return cacheManager.hasCachedData()
    }

    /**
     * Efface le cache des stations
     */
    suspend fun clearCache() {
        cacheManager.clearCache()
    }
}