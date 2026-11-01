package com.example.mastermartini.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mastermartini.ui.components.MensajeError
import com.example.mastermartini.util.CameraUtils
import com.example.mastermartini.viewmodel.RegistroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    viewModel: RegistroViewModel = viewModel(),
    onRegistroExitoso: () -> Unit = {}
) {
    val context = LocalContext.current

    // 1. Observación reactiva: la pantalla se redibuja cada vez que el ViewModel emite cambios
    val uiState by viewModel.uiState.collectAsState()

    // Variable para almacenar la URI temporal donde se guardará la fotografía
    var fotoUriTemporal by remember { mutableStateOf<Uri?>(null) }

    // Launcher con contrato TakePicture para captura mediante la cámara externa del dispositivo
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exitoso ->
        if (exitoso && fotoUriTemporal != null) {
            viewModel.onFotoCapturada(fotoUriTemporal.toString())
        }
    }

    // Launcher para solicitar el permiso de cámara en tiempo de ejecución
    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = CameraUtils.crearUriTemporal(context)
            fotoUriTemporal = uri
            takePictureLauncher.launch(uri)
        } else {
            Toast.makeText(
                context,
                "Se requiere permiso de cámara para capturar la fotografía",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Función que verifica permisos y dispara la captura de la fotografía
    val abrirCamara = {
        val permissionCheck = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        )
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val uri = CameraUtils.crearUriTemporal(context)
            fotoUriTemporal = uri
            takePictureLauncher.launch(uri)
        } else {
            permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Animacion de boton
    val escalaBoton by animateFloatAsState(
        targetValue = if (uiState.isFormValid) 1.0f else 0.96f,
        animationSpec = tween(durationMillis = 250),
        label = "animacion_escala_boton"
    )

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

            // CAMPOS DE ENTRADA

            // Campo Razon Social
            OutlinedTextField(
                value = uiState.razonSocial,
                onValueChange = { viewModel.onRazonSocialChange(it) },
                label = { Text("Razón Social") },
                isError = uiState.errorRazonSocial != null,
                supportingText = {
                    MensajeError(mensaje = uiState.errorRazonSocial)
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
                    MensajeError(mensaje = uiState.errorRut)
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
                        MensajeError(mensaje = uiState.errorRubro)
                    },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
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

            // Campo para el numero telefonico
            OutlinedTextField(
                value = uiState.telefono,
                onValueChange = { viewModel.onTelefonoChange(it) },
                label = { Text("Teléfono de Contacto (9 dígitos)") },
                prefix = { Text("+56 ") },
                isError = uiState.errorTelefono != null,
                supportingText = {
                    MensajeError(mensaje = uiState.errorTelefono)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Campo para el correo electrónico ---
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Correo Electrónico") },
                isError = uiState.errorEmail != null,
                supportingText = {
                    MensajeError(mensaje = uiState.errorEmail)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // SECCIÓN R-10: CAPTURA Y CARGA DE IMÁGENES VÍA CÁMARA NATIVA
            Text(
                text = "Fotografía de la Empresa o Local",
                style = MaterialTheme.typography.titleMedium
            )

            // Recuadro interactivo (botón con ícono de cámara) y visor con AsyncImage (Coil)
            OutlinedCard(
                onClick = { abrirCamara() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (!uiState.fotoUri.isNullOrBlank()) {
                        // Visor con AsyncImage (Coil) para previsualización
                        AsyncImage(
                            model = uiState.fotoUri,
                            contentDescription = "Previsualización de la foto capturada",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )

                        // Indicador flotante para cambiar la imagen capturada
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                            shadowElevation = 4.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = android.R.drawable.ic_menu_camera),
                                    contentDescription = "Cambiar fotografía",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Cambiar foto",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    } else {
                        // Recuadro interactivo inicial con ícono de cámara
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_menu_camera),
                                contentDescription = "Ícono de cámara",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Tomar Fotografía",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Presione para abrir la cámara y capturar una imagen",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Boton para enviar el formulario
            Button(
                onClick = { viewModel.registrar() },
                enabled = uiState.isFormValid, // Bloqueado hasta que no queden errores
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(escalaBoton)
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
