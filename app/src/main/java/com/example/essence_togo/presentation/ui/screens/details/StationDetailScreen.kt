package com.example.essence_togo.presentation.ui.screens.details

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.essence_togo.R
import com.example.essence_togo.data.model.Station
import com.example.essence_togo.presentation.ui.components.ErrorState
import com.example.essence_togo.presentation.ui.components.LoadingIndicator

@Composable
@Preview(showBackground = true)
fun StationDetailPreviewScreen() {
    //StationDetailScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationDetailScreen(
    viewModel: StationDetailsViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column {
        // Top app bar
        TopAppBar(
            title = {
                Text(
                    text        = uiState.station?.nom ?: "Details de la station",
                    style       = MaterialTheme.typography.titleLarge,
                    fontWeight  = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector         = Icons.Default.ArrowBackIosNew,
                        contentDescription  = "Retour"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor      = MaterialTheme.colorScheme.surface,
                titleContentColor   = MaterialTheme.colorScheme.onSurface
            )
        )

        // contenu principal
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    LoadingIndicator(
                        modifier    = Modifier.align(Alignment.Center),
                        message     = "Chargement des details..."
                    )
                }

                uiState.error != null -> {
                    ErrorState(
                        title       = "Erreur",
                        subtitle    = uiState.error!!,
                        onRetry     = {viewModel.retry()},
                        modifier    = Modifier.align(Alignment.Center)
                    )
                }

                uiState.station != null -> {

                }
            }
        }
    }
}

@Composable
fun StationDetailsContent(
    station: Station,
    context: Context
){
    Column {
        // image de la station
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape     = RoundedCornerShape(16.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(station.imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .build(),
                contentDescription = "Image de ${station.nom}",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }
        // informations principales
        Card(
            modifier            = Modifier.fillMaxWidth(),
            elevation           = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors              = CardDefaults.cardColors(
                containerColor  = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text        = station.nom,
                    style       = MaterialTheme.typography.headlineMedium,
                    fontWeight  = FontWeight.Bold,
                    color       = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign   = TextAlign.Center,
                    modifier    = Modifier.fillMaxWidth()
                )

                Divider(color  = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))


            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
){

}