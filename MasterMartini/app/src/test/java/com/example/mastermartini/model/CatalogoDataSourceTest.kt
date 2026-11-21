package com.example.mastermartini.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe

class CatalogoDataSourceTest : StringSpec({

    // 1. Camino feliz: el catálogo contiene recetas válidas filtrables por categoría
    "Camino feliz: CatalogoDataSource contiene recetas con insumos y permite filtrar por Chocolatería" {
        val chocolateria = CatalogoDataSource.recetas.filter { it.categoria == "Chocolatería" }
        chocolateria.shouldNotBeEmpty()
        chocolateria.all { it.insumos.isNotEmpty() && it.pasos.isNotEmpty() } shouldBe true
    }

    // 2. Camino malo: filtrar por una categoría inexistente devuelve lista vacía
    "Camino malo: filtrar por una categoría inexistente retorna lista vacía" {
        val resultado = CatalogoDataSource.recetas.filter { it.categoria == "Bebidas" }
        resultado.shouldBeEmpty()
    }

    // 3. Caso borde: búsqueda parcial ignorando mayúsculas/minúsculas
    "Caso borde: buscar por texto en minúsculas encuentra recetas sin importar las mayúsculas" {
        val resultado = CatalogoDataSource.recetas.filter {
            it.titulo.contains("caravella", ignoreCase = true)
        }
        resultado.size shouldBe 1
        resultado.first().id shouldBe "receta_1"
    }
})
