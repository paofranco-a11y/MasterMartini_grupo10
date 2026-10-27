package com.example.mastermartini.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CameraUtils {
    /**
     * Crea un archivo .jpg vacío en el directorio de caché de la app
     * y genera su URI segura utilizando FileProvider.
     */
    fun crearUriTemporal(context: Context): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val archivoTemporal = File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            context.cacheDir
        )
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            archivoTemporal
        )
    }
}

fun crearUriTemporal(context: Context): Uri = CameraUtils.crearUriTemporal(context)
