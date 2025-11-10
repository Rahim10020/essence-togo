package com.example.essence_togo.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LocaleManager {
    private const val PREF_NAME = "locale_settings"
    private const val KEY_LANGUAGE = "selected_language"

    // Codes de langue supportés
    const val FRENCH = "fr"
    const val ENGLISH = "en"
    const val EWE = "ee"  // Code ISO 639-1 pour Ewe

    /**
     * Sauvegarde la langue sélectionnée
     */
    fun saveLanguage(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    /**
     * Récupère la langue sauvegardée (par défaut: français)
     */
    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, FRENCH) ?: FRENCH
    }

    /**
     * Applique la langue à l'application
     */
    fun setLocale(context: Context, languageCode: String): Context {
        saveLanguage(context, languageCode)

        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context
        }
    }

    /**
     * Applique la langue sauvegardée au démarrage
     */
    fun applySavedLocale(context: Context): Context {
        val savedLanguage = getSavedLanguage(context)
        return setLocale(context, savedLanguage)
    }

    /**
     * Obtient le nom de la langue pour l'affichage
     */
    fun getLanguageName(languageCode: String): String {
        return when (languageCode) {
            FRENCH -> "Français"
            ENGLISH -> "English"
            EWE -> "Eʋegbe"
            else -> "Français"
        }
    }

    /**
     * Liste de toutes les langues disponibles
     */
    fun getAvailableLanguages(): List<Language> {
        return listOf(
            Language(FRENCH, "Français", "🇫🇷"),
            Language(ENGLISH, "English", "🇬🇧"),
            Language(EWE, "Eʋegbe", "🇹🇬")
        )
    }
}

/**
 * Classe de données pour représenter une langue
 */
data class Language(
    val code: String,
    val name: String,
    val flag: String
)
