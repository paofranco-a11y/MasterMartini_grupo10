package com.example.mastermartini.util

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class RutValidadorTest : StringSpec({

    // 1. Camino feliz: RUT válido con formato estándar
    "Camino feliz: validarRut debe retornar true para un RUT chileno válido con puntos y guion" {
        RutValidador.validarRut("12.345.678-5") shouldBe true
        RutValidador.formatearRut("123456785") shouldBe "12.345.678-5"
    }

    // 2. Camino malo: RUT con dígito verificador incorrecto o letras en el cuerpo
    "Camino malo: validarRut debe retornar false cuando el dígito verificador no corresponde o tiene texto inválido" {
        RutValidador.validarRut("12.345.678-9") shouldBe false
        RutValidador.validarRut("12.34A.678-5") shouldBe false
        RutValidador.validarRut("123-4") shouldBe false
    }

    // 3. Caso borde (algo que podría suceder): RUT terminado en 'k' minúscula y espacios alrededor
    "Caso borde: validarRut debe aceptar dígito verificador 'k' minúscula, sin puntos y con espacios" {
        RutValidador.validarRut("  12345670-k ") shouldBe true
        RutValidador.limpiarRut(" 12.345.670-k ") shouldBe "12345670K"
        RutValidador.formatearRut("1") shouldBe "1"
    }
})
