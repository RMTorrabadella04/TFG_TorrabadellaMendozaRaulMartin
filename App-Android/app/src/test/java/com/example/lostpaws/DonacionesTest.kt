package com.example.lostpaws

import org.junit.Assert.*

import org.junit.Test
import java.util.Calendar
import com.example.lostpaws.Donaciones.*

class DonacionesTest {

    private val donaciones = Donaciones() // Instancia de la clase

    @Test
    fun testNumeroEmisorCorrecto() {
        val resultado = donaciones.validacionDatosDonacionParaTest(
            "1234567890123456",
            "12/25",
            "123",
            "1234567890123456"
        )
        assertTrue(resultado)
    }

    @Test
    fun testNumeroEmisorIncorrecto() {
        val resultado = donaciones.validacionDatosDonacionParaTest(
            "12345",
            "12/25",
            "123",
            "1234567890123456"
        )
        assertFalse(resultado)
    }

    @Test
    fun testNumeroDestinatarioIncorrecto() {
        val resultado = donaciones.validacionDatosDonacionParaTest(
            "1234567890123456",
            "12/25",
            "123",
            "12345"
        )
        assertFalse(resultado)
    }

    @Test
    fun testCVVIncorrecto() {
        val resultado = donaciones.validacionDatosDonacionParaTest(
            "1234567890123456",
            "12/25",
            "12",
            "1234567890123456"
        )
        assertFalse(resultado)
    }

    @Test
    fun testFormatoFechaCaducidadIncorrecto() {
        val resultado = donaciones.validacionDatosDonacionParaTest(
            "1234567890123456",
            "1225",
            "123",
            "1234567890123456"
        )
        assertFalse(resultado)
    }

    @Test
    fun testMesFechaCaducidadIncorrecto() {
        val resultado = donaciones.validacionDatosDonacionParaTest(
            "1234567890123456",
            "13/25",
            "123",
            "1234567890123456"
        )
        assertFalse(resultado)
    }

    @Test
    fun testAñoFechaCaducidadCaducado() {
        val añoActual = Calendar.getInstance().get(Calendar.YEAR) % 100
        val resultado = donaciones.validacionDatosDonacionParaTest(
            "1234567890123456",
            "12/${añoActual - 1}",
            "123",
            "1234567890123456"
        )
        assertFalse(resultado)
    }

    @Test
    fun testDatosCompletos() {
        val añoActual = Calendar.getInstance().get(Calendar.YEAR) % 100
        val resultado = donaciones.validacionDatosDonacionParaTest(
            "1234567890123456",
            "12/${añoActual + 1}",
            "123",
            "1234567890123456"
        )
        assertTrue(resultado)
    }

    @Test
    fun testFechaConLetras() {
        val resultado = donaciones.validacionDatosDonacionParaTest(
            "1234567890123456",
            "ab/cd",
            "123",
            "1234567890123456"
        )
        assertFalse(resultado)
    }
}
