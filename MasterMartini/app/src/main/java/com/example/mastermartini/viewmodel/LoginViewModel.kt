package com.example.mastermartini.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mastermartini.data.repository.NegocioRepository
import com.example.mastermartini.ui.screens.login.LoginUiState
import com.example.mastermartini.util.RutValidador
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NegocioRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onRutChange(nuevoRut: String) {
        val error = when {
            nuevoRut.isBlank() -> "El RUT no puede quedar en blanco"
            !RutValidador.validar(nuevoRut) -> "RUT inválido (ej: 12.345.678-5)"
            else -> null
        }
        _uiState.update {
            it.copy(
                rut = nuevoRut,
                errorRut = error,
                errorAutenticacion = null
            )
        }
        evaluarValidezFormulario()
    }

    fun onEmailChange(nuevoEmail: String) {
        val patronEmail = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        val error = when {
            nuevoEmail.isBlank() -> "El correo electrónico es requerido"
            !nuevoEmail.matches(patronEmail) -> "Formato de correo no válido"
            else -> null
        }
        _uiState.update {
            it.copy(
                email = nuevoEmail,
                errorEmail = error,
                errorAutenticacion = null
            )
        }
        evaluarValidezFormulario()
    }

    private fun evaluarValidezFormulario() {
        val state = _uiState.value
        val esValido = state.rut.isNotBlank() &&
                state.email.isNotBlank() &&
                state.errorRut == null &&
                state.errorEmail == null
        _uiState.update { it.copy(isFormValid = esValido) }
    }

    fun iniciarSesion() {
        if (!_uiState.value.isFormValid) return

        _uiState.update { it.copy(isLoading = true, errorAutenticacion = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val perfilGuardado = repository.perfilNegocioFlow.firstOrNull()

                if (perfilGuardado == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorAutenticacion = "No hay una empresa registrada en este dispositivo."
                        )
                    }
                    return@launch
                }

                // Normalización para comparación segura (sin puntos ni espacios)
                val rutInput = RutValidador.limpiarRut(_uiState.value.rut)
                val rutGuardado = RutValidador.limpiarRut(perfilGuardado.rut)
                val emailInput = _uiState.value.email.trim().lowercase()
                val emailGuardado = perfilGuardado.email.trim().lowercase()

                if (rutInput == rutGuardado && emailInput == emailGuardado) {
                    // Credenciales válidas: actualizar sesión y notificar
                    repository.guardarPerfilNegocio(perfilGuardado) // Garantiza flag SESION_ACTIVA = true
                    _uiState.update { it.copy(isLoading = false, loginExitoso = true) }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorAutenticacion = "RUT o correo no coinciden con la empresa registrada."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorAutenticacion = "Ocurrió un error al verificar credenciales: ${e.localizedMessage}"
                    )
                }
            }
        }
    }
}
