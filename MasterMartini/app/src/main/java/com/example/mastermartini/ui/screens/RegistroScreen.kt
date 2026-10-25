package com.example.mastermartini.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mastermartini.viewmodel.RegistroViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.example.mastermartini.ui.theme.MasterMartiniTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    viewModel: RegistroViewModel = viewModel(),
    onRegistroExitoso: () -> Unit = {}
) {
    // 1. Observación reactiva: la pantalla se redibuja cada vez que el ViewModel emite cambios
    val uiState by viewModel.uiState.collectAsState()

    // 2. Disparador de navegación cuando el guardado termina
    if (uiState.registroExitoso) {
        onRegistroExitoso()
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                // Habilita desplazamiento vertical para que el teclado no tape los campos inferiores
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Registro de Empresa",
                style = MaterialTheme.typography.headlineMedium
            )

            // Campo Razon Social
            OutlinedTextField(
                value = uiState.razonSocial,
                onValueChange = { viewModel.onRazonSocialChange(it) },
                label = { Text("Razón Social") },
                isError = uiState.errorRazonSocial != null,
                supportingText = {
                    uiState.errorRazonSocial?.let { Text(it) }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // -Campo Rut
            OutlinedTextField(
                value = uiState.rut,
                onValueChange = { viewModel.onRutChange(it) },
                label = { Text("RUT Empresa (ej: 12.345.678-5)") },
                isError = uiState.errorRut != null,
                supportingText = {
                    uiState.errorRut?.let { Text(it) }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Campo Rubro Gastronómico (menu desplegable)
            ExposedDropdownMenuBox(
                expanded = uiState.isDropdownExpanded,
                onExpandedChange = { viewModel.onDropdownExpandedChange(it) },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.rubro,
                    onValueChange = {},
                    readOnly = true, // No permite que aparezca el teclado
                    label = { Text("Rubro Gastronómico") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.isDropdownExpanded) },
                    isError = uiState.errorRubro != null,
                    supportingText = {
                        uiState.errorRubro?.let { Text(it) }
                    },
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = uiState.isDropdownExpanded,
                    onDismissRequest = { viewModel.onDropdownExpandedChange(false) }
                ) {
                    viewModel.rubrosDisponibles.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = { viewModel.onRubroChange(opcion) }
                        )
                    }
                }
            }

            // --- 4. TELÉFONO CHILENO (9 dígitos) ---
            OutlinedTextField(
                value = uiState.telefono,
                onValueChange = { viewModel.onTelefonoChange(it) },
                label = { Text("Teléfono de Contacto (9 dígitos)") },
                prefix = { Text("+56 ") },
                isError = uiState.errorTelefono != null,
                supportingText = {
                    uiState.errorTelefono?.let { Text(it) }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // --- 5. CORREO ELECTRÓNICO ---
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Correo Electrónico") },
                isError = uiState.errorEmail != null,
                supportingText = {
                    uiState.errorEmail?.let { Text(it) }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- 6. BOTÓN CON ACCESIBILIDAD Y REGLA DE NEGOCIO (R-01, R-06) ---
            Button(
                onClick = { viewModel.registrar() },
                enabled = uiState.isFormValid, // Bloqueado hasta que no queden errores
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp) // Touch target accesible >= 48dp
            ) {
                Text(
                    text = "Registrar Empresa",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }





}