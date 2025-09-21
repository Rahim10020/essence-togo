package com.example.essence_togo.presentation.ui.screens.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.essence_togo.data.model.Station
import com.example.essence_togo.presentation.ui.components.CustomSearchBar
import com.example.essence_togo.presentation.ui.components.EmptyState
import com.example.essence_togo.presentation.ui.components.ErrorState
import com.example.essence_togo.presentation.ui.components.LoadingIndicator
import com.example.essence_togo.presentation.ui.components.StationCard

@Composable
@Preview(showBackground = true)
fun FilterScreenPreview(){
    // FilterScreen()
}

@Composable
fun FilterScreen(
    viewModel: FilterViewModel,
    onStationClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding()
    ) {
        // Header avec titre
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text        = "Rechercher...",
                    style       = MaterialTheme.typography.headlineLarge,
                    fontWeight  = FontWeight.Bold,
                    color       = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text        = "Trouvez votre station-service",
                    style       = MaterialTheme.typography.titleMedium,
                    color       = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // barre de recherche
        CustomSearchBar(
            query           = uiState.searchQuery,
            onQueryChange   = viewModel::onSearchQueryChange,
            placeholder     = "Nom de station ou adresse...",
            enabled         = !uiState.isLoading
        )

        // contenu principal
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    LoadingIndicator(
                        modifier    = Modifier.align(Alignment.Center),
                        message     = "Chargement des stations..."
                    )
                }

                uiState.error != null -> {
                    ErrorState(
                        title       = "Erreur de chargement",
                        subtitle    = uiState.error!!,
                        onRetry     = { viewModel.retry() },
                        modifier    = Modifier.align(Alignment.Center)
                    )
                }

                uiState.filtredStations.isEmpty() && uiState.searchQuery.isNotBlank() -> {
                    SearchEmptyState(
                        query = uiState.searchQuery,
                        onClearSearch = { viewModel.clearSearch() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.filtredStations.isEmpty() -> {
                    EmptyState(
                        title = "Aucune station disponible",
                        subtitle = "Verifiez votre connexion internet",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    FiltredStationsList(
                        stations = uiState.filtredStations,
                        searchQuery = uiState.searchQuery,
                        totalStations = uiState.allStations.size,
                        onStationClick = { station ->
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
private fun SearchEmptyState(
    query: String,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text        = "Aucun resultat",
            style       = MaterialTheme.typography.headlineMedium,
            color       = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign   = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text        = "Aucune station trouvee pour \"$query\" ",
            style       = MaterialTheme.typography.bodyMedium,
            color       = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign   = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onClearSearch,
            colors  = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                text    = "Voir toutes les stations",
                color   = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun FiltredStationsList(
    stations: List<Station>,
    searchQuery: String,
    totalStations: Int,
    onStationClick: (Station) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Afficher les statistiques de recherche
        item {
            Card(
                modifier    = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors      = CardDefaults.cardColors(
                    containerColor = if (searchQuery.isNotBlank()) {
                        MaterialTheme.colorScheme.secondaryContainer
                    } else {
                        MaterialTheme.colorScheme.secondaryContainer
                    }
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (searchQuery.isNotBlank()) {
                        Text(
                            text        = "Resultats pour \"$searchQuery\" ",
                            style       = MaterialTheme.typography.titleSmall,
                            fontWeight  = FontWeight.Medium,
                            color       = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    
                    Text(
                        text = "${stations.size} station${if (stations.size > 1) "s" else ""}" +
                            if (searchQuery.isNotBlank()) "trouvee${if (stations.size > 1) "s" else ""}"
                            else "disponible${if (stations.size > 1) "s" else ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (searchQuery.isNotBlank()) {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )

                    if (searchQuery.isNotBlank() && stations.size < totalStations) {
                        Text(
                            text    = "sur $totalStations au total",
                            style   = MaterialTheme.typography.bodySmall,
                            color   = if (searchQuery.isNotBlank()) {
                                MaterialTheme.colorScheme.onSecondaryContainer
                            } else {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            }
                        )
                    }
                }
            }
        }

        // Liste des stations filtrees
        items(
            items   = stations,
            key     = { station -> station.id }
        ) { station ->
            StationCard(
                station = station,
                onClick = { onStationClick(station) }
            )
        }
        // Espacement en bas
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}