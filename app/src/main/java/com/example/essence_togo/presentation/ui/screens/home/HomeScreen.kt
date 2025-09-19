package com.example.essence_togo.presentation.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.essence_togo.data.model.Station
import com.example.essence_togo.presentation.ui.components.EmptyState
import com.example.essence_togo.presentation.ui.components.ErrorState
import com.example.essence_togo.presentation.ui.components.LoadingIndicator
import com.example.essence_togo.presentation.ui.components.StationCard

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
                    StationsList(
                        stations   = uiState.stations,
                        onStationClick      = { station ->
                            viewModel.onStationClick(station)
                            onStationClick(station.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StationsList(
    stations: List<Station>,
    onStationClick: (Station) -> Unit
){
    LazyColumn {
        // afficher un indicateur du nombre de stations
        item {
            Card(
                modifier    = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors      = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier            = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment   = Alignment.CenterVertically
                ) {
                    Text(
                        text        = "${stations.size} station${if (stations.size > 1) "s" else ""} " +
                                "trouvee${if (stations.size > 1) "s" else ""}",
                        style       = MaterialTheme.typography.bodyMedium,
                        fontWeight  = FontWeight.Medium,
                        color       = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (stations.isNotEmpty() && stations.first().distance > 0) {
                        Text(
                            text    = "Triees par distance",
                            style   = MaterialTheme.typography.bodySmall,
                            color   = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        // liste des stations
        items(
            items   = stations,
            key     = { station -> station.id},
        ){ station ->
            StationCard(
                station = station,
                onClick = { onStationClick(station) }
            )
        }

        // Espacement en bas pour eviter que le contenu ne soit cache par la bottom navbar
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}