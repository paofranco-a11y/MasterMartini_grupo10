package com.example.mastermartini.model

// Datos para el perfil del negocio
data class PerfilNegocio(
    val razonSocial: String = "",
    val rutEmpresa: String = "",
    val rubro: String = "",
    val telefono: String = "",
    val email: String = "",
    val fotoUri: String? = null
)

