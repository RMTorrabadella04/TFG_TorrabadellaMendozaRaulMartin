package com.example.lostpaws

import org.junit.Assert.*

import org.junit.Test

class LogginActivityKtTest {

    @Test
    fun testSinPassword() {
        val resultado = validacionLoginParaTest("test@example.com", "")
        assertFalse(resultado)
    }

    @Test
    fun testEmailInvalido() {
        val resultado = validacionLoginParaTest("correo-mal", "123456")
        assertFalse(resultado)
    }

    @Test
    fun testDatosCorrectos() {
        val resultado = validacionLoginParaTest("test@example.com", "123456")
        assertTrue(resultado)
    }
}