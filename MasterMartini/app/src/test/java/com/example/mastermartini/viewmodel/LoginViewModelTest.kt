package com.example.mastermartini.viewmodel

import android.app.Application
import com.example.mastermartini.data.model.PerfilNegocio
import com.example.mastermartini.data.repository.NegocioRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest : StringSpec({

    val dispatcher = UnconfinedTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(dispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    val mockApp = mockk<Application>(relaxed = true)
    val perfilValido = PerfilNegocio(
        razonSocial = "Dulce Arte",
        rut = "12.345.678-5",
        rubro = "Pastelería",
        telefono = "+56 912345678",
        email = "contacto@dulcearte.cl",
        fotoUri = null
    )

    // 1. Camino feliz: formulario válido y credenciales coinciden con DataStore
    "Camino feliz: login exitoso cuando RUT y correo coinciden con el perfil guardado" {
        val mockRepo = mockk<NegocioRepository>(relaxed = true)
        every { mockRepo.perfilNegocioFlow } returns flowOf(perfilValido)
        coEvery { mockRepo.guardarPerfilNegocio(any()) } returns Unit

        runTest(dispatcher) {
            val vm = LoginViewModel(mockApp, mockRepo, dispatcher)
            vm.onRutChange("12.345.678-5")
            vm.onEmailChange("contacto@dulcearte.cl")

            vm.uiState.value.isFormValid shouldBe true
            vm.iniciarSesion()

            vm.uiState.value.loginExitoso shouldBe true
            vm.uiState.value.errorAutenticacion shouldBe null
            coVerify(exactly = 1) { mockRepo.guardarPerfilNegocio(perfilValido) }
        }
    }

    // 2. Camino malo: RUT o correo inválidos / credenciales no coinciden
    "Camino malo: marca error de validación con datos inválidos y rechaza credenciales incorrectas" {
        val mockRepo = mockk<NegocioRepository>(relaxed = true)
        every { mockRepo.perfilNegocioFlow } returns flowOf(perfilValido)

        runTest(dispatcher) {
            val vm = LoginViewModel(mockApp, mockRepo, dispatcher)
            vm.onRutChange("12.345.678-0")
            vm.onEmailChange("correo-malo")

            vm.uiState.value.isFormValid shouldBe false
            vm.uiState.value.errorRut shouldBe "RUT inválido (ej: 12.345.678-5)"
            vm.uiState.value.errorEmail shouldBe "Formato de correo no válido"

            // Con formato válido pero distinto al guardado
            vm.onRutChange("12.345.670-k")
            vm.onEmailChange("otro@dulcearte.cl")
            vm.iniciarSesion()

            vm.uiState.value.loginExitoso shouldBe false
            vm.uiState.value.errorAutenticacion shouldBe "RUT o correo no coinciden con la empresa registrada."
        }
    }

    // 3. Caso borde (algo que podría suceder): no hay empresa registrada o el usuario ingresa mayúsculas/espacios
    "Caso borde: muestra mensaje claro si no hay empresa registrada en el dispositivo" {
        val mockRepo = mockk<NegocioRepository>(relaxed = true)
        every { mockRepo.perfilNegocioFlow } returns flowOf(null)

        runTest(dispatcher) {
            val vm = LoginViewModel(mockApp, mockRepo, dispatcher)
            vm.onRutChange(" 12345678-5 ")
            vm.onEmailChange("contacto@dulcearte.cl")
            vm.iniciarSesion()

            vm.uiState.value.loginExitoso shouldBe false
            vm.uiState.value.errorAutenticacion shouldBe "No hay una empresa registrada en este dispositivo."
        }
    }
})
