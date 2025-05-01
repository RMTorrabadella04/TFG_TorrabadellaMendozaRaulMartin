package Data

// Clase de modelo para Mascota
data class Mascota(
    val id: String = "",
    val nombre: String = "",
    val tipo: String = "",
    val raza: String = "",
    val chip: String = "",
    val vacunas: String = "",
    val foto: String = "pinguinoilerna.png",
    val email: String = ""
)