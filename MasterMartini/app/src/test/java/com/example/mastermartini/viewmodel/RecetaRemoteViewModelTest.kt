package com.example.mastermartini.viewmodel

import com.example.mastermartini.data.model.RecetaRemote
import com.example.mastermartini.data.repository.RecetaRemoteRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class RecetaRemoteViewModelTest : StringSpec({

    val dispatcher = UnconfinedTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(dispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    val receta1 = RecetaRemote("1", "Bombones Caravella", "Ganache", "Chocolatería", 45, "Intermedia")
    val receta2 = RecetaRemote("2", "Torta Selva Negra", "Bizcocho cacao", "Pastelería", 90, "Avanzada")

    // 1. Camino feliz: carga inicial, creación, actualización y eliminación exitosas
    "Camino feliz: CRUD completo en RecetaRemoteViewModel actualiza el StateFlow correctamente" {
        val mockRepo = mockk<RecetaRemoteRepository>()
        val receta2Editada = receta2.copy(tiempoMinutos = 100)

        coEvery { mockRepo.obtenerRecetas() } returns listOf(receta1)
        coEvery { mockRepo.crearReceta(receta2) } returns receta2
        coEvery { mockRepo.actualizarReceta("2", receta2Editada) } returns receta2Editada
        coEvery { mockRepo.eliminarReceta("1") } returns receta1

        runTest {
            val vm = RecetaRemoteViewModel(repository = mockRepo)
            vm.recetas.value shouldContainExactly listOf(receta1)
            vm.errorMessage.value shouldBe null
            vm.isLoading.value shouldBe false

            var creadaOk = false
            vm.crearReceta(receta2) { creadaOk = true }
            creadaOk shouldBe true
            vm.recetas.value shouldContainExactly listOf(receta1, receta2)

            vm.actualizarReceta("2", receta2Editada)
            vm.recetas.value shouldContainExactly listOf(receta1, receta2Editada)

            vm.eliminarReceta("1")
            vm.recetas.value shouldContainExactly listOf(receta2Editada)
        }
    }

    // 2. Camino malo: error de red al cargar o crear receta
    "Camino malo: si el repositorio lanza excepción, errorMessage guarda el detalle y no crashea" {
        val mockRepo = mockk<RecetaRemoteRepository>()
        coEvery { mockRepo.obtenerRecetas() } throws RuntimeException("Timeout de red")
        coEvery { mockRepo.crearReceta(any()) } throws RuntimeException("Error 500")

        runTest {
            val vm = RecetaRemoteViewModel(repository = mockRepo)
            vm.recetas.value.shouldBeEmpty()
            vm.errorMessage.value.orEmpty() shouldContain "Timeout de red"
            vm.isLoading.value shouldBe false

            var callbackEjecutado = false
            vm.crearReceta(receta1) { callbackEjecutado = true }
            callbackEjecutado shouldBe false
            vm.errorMessage.value.orEmpty() shouldContain "Error 500"
        }
    }

    // 3. Caso borde (algo que podría suceder): intentar eliminar o actualizar un ID que no está en la lista
    "Caso borde: eliminar o actualizar un ID inexistente en la lista local no altera las recetas actuales" {
        val mockRepo = mockk<RecetaRemoteRepository>()
        coEvery { mockRepo.obtenerRecetas() } returns listOf(receta1)
        coEvery { mockRepo.actualizarReceta("999", any()) } returns receta2
        coEvery { mockRepo.eliminarReceta("999") } returns receta2

        runTest {
            val vm = RecetaRemoteViewModel(repository = mockRepo)
            vm.actualizarReceta("999", receta2)
            vm.eliminarReceta("999")

            vm.recetas.value shouldContainExactly listOf(receta1)
            vm.errorMessage.value shouldBe null
        }
    }
})
