package com.example.mastermartini.data.model

/**
 * Modelo DTO para las recetas consumidas y gestionadas vía MockAPI.io.
 */
data class RecetaRemote(
    val id: String = "",
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val tiempoMinutos: Int,
    val dificultad: String
)
