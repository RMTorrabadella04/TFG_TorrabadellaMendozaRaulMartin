package Data

data class Perdida(
    val id: String,
    val nombre: String,
    val tipo: String,
    val raza: String,
    val chip: String,
    val foto: String,
    val fechaPerdida: String,
    val lugarPerdida: String,
    val descripcion: String,
    val telefonoContacto: String,
    val hayRecompensa: Boolean,
    val duenyo: String,
    val recompensa: String
)