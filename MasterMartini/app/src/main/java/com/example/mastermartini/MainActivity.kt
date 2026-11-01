package com.example.mastermartini

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.mastermartini.data.repository.NegocioRepository
import com.example.mastermartini.ui.screens.HomeScreen
import com.example.mastermartini.ui.screens.RegistroScreen
import com.example.mastermartini.ui.screens.login.LoginScreen
import com.example.mastermartini.ui.theme.MasterMartiniTheme
import kotlinx.coroutines.launch

enum class PantallaDestino {
    LOGIN,
    REGISTRO,
    HOME_DASHBOARD
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MasterMartiniTheme {
                val repository = remember { NegocioRepository(applicationContext) }
                val scope = rememberCoroutineScope()
                val isSesionActiva by repository.isSesionActivaFlow.collectAsState(initial = null)

                when (isSesionActiva) {
                    null -> {
                        // Pantalla de carga / splash transitorio mientras DataStore lee el estado
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    true -> {
                        // Sesión activa previa detectada
                        HomeScreen(
                            onCerrarSesion = {
                                scope.launch {
                                    repository.cerrarSesion()
                                    Toast.makeText(this@MainActivity, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                    false -> {
                        var rutaActual by remember { mutableStateOf(PantallaDestino.LOGIN) }
                        when (rutaActual) {
                            PantallaDestino.LOGIN -> {
                                LoginScreen(
                                    onLoginExitoso = {
                                        rutaActual = PantallaDestino.HOME_DASHBOARD
                                    },
                                    onNavegarARegistro = {
                                        rutaActual = PantallaDestino.REGISTRO
                                    }
                                )
                            }
                            PantallaDestino.REGISTRO -> {
                                RegistroScreen(
                                    onRegistroExitoso = {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "¡Empresa registrada exitosamente!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        rutaActual = PantallaDestino.HOME_DASHBOARD
                                    }
                                )
                            }
                            PantallaDestino.HOME_DASHBOARD -> {
                                HomeScreen(
                                    onCerrarSesion = {
                                        scope.launch {
                                            repository.cerrarSesion()
                                            rutaActual = PantallaDestino.LOGIN
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
