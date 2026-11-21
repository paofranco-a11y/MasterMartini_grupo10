package com.example.mastermartini

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.runtime.remember
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mastermartini.data.model.RecetaRemote
import com.example.mastermartini.data.repository.RecetaRemoteRepository
import com.example.mastermartini.ui.screens.RecetasRemoteScreen
import com.example.mastermartini.ui.theme.MasterMartiniTheme
import com.example.mastermartini.viewmodel.RecetaRemoteViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas de interfaz con Compose UI Test para MasterMartini.
 */
@RunWith(AndroidJUnit4::class)
class MasterMartiniUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    /**
     * Repositorio falso en memoria: evita depender de internet y de MockAPI.
     * Nunca llama a la API real porque todos los métodos usados están sobrescritos.
     */
    private class FakeRecetaRemoteRepository : RecetaRemoteRepository() {
        private val datos = mutableListOf(
            RecetaRemote(
                id = "1",
                titulo = "Torta de Prueba",
                descripcion = "Receta sembrada para el test",
                categoria = "Pastelería",
                tiempoMinutos = 45,
                dificultad = "Intermedia"
            )
        )

        override suspend fun obtenerRecetas(): List<RecetaRemote> = datos.toList()

        override suspend fun crearReceta(receta: RecetaRemote): RecetaRemote {
            val creada = receta.copy(id = (datos.size + 1).toString())
            datos.add(creada)
            return creada
        }

        override suspend fun actualizarReceta(id: String, receta: RecetaRemote): RecetaRemote {
            val actualizada = receta.copy(id = id)
            val indice = datos.indexOfFirst { it.id == id }
            if (indice >= 0) datos[indice] = actualizada
            return actualizada
        }

        override suspend fun eliminarReceta(id: String): RecetaRemote {
            val eliminada = datos.first { it.id == id }
            datos.removeAll { it.id == id }
            return eliminada
        }
    }

    /**
     * La app puede abrir en Home si ya hay sesión guardada en DataStore.
     * Este helper cierra la sesión en ese caso para dejar siempre visible el Login.
     */
    private fun asegurarPantallaLogin() {
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodesWithTagSafe("input_rut") ||
                composeTestRule.onAllNodesWithTextSafe("Cerrar Sesión")
        }
        if (composeTestRule.onAllNodesWithTextSafe("Cerrar Sesión")) {
            composeTestRule.onNodeWithText("Cerrar Sesión").performClick()
            composeTestRule.waitUntil(timeoutMillis = 10_000) {
                composeTestRule.onAllNodesWithTagSafe("input_rut")
            }
        }
    }

    private fun androidx.compose.ui.test.junit4.ComposeTestRule.onAllNodesWithTagSafe(tag: String): Boolean =
        onAllNodes(androidx.compose.ui.test.hasTestTag(tag)).fetchSemanticsNodes().isNotEmpty()

    private fun androidx.compose.ui.test.junit4.ComposeTestRule.onAllNodesWithTextSafe(texto: String): Boolean =
        onAllNodes(androidx.compose.ui.test.hasText(texto)).fetchSemanticsNodes().isNotEmpty()

    /**
     * a) La pantalla inicial (Login) carga y muestra sus campos y su botón.
     */
    @Test
    fun pantallaInicial_muestraCamposYBotonDeLogin() {
        asegurarPantallaLogin()

        composeTestRule.onNodeWithTag("input_rut").assertIsDisplayed()
        composeTestRule.onNodeWithTag("input_email").assertIsDisplayed()
        composeTestRule.onNodeWithTag("btn_iniciar_sesion").assertIsDisplayed()
    }

    /**
     * b) Al escribir en los campos del Login, el texto queda ingresado.
     */
    @Test
    fun login_escribirEnCampos_textoQuedaIngresado() {
        asegurarPantallaLogin()

        composeTestRule.onNodeWithTag("input_rut").performTextInput("12345678-5")
        composeTestRule.onNodeWithTag("input_email").performTextInput("test@correo.cl")

        composeTestRule.onNodeWithTag("input_rut").assertTextContains("12345678-5")
        composeTestRule.onNodeWithTag("input_email").assertTextContains("test@correo.cl")
    }

    /**
     * c) RecetasRemoteScreen con un repositorio falso: lista la receta inicial,
     * abre el formulario, crea una receta nueva y la muestra en la lista.
     */
    @Test
    fun recetasRemote_crearRecetaConRepositorioFalso_apareceEnLista() {
        composeTestRule.runOnUiThread {
            composeTestRule.activity.setContent {
                MasterMartiniTheme {
                    val viewModel = remember { RecetaRemoteViewModel(FakeRecetaRemoteRepository()) }
                    RecetasRemoteScreen(
                        onNavigateBack = {},
                        viewModel = viewModel
                    )
                }
            }
        }

        // La lista carga la receta sembrada por el repositorio falso
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTagSafe("card_receta_1")
        }
        composeTestRule.onNodeWithTag("lista_recetas_remotas").assertIsDisplayed()
        composeTestRule.onNodeWithTag("card_receta_1").assertIsDisplayed()

        // Abre el formulario de nueva receta
        composeTestRule.onNodeWithTag("fab_agregar_receta").performClick()
        composeTestRule.onNodeWithTag("input_titulo_receta").assertIsDisplayed()
        composeTestRule.onNodeWithTag("input_categoria_receta").assertIsDisplayed()
        composeTestRule.onNodeWithTag("input_dificultad_receta").assertIsDisplayed()

        // Completa los campos de texto y guarda
        composeTestRule.onNodeWithTag("input_titulo_receta").performTextInput("Pan de Prueba")
        composeTestRule.onNodeWithTag("input_descripcion_receta").performTextInput("Creado desde el test")
        composeTestRule.onNodeWithTag("btn_guardar_receta").performClick()

        // La receta nueva (id "2" en el repositorio falso) aparece en la lista
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTagSafe("card_receta_2")
        }
        composeTestRule.onNodeWithTag("card_receta_2").assertIsDisplayed()
    }
}
