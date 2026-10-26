package com.example.mastermartini.navigation

// Definición centralizada y segura de tipos para las rutas de la aplicación
sealed class Screen(val route: String) {
    // Destinos fijos
    data object Catalogo : Screen("catalogo_screen")
    data object Registro : Screen("registro_screen")
    data object Perfil : Screen("perfil_screen")

    // Destino dinámico para detalle de contenido con parámetro
    data class Detalle(val recetaId: String = "{recetaId}") : Screen("detalle_screen/{recetaId}") {
        fun crearRuta(id: String): String {
            return "detalle_screen/$id"
        }
    }
}