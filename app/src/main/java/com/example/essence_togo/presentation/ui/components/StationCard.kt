package com.example.essence_togo.presentation.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.essence_togo.R
import com.example.essence_togo.data.model.Station
import com.example.essence_togo.presentation.ui.theme.AddressColor
import com.example.essence_togo.presentation.ui.theme.DistanceColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationCard(
    station: Station,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onToggleFavorite: ((Station) -> Unit)? = null  // Optionnel pour rétrocompatibilité
){
    Card(
        onClick     = onClick,
        modifier    = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation   = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape       = RoundedCornerShape(12.dp),
        colors      = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier            = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment   = Alignment.CenterVertically
        ) {
            // image de la station
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(station.getAllImages().firstOrNull() ?: R.drawable.ic_launcher_background)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .crossfade(true)
                    .size(240, 240) // Optimisation: limiter la taille de l'image chargée
                    .build(),
                contentDescription = stringResource(id = R.string.station_image_content_description, station.nom),
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))

            // informations sur la station
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // nom de la station
                Text(
                    text        = station.nom,
                    style       = MaterialTheme.typography.titleLarge,
                    fontWeight  = FontWeight.Bold,
                    color       = MaterialTheme.colorScheme.onSurface,
                    maxLines    = 1,
                    overflow    = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(4.dp))
                // addresse
                Text(
                    text        = station.address,
                    style       = MaterialTheme.typography.bodyMedium,
                    color       = AddressColor,
                    maxLines    = 2,
                    overflow    = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                // distance
                if (station.distance > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = station.getFormattedDistance(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = DistanceColor
                        )
                    }
                }
            }

            // Bouton favori (si le callback est fourni)
            if (onToggleFavorite != null) {
                FavoriteButton(
                    isFavorite = station.isFavorite,
                    onToggleFavorite = { onToggleFavorite(station) }
                )
            }
        }
    }
}