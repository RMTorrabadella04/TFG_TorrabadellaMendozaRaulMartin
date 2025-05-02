package com.example.lostpaws

import Data.Mascota
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class MascotasAdapter(
    private val context: Context,
    private val mascotasList: List<Mascota>,
    private val editListener: (Mascota) -> Unit,
    private val deleteListener: (Mascota) -> Unit,
    private val darPerdidoListener: (Mascota) -> Unit,
    private val abandonarListener: (Mascota) -> Unit
) : RecyclerView.Adapter<MascotasAdapter.MascotaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.mascotacardview, parent, false)
        return MascotaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MascotaViewHolder, position: Int) {
        val mascota = mascotasList[position]
        holder.bind(mascota, editListener, deleteListener, darPerdidoListener)
    }

    override fun getItemCount(): Int = mascotasList.size

    inner class MascotaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imgMascota: ImageView = itemView.findViewById(R.id.imgMascota)
        private val mascotaIdTextView: TextView = itemView.findViewById(R.id.textMascotaId)
        private val mascotaNombreTextView: TextView = itemView.findViewById(R.id.textMascotaNombre)
        private val mascotaTipoTextView: TextView = itemView.findViewById(R.id.textMascotaTipo)
        private val mascotaRazaTextView: TextView = itemView.findViewById(R.id.textMascotaRaza)
        private val mascotaChipTextView: TextView = itemView.findViewById(R.id.textMascotaChip)
        private val mascotaVacunasTextView: TextView = itemView.findViewById(R.id.textMascotaVacunas)
        private val editarButton: Button = itemView.findViewById(R.id.btnEditar)
        private val eliminarButton: Button = itemView.findViewById(R.id.btnEliminar)
        private val darPorPerdidoButton: Button = itemView.findViewById(R.id.btnDarPerdido)
        private val abandonarButton: Button = itemView.findViewById(R.id.btnAbandonar)

        fun bind(
            mascota: Mascota,
            editListener: (Mascota) -> Unit,
            deleteListener: (Mascota) -> Unit,
            darPerdidoListener: (Mascota) -> Unit
        ) {
            // Asigna datos
            val resourceId = when (mascota.foto) {
                "gato.png" -> R.drawable.gato
                "perro.png" -> R.drawable.perro
                "hamster.png" -> R.drawable.hamster
                "pajaro.png" -> R.drawable.pajaro
                "conejo.png" -> R.drawable.conejo
                else -> R.drawable.pinguinoilerna
            }
            imgMascota.setImageResource(resourceId)
            mascotaIdTextView.text = mascota.id
            mascotaNombreTextView.text = mascota.nombre
            mascotaTipoTextView.text = mascota.tipo
            mascotaRazaTextView.text = mascota.raza
            mascotaChipTextView.text = mascota.chip.capitalize()
            mascotaVacunasTextView.text = mascota.vacunas

            // Ajusta texto inicial del botón según si existe reporte en Firebase
            val perdidaRef = FirebaseDatabase.getInstance().getReference("perdida")
            perdidaRef.orderByChild("mascotaId").equalTo(mascota.id)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        darPorPerdidoButton.text = if (snapshot.exists()) "CANCELAR REPORTE" else "DAR POR PERDIDO"
                    }
                    override fun onCancelled(error: DatabaseError) { /* Ignorar error */ }
                })

            // Listeners
            editarButton.setOnClickListener { editListener(mascota) }
            eliminarButton.setOnClickListener { deleteListener(mascota) }
            darPorPerdidoButton.setOnClickListener {
                darPerdidoListener(mascota)
                val nuevoTexto = if (darPorPerdidoButton.text == "DAR POR PERDIDO") "CANCELAR REPORTE" else "DAR POR PERDIDO"
                darPorPerdidoButton.text = nuevoTexto
            }
            abandonarButton.setOnClickListener{
                abandonarListener(mascota)
            }
        }
    }
}
