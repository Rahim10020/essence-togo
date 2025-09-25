package com.example.essence_togo.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.example.essence_togo.data.model.Station
import java.text.SimpleDateFormat
import java.util.*

/**
 * Extensions pour les chaînes de caractères
 */
fun String.capitalizeWords(): String {
    return this.lowercase().split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}

fun String.isValidCoordinate(): Boolean {
    return try {
        val value = this.toDouble()
        value in -180.0..180.0
    } catch (e: NumberFormatException) {
        false
    }
}

/**
 * Extensions pour les nombres
 */
@SuppressLint("DefaultLocale")
fun Double.formatDistance(): String {
    return String.format("%.2f km", this)
}

@SuppressLint("DefaultLocale")
fun Double.formatCoordinate(): String {
    return String.format("%.6f", this)
}

/**
 * Extensions pour les dates
 */
fun Long.formatTimestamp(): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return dateFormat.format(Date(this))
}

/**
 * Extensions pour le contexte Android
 */
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.openGoogleMaps(station: Station) {
    try {
        val geoUri = "geo:${station.latitude},${station.longitude}?q=${station.latitude},${station.longitude}(${station.nom})"
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        } else {
            val genericMapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
            if (genericMapIntent.resolveActivity(packageManager) != null) {
                startActivity(genericMapIntent)
            } else {
                showToast(Constants.ErrorMessages.NO_NAVIGATION_APP)
            }
        }
    } catch (e: Exception) {
        showToast("Erreur lors de l'ouverture de la navigation")
    }
}

fun Context.shareStation(station: Station) {
    val shareText = buildString {
        appendLine("Découvrez cette station-service !")
        appendLine("📍 ${station.nom}")
        appendLine("📍 ${station.address}")
        if (station.distance > 0) {
            appendLine("🚗 Distance: ${station.getFormattedDistance()}")
        }
        appendLine()
        appendLine("Coordonnées: ${station.latitude.formatCoordinate()}, ${station.longitude.formatCoordinate()}")
        appendLine()
        appendLine("Partagé depuis EssenceTogo 🇹🇬")
    }

    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
        putExtra(Intent.EXTRA_SUBJECT, "Station-service: ${station.nom}")
    }

    startActivity(Intent.createChooser(shareIntent, "Partager la station"))
}

/**
 * Extensions pour les listes
 */
fun List<Station>.filterByQuery(query: String): List<Station> {
    if (query.isBlank()) return this

    return filter { station ->
        station.nom.contains(query, ignoreCase = true) ||
                station.address.contains(query, ignoreCase = true)
    }
}

fun List<Station>.sortByDistance(): List<Station> {
    return sortedBy { it.distance }
}

fun List<Station>.sortByName(): List<Station> {
    return sortedBy { it.nom }
}

/**
 * Extensions pour Compose
 */
fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = composed {
    this.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
}

/**
 * Extensions pour Station
 */
fun Station.hasValidCoordinates(): Boolean {
    return latitude != 0.0 && longitude != 0.0 &&
            latitude in -90.0..90.0 && longitude in -180.0..180.0
}

fun Station.getShareableText(): String {
    return buildString {
        appendLine("📍 $nom")
        appendLine("📍 $address")
        if (distance > 0) {
            appendLine("🚗 ${distance.formatDistance()}")
        }
        if (hasValidCoordinates()) {
            appendLine("🌐 ${latitude.formatCoordinate()}, ${longitude.formatCoordinate()}")
        }
    }
}

/**
 * Extensions pour la validation des données
 */
fun Station.isValid(): Boolean {
    return id > 0 &&
            nom.isNotBlank() &&
            address.isNotBlank() &&
            hasValidCoordinates()
}

/**
 * Extensions utilitaires pour le logging
 */
fun Any.logTag(): String {
    return this::class.simpleName ?: "UnknownClass"
}

/**
 * Extensions pour les collections
 */
fun <T> List<T>.safeGet(index: Int): T? {
    return if (index in 0 until size) get(index) else null
}

fun <T> MutableList<T>.addIfNotExists(item: T): Boolean {
    return if (!contains(item)) {
        add(item)
        true
    } else {
        false
    }
}