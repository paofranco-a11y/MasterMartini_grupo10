package com.example.mastermartini.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mastermartini.data.repository.NegocioRepository
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onIrAlCatalogo: () -> Unit = {},
    onIrADistribuidores: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = NegocioRepository(context)

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¡Bienvenido a Master Martini!",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Sesión iniciada correctamente.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Acceso al Catálogo de Recetas
            Button(
                onClick = onIrAlCatalogo,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Catálogo de Recetas")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Acceso a Puntos de Distribución (GPS)
            Button(
                onClick = onIrADistribuidores,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Puntos de Distribución (GPS)")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Cerrar sesión
            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        repository.cerrarSesion()
                        onCerrarSesion()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar Sesión")
            }
        }
    }
}

