package com.example.essence_togo.utils


// Constantes utilisees dans l'application

object Constants {
    // Firebase
    const val FIREBASE_STATIONS_PATH = "stations"

    // Localisation
    const val DEFAULT_LATITUDE = 6.1375 // Lomé, Togo
    const val DEFAULT_LONGITUDE = 1.2123 // Lomé, Togo
    const val LOCATION_UPDATE_INTERVAL = 10000L // 10 secondes
    const val LOCATION_FASTEST_INTERVAL = 5000L // 5 secondes
    const val LOCATION_MIN_DISTANCE = 100f // 100 mètres

    // UI
    const val ANIMATION_DURATION_SHORT = 300
    const val ANIMATION_DURATION_MEDIUM = 500
    const val ANIMATION_DURATION_LONG = 700

    // Historique
    const val MAX_VISITED_STATIONS = 50

    // Images
    const val IMAGE_LOADING_PLACEHOLDER = "https://via.placeholder.com/300x200/0C67AD/FFFFFF?text=Station"

    // Messages d'erreur
    object ErrorMessages {
        const val NETWORK_ERROR = "Vérifiez votre connexion internet"
        const val LOCATION_PERMISSION_DENIED = "Permission de localisation refusée"
        const val STATION_NOT_FOUND = "Station introuvable"
        const val GENERIC_ERROR = "Une erreur est survenue"
        const val NO_NAVIGATION_APP = "Aucune application de navigation disponible"
    }

    // Messages de succès
    object SuccessMessages {
        const val LOCATION_OBTAINED = "Position obtenue"
        const val STATION_ADDED_TO_HISTORY = "Station ajoutée à l'historique"
        const val HISTORY_CLEARED = "Historique vidé"
    }

}