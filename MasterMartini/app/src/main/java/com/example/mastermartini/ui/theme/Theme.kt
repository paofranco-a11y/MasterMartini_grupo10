package com.example.mastermartini.ui.theme

import android.app.Activity
import android.os.Build
import android.view.Window
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


private val LightColorScheme = lightColorScheme(

    primary = ColorInstitucional,
    onPrimary = ColorTextoAlterno,
    primaryContainer = ColorInstitucionalContenedor,
    onPrimaryContainer = ColorInstitucionalOscuro,
    secondary = ColorSecundario,
    onSecondary = ColorTextoAlterno,
    secondaryContainer = ColorSecundarioContenedor,
    background = FondoPantalla,
    onBackground = ColorTexto,
    surface = FondoSuperficie,
    onSurface = ColorTexto,
    error = ColorError,
    onError = ColorTextoAlterno,
    outline = ColorBorde
)

@Composable
fun MasterMartiniTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {


    val colorScheme = LightColorScheme
    val view= LocalView.current


    if (!view.isInEditMode){
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {

                it.statusBarColor= colorScheme.primary.toArgb()
                WindowCompat.getInsetsController(it,view).isAppearanceLightStatusBars = false)
            }

        }

    }



    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )

}