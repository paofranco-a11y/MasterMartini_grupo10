package com.example.mastermartini.ui.screens.login

/**
 * Representa el estado inmutable de la pantalla de autenticación.
 */
data class LoginUiState(
    val rut: String = "",
    val email: String = "",
    val errorRut: String? = null,
    val errorEmail: String? = null,
    val isLoading: Boolean = false,
    val errorAutenticacion: String? = null,
    val isFormValid: Boolean = false,
    val loginExitoso: Boolean = false
)
