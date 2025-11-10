package com.example.essence_togo.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.essence_togo.R
import com.example.essence_togo.presentation.ui.theme.SearchBarBackground

@Composable
fun CustomSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = stringResource(id = R.string.search_placeholder),
    enabled: Boolean = true,
    modifier : Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    
    OutlinedTextField(
        value           = query,
        onValueChange   = onQueryChange,
        modifier        = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .focusRequester(focusRequester),
        placeholder     = {
            Text(
                text    = placeholder,
                style   = MaterialTheme.typography.bodyMedium,
                color   = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        leadingIcon     = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(id = R.string.filter_title),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon    = {
            if(query.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onQueryChange("")
                        keyboardController?.hide()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(id = R.string.clear_search_content_description),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        enabled         = enabled,
        singleLine      = true,
        shape           = RoundedCornerShape(25.dp),
        colors          = OutlinedTextFieldDefaults.colors(
            focusedContainerColor   = SearchBarBackground,
            unfocusedContainerColor = SearchBarBackground,
            focusedBorderColor      = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor    = MaterialTheme.colorScheme.outline,
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
                focusRequester.freeFocus()
            }
        )
    )
}
