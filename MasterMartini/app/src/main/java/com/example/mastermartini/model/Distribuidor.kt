package com.example.mastermartini.model

// Modelo de datos para representar puntos de distribución oficiales Master Martini Chile
data class Distribuidor(
    val id: String,
    val nombre: String,
    val direccion: String,
    val ciudad: String,
    val latitud: Double,
    val longitud: Double,
    val telefono: String
)

object DistribuidoresDataSource {
    // Lista estática con datos sintéticos según las restricciones del caso académico
    val sucursales = listOf(
        Distribuidor(
            id = "dist_1",
            nombre = "Master Martini Central Chile",
            direccion = "Av. Américo Vespucio 1551, Quilicura",
            ciudad = "Santiago",
            latitud = -33.3644,
            longitud = -70.7128,
            telefono = "+56 2 2489 8000"
        ),
        Distribuidor(
            id = "dist_2",
            nombre = "Distribuidora Gastronómica Pan Baker",
            direccion = "Av. Providencia 1208",
            ciudad = "Santiago",
            latitud = -33.4285,
            longitud = -70.6152,
            telefono = "+56 2 2235 9911"
        ),
        Distribuidor(
            id = "dist_3",
            nombre = "Insumos Pastelería Valparaíso",
            direccion = "Calle Errázuriz 850",
            ciudad = "Valparaíso",
            latitud = -33.0458,
            longitud = -71.6197,
            telefono = "+56 32 254 1120"
        ),
        Distribuidor(
            id = "dist_4",
            nombre = "Comercial del Sur Concepción",
            direccion = "Av. Los Carrera 1420",
            ciudad = "Concepción",
            latitud = -36.8201,
            longitud = -73.0444,
            telefono = "+56 41 224 8877"
        )
    )
}