package com.example.essence_togo.presentation.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.essence_togo.presentation.ui.components.EmptyState
import com.example.essence_togo.presentation.ui.components.ErrorState
import com.example.essence_togo.presentation.ui.components.LoadingIndicator

@Composable
@Preview
fun HomeScreenPreview(){
    //HomeScreen()
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStationClick: (Int) -> Unit,
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Header avec titre
        Surface(
            color           = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text        = "EssenceTogo",
                    style       = MaterialTheme.typography.headlineLarge,
                    fontWeight  = FontWeight.Bold,
                    color       = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text    = "Stations les plus proches",
                    style   = MaterialTheme.typography.titleMedium,
                    color   = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        // contenu principal
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    LoadingIndicator(
                        modifier    = Modifier.align(Alignment.Center),
                        message     = "Recuperation de votre position et des stations"
                    )
                }
                uiState.error != null -> {
                    ErrorState(
                        title       = "Oops !",
                        subtitle    = uiState.error!!,
                        onRetry     = {viewModel.retry()},
                        modifier    = Modifier.align(Alignment.Center),
                    )
                }
                uiState.stations.isEmpty() -> {
                    EmptyState(
                        title       = "Aucune station trouvee",
                        subtitle    = "Verifiez votre connexion internet",
                        modifier    = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                }
            }
        }
    }
}

@Composable
private fun StationsList(
    
){}