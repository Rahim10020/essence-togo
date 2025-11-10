package com.example.essence_togo.presentation.ui.screens.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.essence_togo.presentation.ui.components.LoadingIndicator
import com.example.essence_togo.presentation.ui.components.StationCard

@Composable
@Preview(showBackground = true)
fun HistoryScreenPreview() {
    // HistoryScreen()
}

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onStationClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showClearDialog by remember { mutableStateOf(false)}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Header avec titre et bouton de suppression
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text        = stringResource(id = R.string.nav_history),
                        style       = MaterialTheme.typography.headlineLarge,
                        fontWeight  = FontWeight.Bold,
                        color       = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text        = stringResource(id = R.string.history_subtitle),
                        style       = MaterialTheme.typography.titleMedium,
                        fontWeight  = FontWeight.Bold,
                        color       = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Bouton pour vider l'historique
                if (uiState.visitedStations.isNotEmpty()) {
                    FilledTonalButton(
                        onClick             = { showClearDialog = true },
                        colors              = ButtonDefaults.filledTonalButtonColors(
                            containerColor  = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Icon(
                            imageVector         = Icons.Outlined.Delete,
                            contentDescription  = stringResource(id = R.string.clear_history),
                            modifier            = Modifier.size(18.dp)
                        )
                        Text(text = stringResource(id = R.string.clear_history))
                    }
                }
            }
        }

        // contenu principal 
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    LoadingIndicator(
                        modifier    = Modifier.align(Alignment.Center),
                        message     = stringResource(id = R.string.loading_history)
                    )
                }

                uiState.visitedStations.isEmpty() -> {
                    EmptyState(
                        title       = stringResource(id = R.string.no_visited_stations_title),
                        subtitle    = stringResource(id = R.string.no_visited_stations_subtitle),
                        modifier    = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    HistoryStationsList(
                        stations        = uiState.visitedStations,
                        onStationClick  = {station ->
                            viewModel.onStationClick(station)
                            onStationClick(station.id)
                        }
                    )
                }
            }
        }
    }

    // dialog de confirmation pour vider l'historique
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text(text = stringResource(id = R.string.clear_history))
            },
            text = {
                Text(
                    text = stringResource(id = R.string.clear_history_message),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearHistory()
                        showClearDialog = false
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.confirm),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearDialog = false }
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun HistoryStationsList(
    stations: List<Station>,
    onStationClick: (Station) -> Unit
) {
    LazyColumn {
        // afficher le nombre de station visitees
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors  = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text    = "${stations.size} station${if (stations.size > 1) "s" else ""} " +
                                "visitee${if (stations.size > 1) "s" else ""}",
                        style   = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color   = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text    = stringResource(id = R.string.most_recent_first),
                        style   = MaterialTheme.typography.bodySmall,
                        color   = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }

        // Liste des stations visitees
        items(
            items   = stations,
            key     = { station -> "${station.id}_${station.hashCode()}"}
        ) {station ->
            StationCard(
                station = station,
                onClick = { onStationClick(station)}
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}