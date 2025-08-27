package com.example.essence_togo.data.repository

import android.util.Log
import com.example.essence_togo.data.model.Station
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class StationRepository {
    private val database        = FirebaseDatabase.getInstance()
    private val stationsRef    = database.getReference("stations")

    companion object {
        private const val TAG = "StationRepository"
    }

    // recuperation de toutes les stations depuis firebase en temps reel
    // retourner un Flow pour une observation continue des donnees
    fun getAllStations(): Flow<List<Station>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val stations = mutableListOf<Station>()
                for (dataSnapshot in snapshot.children) {
                    try {
                        val station = dataSnapshot.getValue(Station::class.java)
                        station?.let {
                            stations.add(it)
                            Log.d(TAG, "station chargee : ${it.nom} - ID ${it.id}")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Erreur lors du parsing de la station",e)
                    }
                }
                Log.d(TAG, "Total stations chargees: ${stations.size}")
                trySend(stations)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Erreur firebase : ${error.message}")
                close(error.toException())
            }
        }
        stationsRef.addValueEventListener(listener)
        awaitClose { stationsRef.removeEventListener(listener) }
    }

    // recuperation d'une station par son id
    fun getStationById(id: Int): Station? {
        return try {
            val snapshot = stationsRef.orderByChild("id").equalTo(id.toDouble()).get()
            if (snapshot.result.exists()) {
                val dataSnapshot = snapshot.result.children.first()
                dataSnapshot.getValue(Station::class.java)
            } else {null}

        } catch (e: Exception) {
            Log.e(TAG, "Erreur los de la recuperation de la station ID $id",e)
            null
        }
    }

    // filtrage des stations par noms ou par addresses
    fun filterStations(stations: List<Station>, query: String): List<Station> {
        if (query.isBlank()) return stations
        return stations.filter { station ->
            station.nom.contains(query, ignoreCase = true) ||
                    station.address.contains(query, ignoreCase = true)
        }
    }

    // trie des stations par distance
    fun sortStationsByDistance(stations: List<Station>): List<Station> {
        return stations.sortedBy { it.distance }
    }

    // calcule de la distance pour toutes les stations
    fun calculateDistancesForStations(
        stations: List<Station>,
        userLat: Double,
        userLong: Double
    ): List<Station> {
        return stations.map { station -> station.withDistance(userLat, userLong) }

    }
}