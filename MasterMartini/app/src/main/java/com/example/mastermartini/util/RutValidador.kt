package com.example.mastermartini.util

object RutValidador {

    fun validarRut(rutCompleto: String): Boolean{

        //REGLAS DE VALIDACION
        val rutLimpio = rutCompleto.trim().uppercase()

            //LIMPIEZA DEL TEXTO
            .replace(".","")
            .replace("-","")

        // verifica si el rut tiene minimo 7 digitos, en caso contrario retorna como invalido
        if (rutLimpio.length< 8) return false

        // se separa el cuerpo y el dv del rut
        val cuerpo = rutLimpio.substring(0, rutLimpio.length - 1)
        val dvIngresado = rutLimpio.last()

        // se verifica que el cuerpo este compuesto solo por numeros
        if (!cuerpo.all { it.isDigit() }) return false

        // trae el resultado del calculo
        val dvEsperado = calcularDigitoVerificador (cuerpo)

        return  dvIngresado == dvEsperado

    }

    // FUNCION PARA COMPROBAR LA VERACIDAD DEL DigitoVerificador
    private fun calcularDigitoVerificador(cuerpo: String): Char {
        var suma = 0
        var multiplicador = 2

        // Se recorren los dígitos de derecha a izquierda
        for (i in cuerpo.indices.reversed()) {
            val digito = cuerpo[i].digitToInt()
            suma += digito * multiplicador

            // El conteo va de 2 a 7 y una vez que llegue a siete vuelve a 2
            multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
        }

        val resto = suma % 11
        val resultado = 11 - resto

        return when (resultado) {
            11 -> '0'
            10 -> 'K'
            else -> resultado.digitToChar()
        }
    }

    // funcion para formatear el rut ingresado
    fun formatearRut(rut: String): String {
        val limpio = rut.trim().uppercase()
            .replace(".", "")
            .replace("-", "")

        if (limpio.length < 2) return limpio

        val cuerpo = limpio.substring(0, limpio.length - 1)
        val dv = limpio.last()

        // Invertimos el cuerpo con la funcion .reversed(), separamos en cadenas de maximo 3 digitos separados por . y volvemos a invertir
        val cuerpoFormateado = cuerpo.reversed().chunked(3).joinToString(".").reversed()

        // Finalmente se concatena con el Digito verificador
        return "$cuerpoFormateado-$dv"
    }

}

