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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.essence_togo.R
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
                    text        = stringResource(id = R.string.home_title),
                    style       = MaterialTheme.typography.headlineLarge,
                    fontWeight  = FontWeight.Bold,
                    color       = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text    = stringResource(id = R.string.home_subtitle),
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
                        message     = stringResource(id = R.string.loading_location)
                    )
                }
                uiState.error != null -> {
                    ErrorState(
                        title       = stringResource(R.string.error_title),
                        subtitle    = uiState.error!!,
                        onRetry     = {viewModel.retry()},
                        modifier    = Modifier.align(Alignment.Center),
                    )
                }
                uiState.stations.isEmpty() -> {
                    EmptyState(
                        title       = stringResource(id = R.string.no_stations_title),
                        subtitle    = stringResource(id = R.string.no_stations_subtitle),
                        modifier    = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    StationsList(
                        stations   = uiState.stations,
                        onStationClick      = { station ->
                            viewModel.onStationClick(station)
                            onStationClick(station.id)
                        },
                        onToggleFavorite = { station ->
                            viewModel.toggleFavorite(station)
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
    onStationClick: (Station) -> Unit,
    onToggleFavorite: (Station) -> Unit
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
                            text    = stringResource(id = R.string.sorted_by_distance),
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
                onClick = { onStationClick(station) },
                onToggleFavorite = onToggleFavorite
            )
        }

        // Espacement en bas pour eviter que le contenu ne soit cache par la bottom navbar
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}