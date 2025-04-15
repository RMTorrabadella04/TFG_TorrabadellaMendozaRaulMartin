package com.example.lostpaws.Data

// Clase base para todos los centros
open class Centro(
    val nombre: String = "",
    val direccion: String = "",
    val telefono: String = "",
    val email: String = "",
    val tipo: String = "" // "refugio" o "veterinario"
)

// Clase específica para refugios
class Refugio(
    nombre: String = "",
    direccion: String = "",
    telefono: String = "",
    email: String = "",
    val capacidad: Int = 0
) : Centro(nombre, direccion, telefono, email, "refugio")

// Clase específica para veterinarios
class Veterinario(
    nombre: String = "",
    direccion: String = "",
    telefono: String = "",
    email: String = "",
    val puede_operar: Boolean = false
) : Centro(nombre, direccion, telefono, email, "veterinario")