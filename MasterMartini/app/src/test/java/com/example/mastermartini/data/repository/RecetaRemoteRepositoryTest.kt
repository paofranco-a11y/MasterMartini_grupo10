package com.example.mastermartini.data.repository

import com.example.mastermartini.data.model.RecetaRemote
import com.example.mastermartini.data.remote.RecetaApiService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import java.io.IOException

class RecetaRemoteRepositoryTest : StringSpec({

    val recetaEjemplo = RecetaRemote(
        id = "1",
        titulo = "Bombones de Chocolate Caravella",
        descripcion = "Ganache suave con cobertura Caravella.",
        categoria = "Chocolatería",
        tiempoMinutos = 45,
        dificultad = "Intermedia"
    )

    // 1. Camino feliz: operaciones CRUD exitosas contra ApiService mockeada
    "Camino feliz: obtener, crear, actualizar y eliminar recetas deben delegar correctamente a la API" {
        val mockApi = mockk<RecetaApiService>()
        val recetaEditada = recetaEjemplo.copy(titulo = "Bombones Caravella Premium")

        coEvery { mockApi.getRecetas() } returns listOf(recetaEjemplo)
        coEvery { mockApi.getRecetaById("1") } returns recetaEjemplo
        coEvery { mockApi.createReceta(recetaEjemplo) } returns recetaEjemplo
        coEvery { mockApi.updateReceta("1", recetaEditada) } returns recetaEditada
        coEvery { mockApi.deleteReceta("1") } returns recetaEjemplo

        val repo = RecetaRemoteRepository(api = mockApi)

        runTest {
            repo.obtenerRecetas() shouldContainExactly listOf(recetaEjemplo)
            repo.obtenerRecetaPorId("1") shouldBe recetaEjemplo
            repo.crearReceta(recetaEjemplo) shouldBe recetaEjemplo
            repo.actualizarReceta("1", recetaEditada) shouldBe recetaEditada
            repo.eliminarReceta("1") shouldBe recetaEjemplo

            coVerify(exactly = 1) { mockApi.getRecetas() }
            coVerify(exactly = 1) { mockApi.createReceta(recetaEjemplo) }
            coVerify(exactly = 1) { mockApi.updateReceta("1", recetaEditada) }
            coVerify(exactly = 1) { mockApi.deleteReceta("1") }
        }
    }

    // 2. Camino malo: falla de red al consultar la API
    "Camino malo: obtenerRecetas debe propagar la excepción cuando falla la conexión" {
        val mockApi = mockk<RecetaApiService>()
        coEvery { mockApi.getRecetas() } throws IOException("Sin conexión a Internet")

        val repo = RecetaRemoteRepository(api = mockApi)

        runTest {
            shouldThrow<IOException> {
                repo.obtenerRecetas()
            }
        }
    }

    // 3. Caso borde (algo que podría suceder): la API responde con lista vacía
    "Caso borde: obtenerRecetas debe retornar lista vacía cuando no hay registros en MockAPI" {
        val mockApi = mockk<RecetaApiService>()
        coEvery { mockApi.getRecetas() } returns emptyList()

        val repo = RecetaRemoteRepository(api = mockApi)

        runTest {
            repo.obtenerRecetas().shouldBeEmpty()
        }
    }
})
