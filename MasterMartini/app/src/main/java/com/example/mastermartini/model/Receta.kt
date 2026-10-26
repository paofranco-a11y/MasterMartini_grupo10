package com.example.mastermartini.model

// Modelo principal de receta y ficha técnica de preparación
data class Receta(
    val id: String,
    val titulo: String,
    val categoria: String, // "Chocolatería", "Pastelería", "Panadería"
    val descripcionCorta: String,
    val tiempoPreparacion: String,
    val dificultad: String,
    val insumos: List<Insumo>,
    val pasos: List<String>
)

// Datos sintéticos iniciales de Master Martini para poblar el catálogo y pruebas
object CatalogoDataSource {
    val recetas = listOf(
        Receta(
            id = "receta_1",
            titulo = "Bombones de Chocolate Caravella",
            categoria = "Chocolatería",
            descripcionCorta = "Bombones brillantes rellenos de ganache suave utilizando cobertura Caravella.",
            tiempoPreparacion = "45 min",
            dificultad = "Intermedia",
            insumos = listOf(
                Insumo("Cobertura de Chocolate Caravella", "500 g"),
                Insumo("Crema vegetal vegetalina", "200 ml"),
                Insumo("Glucosa técnica", "30 g")
            ),
            pasos = listOf(
                "Templar el chocolate Caravella a 31°C.",
                "Encamisar los moldes de policarbonato y retirar el exceso.",
                "Verter el ganache a temperatura ambiente.",
                "Sellar con una capa fina de chocolate y refrigerar por 15 minutos."
            )
        ),
        Receta(
            id = "receta_2",
            titulo = "Torta Selva Negra Tradicional",
            categoria = "Pastelería",
            descripcionCorta = "Bizcocho húmedo de cacao con crema Chantypak y guindas ácidas.",
            tiempoPreparacion = "90 min",
            dificultad = "Avanzada",
            insumos = listOf(
                Insumo("Crema para batir Chantypak", "600 ml"),
                Insumo("Premezcla Bizcocho Cacao Master Martini", "500 g"),
                Insumo("Cerezas amarenas", "200 g")
            ),
            pasos = listOf(
                "Hornear el bizcocho a 180°C durante 30 minutos.",
                "Batir la crema Chantypak a velocidad media hasta lograr picos firmes.",
                "Embeber capas con almíbar y rellenar con Chantypak y cerezas.",
                "Decorar la cubierta con virutas de cobertura bitter."
            )
        ),
        Receta(
            id = "receta_3",
            titulo = "Pan Ciabatta Rústico Artesanal",
            categoria = "Panadería",
            descripcionCorta = "Pan de alta hidratación con corteza crocante y miga abierta alveolada.",
            tiempoPreparacion = "180 min",
            dificultad = "Intermedia",
            insumos = listOf(
                Insumo("Harina de fuerza panadera", "1 kg"),
                Insumo("Levadura seca instantánea", "10 g"),
                Insumo("Agua fría filtrada", "800 ml"),
                Insumo("Sal fina de mesa", "20 g")
            ),
            pasos = listOf(
                "Mezclar harina, agua y levadura hasta formar una masa homogénea.",
                "Realizar 3 pliegues cada 30 minutos para desarrollar gluten.",
                "Fermentar en bloque durante 2 horas.",
                "Cortar piezas rectangulares y hornear a 230°C con vapor."
            )
        )
    )
}