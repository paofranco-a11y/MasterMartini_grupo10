package com.example.mastermartini.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mastermartini.ui.screens.CatalogoScreen
import com.example.mastermartini.ui.screens.DetalleRecetaScreen
import com.example.mastermartini.ui.screens.DistribuidoresScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Catalogo.route,
        modifier = modifier
    ) {
        // Vista 1: Catálogo interactivo con filtrado por categorías (R-08 y R-09)
        composable(route = Screen.Catalogo.route) {
            CatalogoScreen(
                onRecetaClick = { recetaId ->
                    navController.navigate(Screen.Detalle().crearRuta(recetaId))
                },
                onDistribuidoresClick = {
                    navController.navigate(Screen.Distribuidores.route)
                }
            )
        }

        // Vista 2: Detalle de Receta con animación expandible (R-07)
        composable(
            route = Screen.Detalle().route,
            arguments = listOf(
                navArgument("recetaId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("recetaId").orEmpty()
            DetalleRecetaScreen(
                recetaId = id,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Vista 3: Recurso Nativo GPS - Distribuidores oficiales (R-11)
        composable(route = Screen.Distribuidores.route) {
            DistribuidoresScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Vista 4: Registro de negocio (Módulo Integrante B)
        composable(route = Screen.Registro.route) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Formulario de Registro")
            }
        }

        // Vista 5: Perfil de usuario (Módulo Integrante B)
        composable(route = Screen.Perfil.route) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Perfil de Usuario")
            }
        }
    }
}