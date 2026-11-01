package com.example.mastermartini.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun MensajeError(
    mensaje: String?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = !mensaje.isNullOrBlank(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        if (!mensaje.isNullOrBlank()) {
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
