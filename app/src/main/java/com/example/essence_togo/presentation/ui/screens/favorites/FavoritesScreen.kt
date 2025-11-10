package com.example.essence_togo.presentation.ui.screens.favorites

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.essence_togo.data.model.Station
import com.example.essence_togo.presentation.ui.components.EmptyState
import com.example.essence_togo.presentation.ui.components.LoadingIndicator
import com.example.essence_togo.presentation.ui.components.StationCard

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onStationClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showClearDialog by remember { mutableStateOf(false) }

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
                        text = "Favoris",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Vos stations préférées",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Bouton pour vider les favoris
                if (uiState.favoriteStations.isNotEmpty()) {
                    FilledTonalButton(
                        onClick = { showClearDialog = true },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Vider",
                            modifier = Modifier.size(18.dp)
                        )
                        Text(text = "Vider")
                    }
                }
            }
        }

        // Contenu principal
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    LoadingIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        message = "Chargement des favoris..."
                    )
                }

                uiState.favoriteStations.isEmpty() -> {
                    EmptyState(
                        title = "Aucun favori",
                        subtitle = "Ajoutez des stations à vos favoris en appuyant sur l'étoile ⭐",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    FavoriteStationsList(
                        stations = uiState.favoriteStations,
                        onStationClick = { station ->
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

    // Dialog de confirmation pour vider les favoris
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text(text = "Vider les favoris")
            },
            text = {
                Text(
                    text = "Êtes-vous sûr de vouloir supprimer toutes les stations favorites ? " +
                            "Cette action ne peut pas être annulée."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllFavorites()
                        showClearDialog = false
                    }
                ) {
                    Text(text = "Confirmer", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearDialog = false }
                ) {
                    Text(text = "Annuler")
                }
            }
        )
    }
}

@Composable
private fun FavoriteStationsList(
    stations: List<Station>,
    onStationClick: (Station) -> Unit,
    onToggleFavorite: (Station) -> Unit
) {
    LazyColumn {
        // Afficher le nombre de stations favorites
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${stations.size} station${if (stations.size > 1) "s" else ""} " +
                                "favorite${if (stations.size > 1) "s" else ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Liste des stations favorites
        items(
            items = stations,
            key = { station -> station.id }
        ) { station ->
            StationCard(
                station = station,
                onClick = { onStationClick(station) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}