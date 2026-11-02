package com.example.mastermartini.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mastermartini.navigation.Screen

sealed class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
) {
    data object Home : BottomNavItem("Inicio", Screen.Home.route, Icons.Default.Home)
    data object Catalogo : BottomNavItem("Catálogo", Screen.Catalogo.route, Icons.AutoMirrored.Filled.MenuBook)
    data object Favoritos : BottomNavItem("Favoritos", Screen.Favoritos.route, Icons.Default.Favorite)
    data object Distribuidores : BottomNavItem("Ubicación", Screen.Distribuidores.route, Icons.Default.LocationOn)
    data object Perfil : BottomNavItem("Perfil", Screen.Perfil.route, Icons.Default.Person)
}

@Composable
fun AppBottomBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Catalogo,
        BottomNavItem.Favoritos,
        BottomNavItem.Distribuidores,
        BottomNavItem.Perfil
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // No mostrar la barra de navegación en Login ni en Registro
    val rutasSinBarra = listOf(Screen.Login.route, Screen.Registro.route)
    if (currentRoute in rutasSinBarra || currentRoute == null) {
        return
    }

    NavigationBar {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
