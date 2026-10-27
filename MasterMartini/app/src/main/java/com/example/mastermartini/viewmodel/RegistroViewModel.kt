package com.example.mastermartini.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.mastermartini.model.PerfilNegocio
import com.example.mastermartini.util.RutValidador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegistroViewModel : ViewModel() {


    private val _uiState = MutableStateFlow(RegistroUiState())

    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    // Opciones para escoger en el menu desplegable
    val rubrosDisponibles = listOf(
        "Panadería",
        "Pastelería",
        "Chocolatería",
        "Heladería",
        "Masas y Pizzería"
    )

    // funciones para validar los campos de entrada

    fun onRazonSocialChange(nuevoTexto: String) {
        val error = when {
            nuevoTexto.isBlank() -> "La razón social es obligatoria"
            nuevoTexto.trim().length < 3 -> "Debe tener al menos 3 caracteres"
            else -> null
        }
        _uiState.update { it.copy(razonSocial = nuevoTexto, errorRazonSocial = error) }
        evaluarFormularioCompleto()
    }

    fun onRutChange(nuevoTexto: String) {
        val error = when {
            nuevoTexto.isBlank() -> "El RUT es obligatorio"
            !RutValidador.validarRut(nuevoTexto) -> "RUT invalido, debe tener el formato (ej: 12.345.678-5)"
            else -> null
        }
        _uiState.update { it.copy(rut = nuevoTexto, errorRut = error) }
        evaluarFormularioCompleto()
    }

    fun onRubroChange(nuevoRubro: String) {
        val error = if (nuevoRubro.isBlank()) "Debe seleccionar un rubro" else null
        _uiState.update {
            it.copy(
                rubro = nuevoRubro,
                errorRubro = error,
                isDropdownExpanded = false
            )
        }
        evaluarFormularioCompleto()
    }

    fun onDropdownExpandedChange(expanded: Boolean) {
        _uiState.update { it.copy(isDropdownExpanded = expanded) }
    }

    fun onTelefonoChange(nuevoTexto: String) {
        // Filtramos para aceptar solo números y máximo 9 dígitos
        val soloNumeros = nuevoTexto.filter { it.isDigit() }.take(9)
        val error = when {
            soloNumeros.isBlank() -> "El teléfono es obligatorio"
            soloNumeros.length < 9 -> "Debe tener exactamente 9 dígitos"
            !soloNumeros.startsWith("9") -> "Debe comenzar con el dígito 9"
            else -> null
        }
        _uiState.update { it.copy(telefono = soloNumeros, errorTelefono = error) }
        evaluarFormularioCompleto()
    }

    fun onEmailChange(nuevoTexto: String) {
        val textoLimpio = nuevoTexto.trim()
        val error = when {
            textoLimpio.isBlank() -> "El correo es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(textoLimpio).matches() -> "Formato de correo no válido"
            else -> null
        }
        _uiState.update { it.copy(email = textoLimpio, errorEmail = error) }
        evaluarFormularioCompleto()
    }

    // Actualiza la URI de la fotografía capturada
    fun onFotoCapturada(uri: String) {
        _uiState.update { it.copy(fotoUri = uri) }
    }

    // bloque para habilitar o deshabilitar el boton de registro

    private fun evaluarFormularioCompleto() {
        _uiState.update { actual ->
            val camposLlenos = actual.razonSocial.isNotBlank() &&
                    actual.rut.isNotBlank() &&
                    actual.rubro.isNotBlank() &&
                    actual.telefono.isNotBlank() &&
                    actual.email.isNotBlank()

            val sinErrores = actual.errorRazonSocial == null &&
                    actual.errorRut == null &&
                    actual.errorRubro == null &&
                    actual.errorTelefono == null &&
                    actual.errorEmail == null

            actual.copy(isFormValid = camposLlenos && sinErrores)
        }
    }

    fun registrar() {
        if (!_uiState.value.isFormValid) return

        val perfil = PerfilNegocio(
            razonSocial = _uiState.value.razonSocial.trim(),
            rutEmpresa = RutValidador.formatearRut(_uiState.value.rut),
            rubro = _uiState.value.rubro,
            telefono = _uiState.value.telefono,
            email = _uiState.value.email.trim(),
            fotoUri = _uiState.value.fotoUri
        )

        // Marcamos el registro como exito para que la UI pueda navegar o mostrar confirmación
        _uiState.update { it.copy(registroExitoso = true) }
    }
}
