package com.example.mastermartini.viewmodel

data class RegistroUiState(
    // Valores crudos de los campos de entrada
    val razonSocial: String = "",
    val rut: String = "",
    val rubro: String = "",
    val telefono: String = "",
    val email: String = "",
    val fotoUri: String? = null,

    // Errores de validacion por campo (null indica campo válido o sin validar)
    val errorRazonSocial: String? = null,
    val errorRut: String? = null,
    val errorRubro: String? = null,
    val errorTelefono: String? = null,
    val errorEmail: String? = null,

    // Banderas de control de interfaz de usuario
    val isFormValid: Boolean = false,
    val isDropdownExpanded: Boolean = false,
    val isSubmitting: Boolean = false,
    val registroExitoso: Boolean = false
)