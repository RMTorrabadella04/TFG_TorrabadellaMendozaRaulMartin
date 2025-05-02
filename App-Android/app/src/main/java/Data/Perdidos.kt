package Data

data class Perdida(
    val id: String = "",
    val mascotaId: String = "",
    val mascotaNombre: String = "",
    val mascotaTipo: String = "",
    val mascotaRaza: String = "",
    val mascotaChip: String = "",
    val mascotaFoto: String = "",
    val fechaReporte: String = "",
    val fechaPerdida: String = "",
    val lugarPerdida: String = "",
    val descripcion: String = "",
    val telefonoContacto: String = "",
    val hayRecompensa: Boolean = false,
    val recompensa: String = "0",
    val userEmail: String = "",
    val estado: String = "PERDIDO" // Puede ser: PERDIDO, ENCONTRADO, CERRADO
)