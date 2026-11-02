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
import com.example.mastermartini.ui.screens.FavoritosScreen
import com.example.mastermartini.ui.screens.HomeScreen
import com.example.mastermartini.ui.screens.PerfilScreen
import com.example.mastermartini.ui.screens.RegistroScreen
import com.example.mastermartini.ui.screens.login.LoginScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Vista Inicio/Login (R-14)
        composable(route = Screen.Login.route) {
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavegarARegistro = {
                    navController.navigate(Screen.Registro.route)
                }
            )
        }

        // Vista Home con bienvenida y opciones de la app
        composable(route = Screen.Home.route) {
            HomeScreen(
                onIrAlCatalogo = {
                    navController.navigate(Screen.Catalogo.route)
                },
                onIrADistribuidores = {
                    navController.navigate(Screen.Distribuidores.route)
                },
                onCerrarSesion = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

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

        // Vista Favoritos: Listado reactivo persistido en DataStore
        composable(route = Screen.Favoritos.route) {
            FavoritosScreen(
                onRecetaClick = { recetaId ->
                    navController.navigate(Screen.Detalle().crearRuta(recetaId))
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

        // Vista 4: Registro de negocio (R-12 y R-13)
        composable(route = Screen.Registro.route) {
            RegistroScreen(
                onRegistroExitoso = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Registro.route) { inclusive = true }
                    }
                }
            )
        }

        // Vista 5: Perfil de usuario y negocio
        composable(route = Screen.Perfil.route) {
            PerfilScreen(
                onEditarClick = {
                    navController.navigate(Screen.Registro.route)
                },
                onCerrarSesion = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}