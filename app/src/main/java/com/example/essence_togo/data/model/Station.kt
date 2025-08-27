package com.example.essence_togo.data.model

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class Station(
    val id: Int             = 0,
    val nom: String         = "",
    val imageUrl: String    = "",
    val address: String     = "",
    val latitude: Double    = 0.0,
    val longitude: Double   = 0.0,
    var distance: Double    = 0.0
){
    // constructeur sans parametre pour firebase.
    constructor() : this(0,"","","",0.0,0.0,0.0)

    // calcul de la distance entre la station et la position de l'utilisateur
    // methode Haversine
    private fun calculateDistance(userLat: Double, userLong: Double): Double {
        // rayon de la terre en km
        val earthRadius     = 6371.0

        val latDiff         = Math.toRadians(userLat - latitude)
        val longDiff        = Math.toRadians(userLong - longitude)

        val a               = sin(latDiff /2).pow(2.0) +
                    cos(Math.toRadians(latitude)) * cos(Math.toRadians(userLat)) *
                    sin(longDiff / 2).pow(2.0)

        val c               = 2 * atan2(sqrt(a), sqrt(1-a))
        distance            = earthRadius * c
        return distance
    }

    // retourner une copie de la station avec la distance calculee
    fun withDistance(userLat: Double, userLong: Double): Station {
        return copy(distance = calculateDistance(userLat, userLong))
    }

    // formattage de la distance pour l'affichage
    fun getFormattedDistance(): String {
        return String.format("%.2f km" ,distance)
    }
}
