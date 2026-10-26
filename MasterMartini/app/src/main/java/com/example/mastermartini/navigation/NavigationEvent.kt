package com.example.mastermartini.navigation

// Representación de acciones y manipulaciones sobre la pila de navegación
sealed class NavigationEvent {
    data class NavigateTo(
        val route: Screen,
        val popUpToRoute: Screen? = null,
        val inclusive: Boolean = false,
        val singleTop: Boolean = false
    ) : NavigationEvent()

    data object PopBackStack : NavigationEvent()
    data object NavigateUp : NavigationEvent()
}