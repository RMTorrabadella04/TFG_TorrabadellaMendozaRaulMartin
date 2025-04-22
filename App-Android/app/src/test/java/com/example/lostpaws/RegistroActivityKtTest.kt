package com.example.lostpaws

import org.junit.Assert.*

import org.junit.Test

class RegistroActivityKtTest {

    @Test
    fun testPasswordMuyCorta() {
        val resultado = validacionDatosParaTest("test@example.com", "123")
        assertFalse(resultado)
    }

    @Test
    fun testEmailInvalido() {
        val resultado = validacionDatosParaTest("correo-mal", "123456")
        assertFalse(resultado)
    }

    @Test
    fun testDatosCorrectos() {
        val resultado = validacionDatosParaTest("test@example.com", "123456")
        assertTrue(resultado)
    }
}