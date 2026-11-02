package com.example.mastermartini

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.mastermartini.data.repository.NegocioRepository
import com.example.mastermartini.navigation.AppNavHost
import com.example.mastermartini.navigation.Screen
import com.example.mastermartini.ui.components.AppBottomBar
import com.example.mastermartini.ui.theme.MasterMartiniTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MasterMartiniTheme {
                val repository = remember { NegocioRepository(applicationContext) }
                val sesionActiva by repository.isSesionActivaFlow.collectAsState(initial = null)

                when (val activa = sesionActiva) {
                    null -> {
                        // Estado de carga inicial mientras se leen las preferencias de DataStore
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    else -> {
                        val navController = rememberNavController()
                        val startDestination = if (activa) Screen.Home.route else Screen.Login.route

                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            bottomBar = {
                                AppBottomBar(navController = navController)
                            }
                        ) { innerPadding ->
                            AppNavHost(
                                navController = navController,
                                startDestination = startDestination,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}