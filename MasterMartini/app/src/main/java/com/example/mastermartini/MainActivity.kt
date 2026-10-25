package com.example.mastermartini

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mastermartini.ui.screens.RegistroScreen
import com.example.mastermartini.ui.theme.MasterMartiniTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Usa el tema de la aplicación
            MasterMartiniTheme {
                RegistroScreen(
                    onRegistroExitoso = {
                        // Feedback inmediato para confirmar que el botón funcionó
                        Toast.makeText(
                            this,
                            "¡Empresa registrada exitosamente!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            }
        }
    }
}