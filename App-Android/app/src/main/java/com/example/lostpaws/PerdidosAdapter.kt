package com.example.lostpaws

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

class PerdidosAdapter(
    private val context: Context,
    private val perdidasList: List<Perdida>,
    private val contactarListener: (Perdida) -> Unit
) : RecyclerView.Adapter<PerdidosAdapter.PerdidaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PerdidaViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.perdidoscardview, parent, false)
        return PerdidaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PerdidaViewHolder, position: Int) {
        val perdida = perdidasList[position]
        holder.bind(perdida, contactarListener)
    }

    override fun getItemCount(): Int = perdidasList.size

    inner class PerdidaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imgMascota: ImageView = itemView.findViewById(R.id.imgMascota)
        private val textMascotaId: TextView = itemView.findViewById(R.id.textMascotaId)
        private val textMascotaNombre: TextView = itemView.findViewById(R.id.textMascotaNombre)
        private val textMascotaTipo: TextView = itemView.findViewById(R.id.textMascotaTipo)
        private val textMascotaRaza: TextView = itemView.findViewById(R.id.textMascotaRaza)
        private val textMascotaChip: TextView = itemView.findViewById(R.id.textMascotaChip)
        private val textFechaPerdida: TextView = itemView.findViewById(R.id.textFechaPerdida)
        private val textLugarPerdida: TextView = itemView.findViewById(R.id.textLugarPerdida)
        private val textDescripcion: TextView = itemView.findViewById(R.id.textDescripcion)
        private val textTelefonoContacto: TextView = itemView.findViewById(R.id.textTelefonoContacto)
        private val layoutRecompensa: LinearLayout = itemView.findViewById(R.id.layoutRecompensa)
        private val textRecompensa: TextView = itemView.findViewById(R.id.textRecompensa)
        private val btnContactar: Button = itemView.findViewById(R.id.btnContactar)

        fun bind(
            perdida: Perdida,
            contactarListener: (Perdida) -> Unit
        ) {
            // Asigna la imagen según el tipo de mascota
            val resourceId = when (perdida.foto) {
                "gato.png" -> R.drawable.gato
                "perro.png" -> R.drawable.perro
                "hamster.png" -> R.drawable.hamster
                "pajaro.png" -> R.drawable.pajaro
                "conejo.png" -> R.drawable.conejo
                else -> R.drawable.pinguinoilerna
            }
            imgMascota.setImageResource(resourceId)

            // Asigna los datos de la mascota perdida
            textMascotaId.text = perdida.id
            textMascotaNombre.text = perdida.nombre
            textMascotaTipo.text = perdida.tipo
            textMascotaRaza.text = perdida.raza
            textMascotaChip.text = perdida.chip.capitalize()
            textFechaPerdida.text = perdida.fechaPerdida
            textLugarPerdida.text = perdida.lugarPerdida
            textDescripcion.text = perdida.descripcion
            textTelefonoContacto.text = perdida.telefonoContacto

            // Maneja la recompensa
            if (perdida.hayRecompensa) {
                layoutRecompensa.visibility = View.VISIBLE
                textRecompensa.text = perdida.recompensa
            } else {
                layoutRecompensa.visibility = View.GONE
            }

            // Configura el listener del botón contactar
            btnContactar.setOnClickListener {
                contactarListener(perdida)
            }
        }
    }
}