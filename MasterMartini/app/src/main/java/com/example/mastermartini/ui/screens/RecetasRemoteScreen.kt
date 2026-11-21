package com.example.mastermartini.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mastermartini.data.model.RecetaRemote
import com.example.mastermartini.viewmodel.RecetaRemoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecetasRemoteScreen(
    onNavigateBack: () -> Unit, // Función para volver a la pantalla anterior
    viewModel: RecetaRemoteViewModel = viewModel() // Conexión con el ViewModel que habla con MockAPI
) {
    // Estados reactivos que escuchan los datos en tiempo real desde el ViewModel
    val recetas by viewModel.recetas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Variables locales de la pantalla para abrir/cerrar la ventana emergente de crear o editar
    var mostrarDialogo by remember { mutableStateOf(false) }
    var recetaEnEdicion by remember { mutableStateOf<RecetaRemote?>(null) }

    // Estructura principal de la pantalla (barra superior + contenido + botón flotante)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recetario Comunitario") },
                navigationIcon = {
                    // Botón de flecha atrás
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    // Botón para volver a descargar la lista desde internet (GET)
                    IconButton(onClick = { viewModel.cargarRecetas() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Recargar"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            // Botón flotante "+" para abrir el formulario de nueva receta (POST)
            FloatingActionButton(
                onClick = {
                    recetaEnEdicion = null // Null indica que es creación nueva, no edición
                    mostrarDialogo = true
                },
                modifier = Modifier.testTag("fab_agregar_receta") // Etiqueta para pruebas de UI
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Receta")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Decide qué dibujar según el estado actual de la red
            when {
                // 1. Si está cargando por primera vez, muestra el círculo girando
                isLoading && recetas.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag("loading_recetas")
                    )
                }

                // 2. Si falló internet o la API, muestra el error y botón para reintentar
                errorMessage != null && recetas.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage ?: "Error desconocido",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.cargarRecetas() }) {
                            Text("Reintentar")
                        }
                    }
                }

                // 3. Si respondió bien pero la lista en MockAPI está vacía
                recetas.isEmpty() -> {
                    Text(
                        text = "No hay recetas registradas. Presiona + para crear una.",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                // 4. Si hay recetas, dibuja la lista desplazable (LazyColumn)
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("lista_recetas_remotas"),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recetas, key = { it.id }) { receta ->
                            // Tarjeta individual por cada receta remota
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("card_receta_${receta.id}"),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Título de la receta
                                        Text(
                                            text = receta.titulo,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Row {
                                            // Botón Lápiz: abre el formulario cargado con esta receta para editar (PUT)
                                            IconButton(
                                                onClick = {
                                                    recetaEnEdicion = receta
                                                    mostrarDialogo = true
                                                },
                                                modifier = Modifier.testTag("btn_editar_${receta.id}")
                                            ) {
                                                Icon(
                                                    Icons.Default.Edit,
                                                    contentDescription = "Editar"
                                                )
                                            }
                                            // Botón Basurero: borra la receta directamente en MockAPI (DELETE)
                                            IconButton(
                                                onClick = { viewModel.eliminarReceta(receta.id) },
                                                modifier = Modifier.testTag("btn_eliminar_${receta.id}")
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Eliminar",
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    // Descripción de la receta
                                    Text(
                                        text = receta.descripcion,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    // Etiqueta inferior con categoría, tiempo y dificultad
                                    Text(
                                        text = "${receta.categoria} • ${receta.tiempoMinutos} min • ${receta.dificultad}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Barra delgada arriba cuando está actualizando, creando o borrando sobre una lista ya visible
            if (isLoading && recetas.isNotEmpty()) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            }
        }

        // Muestra la ventana emergente de formulario cuando mostrarDialogo es true
        if (mostrarDialogo) {
            RecetaFormDialog(
                recetaInicial = recetaEnEdicion,
                onDismiss = { mostrarDialogo = false },
                onConfirm = { titulo, descripcion, categoria, tiempo, dificultad ->
                    val datos = RecetaRemote(
                        id = recetaEnEdicion?.id ?: "",
                        titulo = titulo,
                        descripcion = descripcion,
                        categoria = categoria,
                        tiempoMinutos = tiempo,
                        dificultad = dificultad
                    )
                    // Si recetaEnEdicion es null crea una nueva (POST); si no, actualiza la existente (PUT)
                    if (recetaEnEdicion == null) {
                        viewModel.crearReceta(datos) { mostrarDialogo = false }
                    } else {
                        viewModel.actualizarReceta(recetaEnEdicion!!.id, datos) {
                            mostrarDialogo = false
                        }
                    }
                }
            )
        }
    }
}

// Opciones fijas para estandarizar los datos enviados a MockAPI
private val CATEGORIAS_RECETA = listOf("Chocolatería", "Pastelería", "Panadería", "Heladería", "Masas y Pizzería")
private val DIFICULTADES_RECETA = listOf("Básica", "Intermedia", "Avanzada")

// Campo de selección cerrado (solo lectura) con menú desplegable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OpcionesDropdown(
    etiqueta: String,
    opciones: List<String>,
    seleccionada: String,
    onSeleccion: (String) -> Unit,
    testTag: String
) {
    var expandido by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = it }
    ) {
        OutlinedTextField(
            value = seleccionada,
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
                .testTag(testTag)
        )
        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccion(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}

// Ventana emergente (AlertDialog) con los 5 campos para crear o editar una receta
@Composable
private fun RecetaFormDialog(
    recetaInicial: RecetaRemote?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Int, String) -> Unit
) {
    // Estados de los campos de texto del formulario
    var titulo by remember { mutableStateOf(recetaInicial?.titulo ?: "") }
    var descripcion by remember { mutableStateOf(recetaInicial?.descripcion ?: "") }
    var categoria by remember {
        mutableStateOf(recetaInicial?.categoria?.takeIf { it in CATEGORIAS_RECETA } ?: CATEGORIAS_RECETA[1])
    }
    var tiempoTexto by remember { mutableStateOf(recetaInicial?.tiempoMinutos?.toString() ?: "30") }
    var dificultad by remember {
        mutableStateOf(recetaInicial?.dificultad?.takeIf { it in DIFICULTADES_RECETA } ?: DIFICULTADES_RECETA[1])
    }

    // Validación básica: impide guardar si hay campos vacíos o tiempo igual a 0
    val esValido = titulo.isNotBlank() &&
            descripcion.isNotBlank() &&
            categoria.isNotBlank() &&
            (tiempoTexto.toIntOrNull() ?: 0) > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (recetaInicial == null) "Nueva Receta" else "Editar Receta")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_titulo_receta")
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_descripcion_receta")
                )
                // Menú desplegable cerrado con las categorías estándar
                OpcionesDropdown(
                    etiqueta = "Categoría",
                    opciones = CATEGORIAS_RECETA,
                    seleccionada = categoria,
                    onSeleccion = { categoria = it },
                    testTag = "input_categoria_receta"
                )
                OutlinedTextField(
                    value = tiempoTexto,
                    onValueChange = { tiempoTexto = it.filter { c -> c.isDigit() } }, // Solo permite números
                    label = { Text("Tiempo (minutos)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_tiempo_receta")
                )
                // Menú desplegable cerrado con los niveles de dificultad estándar
                OpcionesDropdown(
                    etiqueta = "Dificultad",
                    opciones = DIFICULTADES_RECETA,
                    seleccionada = dificultad,
                    onSeleccion = { dificultad = it },
                    testTag = "input_dificultad_receta"
                )
            }
        },
        confirmButton = {
            // Botón Guardar: se bloquea automáticamente si esValido es false
            Button(
                onClick = {
                    onConfirm(
                        titulo.trim(),
                        descripcion.trim(),
                        categoria.trim(),
                        tiempoTexto.toIntOrNull() ?: 30,
                        dificultad.trim()
                    )
                },
                enabled = esValido,
                modifier = Modifier.testTag("btn_guardar_receta")
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
