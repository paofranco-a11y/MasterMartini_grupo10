package com.example.mastermartini.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mastermartini.data.model.RecetaRemote
import com.example.mastermartini.data.repository.RecetaRemoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar el listado y operaciones CRUD remotas de recetas (MockAPI.io).
 */
open class RecetaRemoteViewModel(
    private val repository: RecetaRemoteRepository = RecetaRemoteRepository()
) : ViewModel() {

    private val _recetas = MutableStateFlow<List<RecetaRemote>>(emptyList())
    open val recetas: StateFlow<List<RecetaRemote>> = _recetas.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        cargarRecetas()
    }

    open fun cargarRecetas() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _recetas.value = repository.obtenerRecetas()
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar recetas: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    open fun crearReceta(receta: RecetaRemote, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val creada = repository.crearReceta(receta)
                _recetas.value = _recetas.value + creada
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Error al crear receta: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    open fun actualizarReceta(id: String, receta: RecetaRemote, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val actualizada = repository.actualizarReceta(id, receta)
                _recetas.value = _recetas.value.map { if (it.id == id) actualizada else it }
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar receta: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    open fun eliminarReceta(id: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.eliminarReceta(id)
                _recetas.value = _recetas.value.filterNot { it.id == id }
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar receta: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
