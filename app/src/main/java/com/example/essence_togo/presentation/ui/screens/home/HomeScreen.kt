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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.essence_togo.R
import com.example.essence_togo.data.model.Station
import com.example.essence_togo.presentation.ui.components.EmptyState
import com.example.essence_togo.presentation.ui.components.ErrorState
import com.example.essence_togo.presentation.ui.components.LoadingIndicator
import com.example.essence_togo.presentation.ui.components.OfflineIndicator
import com.example.essence_togo.presentation.ui.components.StationCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStationClick: (Int) -> Unit,
    onSettingsClick: () -> Unit
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // TopAppBar avec bouton Settings
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = stringResource(R.string.home_title),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.home_subtitle),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            actions = {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.settings_icon_content_description),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Indicateur de mode offline
        OfflineIndicator(isOffline = uiState.isOffline)

        // contenu principal
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    LoadingIndicator(
                        modifier    = Modifier.align(Alignment.Center),
                        message     = stringResource(R.string.loading_location)
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
                        title       = stringResource(R.string.no_stations_title),
                        subtitle    = stringResource(R.string.check_connection),
                        modifier    = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    StationsList(
                        stations   = uiState.stations,
                        isOffline  = uiState.isOffline,
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
    isOffline: Boolean,
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
                    containerColor = if (isOffline)
                        MaterialTheme.colorScheme.errorContainer
                    else
                        MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier            = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment   = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text        = "${stations.size} station${if (stations.size > 1) "s" else ""} " +
                                    if (isOffline) "en cache" else "trouvée${if (stations.size > 1) "s" else ""}",
                            style       = MaterialTheme.typography.bodyMedium,
                            fontWeight  = FontWeight.Medium,
                            color       = if (isOffline)
                                MaterialTheme.colorScheme.onErrorContainer
                            else
                                MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        if (stations.isNotEmpty() && stations.first().distance > 0) {
                            Text(
                                text    = "Triées par distance",
                                style   = MaterialTheme.typography.bodySmall,
                                color   = if (isOffline)
                                    MaterialTheme.colorScheme.onErrorContainer
                                else
                                    MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
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