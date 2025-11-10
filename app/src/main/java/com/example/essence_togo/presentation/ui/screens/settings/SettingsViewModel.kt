package com.example.essence_togo.presentation.ui.screens.settings


import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.essence_togo.utils.Language
import com.example.essence_togo.utils.LocaleManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val currentLanguage: String = LocaleManager.FRENCH,
    val availableLanguages: List<Language> = emptyList(),
    val showLanguageDialog: Boolean = false
)

class SettingsViewModel(
    private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "SettingsViewModel"
    }

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val currentLanguage = LocaleManager.getSavedLanguage(context)
            val availableLanguages = LocaleManager.getAvailableLanguages()

            _uiState.value = _uiState.value.copy(
                currentLanguage = currentLanguage,
                availableLanguages = availableLanguages
            )

            Log.d(TAG, "Settings loaded. Current language: $currentLanguage")
        }
    }

    fun showLanguageDialog() {
        _uiState.value = _uiState.value.copy(showLanguageDialog = true)
    }

    fun hideLanguageDialog() {
        _uiState.value = _uiState.value.copy(showLanguageDialog = false)
    }

    fun changeLanguage(languageCode: String) {
        viewModelScope.launch {
            LocaleManager.setLocale(context, languageCode)

            _uiState.value = _uiState.value.copy(
                currentLanguage = languageCode,
                showLanguageDialog = false
            )

            Log.d(TAG, "Language changed to: ${LocaleManager.getLanguageName(languageCode)}")
        }
    }

    fun getCurrentLanguageName(): String {
        return LocaleManager.getLanguageName(_uiState.value.currentLanguage)
    }
}