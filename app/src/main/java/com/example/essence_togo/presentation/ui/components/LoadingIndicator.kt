package com.example.essence_togo.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.essence_togo.R

@Composable
fun LoadingIndicator(
    modifier: Modifier  = Modifier,
    message: String     = stringResource(R.string.loading_stations),
) {
    Column(
        modifier            = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color       = MaterialTheme.colorScheme.primary,
            strokeWidth = 3.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text        = message,
            style       = MaterialTheme.typography.bodyMedium,
            color       = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign   = TextAlign.Center
        )
    }
}

@Composable
fun EmptyState(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text        = title,
            style       = MaterialTheme.typography.headlineMedium,
            color       = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign   = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text        = subtitle,
            style       = MaterialTheme.typography.bodyMedium,
            color       = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign   = TextAlign.Center
        )
    }
}

@Composable
fun ErrorState(
    modifier: Modifier      = Modifier,
    title: String           = stringResource(id = R.string.error_connection),
    subtitle: String        = stringResource(id = R.string.retry),
    onRetry: (() -> Unit)?  = null,
) {
    Column(
        modifier            = modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text        = title,
            style       = MaterialTheme.typography.headlineMedium,
            color       = MaterialTheme.colorScheme.error,
            textAlign   = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text        = subtitle,
            style       = MaterialTheme.typography.bodyMedium,
            color       = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign   = TextAlign.Center
        )

        if (onRetry != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors  = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = stringResource(id = R.string.retry))
            }
        }
    }
}