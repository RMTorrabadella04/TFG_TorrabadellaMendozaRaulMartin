package Data

data class Abandono(
    val id: String,
    val nombre: String,
    val tipo: String,
    val raza: String,
    val chip: String,
    val foto: String,
    val fechaReporte: String,
    val fechaAbandono: String,
    val refugioId: String,
    val userEmail: String
)