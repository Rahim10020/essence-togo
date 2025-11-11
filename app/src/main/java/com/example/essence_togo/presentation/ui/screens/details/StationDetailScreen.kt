package com.example.essence_togo.presentation.ui.screens.details

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.stringResource
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
import com.example.essence_togo.presentation.ui.theme.AddressColor
import com.example.essence_togo.presentation.ui.theme.DistanceColor
import androidx.core.net.toUri
import com.example.essence_togo.presentation.ui.components.FavoriteButton

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

    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding()
    ) {
        // Top app bar
        TopAppBar(
            title = {
                Text(
                    text        = uiState.station?.nom ?: stringResource(id = R.string.station_details_title),
                    style       = MaterialTheme.typography.titleLarge,
                    fontWeight  = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector         = Icons.Default.ArrowBackIosNew,
                        contentDescription  = stringResource(id = R.string.back)
                    )
                }
            },
            actions = {
                // Bouton favori dans la barre supérieure
                if (uiState.station != null) {
                    FavoriteButton(
                        isFavorite = uiState.station!!.isFavorite,
                        onToggleFavorite = { viewModel.toggleFavorite() }
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
                        message     = stringResource(id = R.string.loading_details)
                    )
                }

                uiState.error != null -> {
                    ErrorState(
                        title       = stringResource(id = R.string.error_title),
                        subtitle    = uiState.error!!,
                        onRetry     = {viewModel.retry()},
                        modifier    = Modifier.align(Alignment.Center)
                    )
                }

                uiState.station != null -> {
                    StationDetailsContent(
                        station = uiState.station!!,
                        context = context
                    )
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun StationDetailsContent(
    station: Station,
    context: Context
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                contentDescription = stringResource(id = R.string.station_image_content_description, station.nom),
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

                HorizontalDivider(
                    Modifier,
                    DividerDefaults.Thickness,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )

                // Adresse
                DetailRow(
                    icon        = Icons.Default.LocationOn,
                    label       = stringResource(id = R.string.station_address_label),
                    value       = station.address,
                    valueColor  = AddressColor
                )

                // distance si disponible
                if (station.distance > 0) {
                    DetailRow(
                        icon        = Icons.Default.Directions,
                        label       = stringResource(id = R.string.station_distance_label),
                        value       = station.getFormattedDistance(),
                        valueColor  = DistanceColor
                    )
                }
            }
        }

        // coordonnees gps (pour debug/info)
        if (station.latitude != 0.0 && station.longitude != 0.0) {
            Card(
                modifier    = Modifier.fillMaxWidth(),
                elevation   = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors      = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    Text(
                        text        = stringResource(id = R.string.station_coordinates),
                        style       = MaterialTheme.typography.titleMedium,
                        fontWeight  = FontWeight.SemiBold,
                        color       = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text    = stringResource(id = R.string.station_latitude, station.latitude),
                        style   = MaterialTheme.typography.bodySmall,
                        color   = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text    = stringResource(id = R.string.station_longitude, station.longitude),
                        style   = MaterialTheme.typography.bodySmall,
                        color   = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // bouton pour ouvrir Google maps
        Button(
            onClick     = { onpenGoogleMaps(context, station) },
            modifier    = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors      = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape       = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector         = Icons.Default.Directions,
                contentDescription  = stringResource(id = R.string.directions),
                modifier            = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text        = stringResource(id = R.string.go_to_location),
                style       = MaterialTheme.typography.titleMedium,
                fontWeight  = FontWeight.SemiBold
            )
        }

        // espacement en bas
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
){
    Row(
        modifier            = Modifier.fillMaxWidth(),
        verticalAlignment   = Alignment.Top
    ) {
        Icon(
            imageVector         = icon,
            contentDescription  = label,
            tint                = MaterialTheme.colorScheme.primary,
            modifier            = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text    = label,
                style   = MaterialTheme.typography.labelMedium,
                color   = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text    = value,
                style   = MaterialTheme.typography.labelMedium,
                color   = valueColor
            )
        }
    }
}

@Composable
private fun StationImageGallery(
    images: List<String>,
    stationName: String
) {
    if (images.isEmpty()) return

    if (images.size == 1) {
        // Une seule image - affichage normal
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(images[0])
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .build(),
                contentDescription = stringResource(id = R.string.station_image_content_description, stationName),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }
    } else {
        // Plusieurs images - carrousel horizontal
        Column {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 0.dp)
            ) {
                items(images.size) { index ->
                    Card(
                        modifier = Modifier
                            .width(280.dp)
                            .height(200.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(images[index])
                                .placeholder(R.drawable.ic_launcher_background)
                                .error(R.drawable.ic_launcher_background)
                                .build(),
                            contentDescription = stringResource(
                                id = R.string.station_image_content_description,
                                stationName
                            ),
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // Indicateur du nombre d'images
            Text(
                text = stringResource(id = R.string.image_count, images.size),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

private fun onpenGoogleMaps(context: Context, station: Station) {
    try {
        // creer l'URI pour google maps avec les coordonnees et le nom de la station
        val geoUri      = "geo:${station.latitude},${station.longitude}?q=${station.latitude},${station.longitude}(${station.nom})"
        val mapIntent   = Intent(Intent.ACTION_VIEW, geoUri.toUri())

        // definir explicitement googole maps comme application cible
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            // si google maps n'est pas installe, essayer d'ouvrir avec n'importe quel autre app de navigation
            val genericMapIntent = Intent(Intent.ACTION_VIEW, geoUri.toUri())
            if (genericMapIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(genericMapIntent)
            } else {
                Toast.makeText(context, "Aucune application de navigation disponible", Toast.LENGTH_SHORT).show()
            }
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Erreur lors de l'ouverture de la navigation: $e", Toast.LENGTH_SHORT).show()
    }
}