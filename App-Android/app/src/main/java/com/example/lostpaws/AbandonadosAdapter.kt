package com.example.lostpaws

import Data.Abandono
import Data.Perdida
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AbandonadosAdapter(
    private val context: Context,
    private val abandonadosList: List<Abandono>,
    private val contactarListener: (Abandono) -> Unit
) : RecyclerView.Adapter<AbandonadosAdapter.AbandonoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbandonoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.abandonoscardview, parent, false)
        return AbandonoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AbandonoViewHolder, position: Int) {
        val abandono = abandonadosList[position]
        holder.bind(abandono, contactarListener)
    }

    override fun getItemCount(): Int = abandonadosList.size

    inner class AbandonoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imgMascota: ImageView = itemView.findViewById(R.id.imgMascota)
        private val textMascotaId: TextView = itemView.findViewById(R.id.textMascotaId)
        private val textMascotaNombre: TextView = itemView.findViewById(R.id.textMascotaNombre)
        private val textMascotaTipo: TextView = itemView.findViewById(R.id.textMascotaTipo)
        private val textMascotaRaza: TextView = itemView.findViewById(R.id.textMascotaRaza)
        private val textMascotaChip: TextView = itemView.findViewById(R.id.textMascotaChip)
        private val textFechaReporte: TextView = itemView.findViewById(R.id.textFechaReporte)
        private val textFechaAbandono: TextView = itemView.findViewById(R.id.textFechaAbandono)
        private val textRefugioId: TextView = itemView.findViewById(R.id.textRefugioId)
        private val btnContactar: Button = itemView.findViewById(R.id.btnContactar)

        fun bind(
            abandono: Abandono,
            contactarListener: (Abandono) -> Unit
        ) {
            // Asigna la imagen según el tipo de mascota
            val resourceId = when (abandono.foto) {
                "gato.png" -> R.drawable.gato
                "perro.png" -> R.drawable.perro
                "hamster.png" -> R.drawable.hamster
                "pajaro.png" -> R.drawable.pajaro
                "conejo.png" -> R.drawable.conejo
                else -> R.drawable.pinguinoilerna
            }
            imgMascota.setImageResource(resourceId)

            // Asigna los datos de la mascota perdida
            textMascotaId.text = abandono.id
            textMascotaNombre.text = abandono.nombre
            textMascotaTipo.text = abandono.tipo
            textMascotaRaza.text = abandono.raza
            textMascotaChip.text = abandono.chip.capitalize()
            textFechaReporte.text = abandono.fechaReporte
            textFechaAbandono.text = abandono.fechaAbandono
            textRefugioId.text = abandono.refugioId

            // Configura el listener del botón contactar
            btnContactar.setOnClickListener {
                contactarListener(abandono)
            }
        }
    }
}