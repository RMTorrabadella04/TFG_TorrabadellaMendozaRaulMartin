package Data

data class Mensaje(
    val id: String = "",
    val texto: String = "",
    val emisorId: String = "",
    val emisorNombre: String = "",
    val destinatarioId: String = "",
    val destinatarioNombre: String = "",
    val timestamp: Long = 0
)