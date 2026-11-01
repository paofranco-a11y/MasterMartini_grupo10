package com.example.mastermartini.data.model

data class PerfilNegocio(
    val razonSocial: String,
    val rut: String,
    val rubro: String,
    val telefono: String,
    val email: String,
    val fotoUri: String? = null
)
