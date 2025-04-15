package com.example.lostpaws

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lostpaws.Data.Refugio

class RefugioAdapter(
    private val context: Context,
    private val refugioList: List<Refugio>,
    private val deleteListener: (Refugio) -> Unit
) : RecyclerView.Adapter<RefugioAdapter.RefugioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RefugioViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.refugiocardview, parent, false)
        return RefugioViewHolder(view)
    }

    override fun onBindViewHolder(holder: RefugioViewHolder, position: Int) {
        val refugio = refugioList[position]
        holder.bind(refugio, deleteListener)
    }

    override fun getItemCount(): Int = refugioList.size

    inner class RefugioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreText: TextView = itemView.findViewById(R.id.textRefugioNombre)
        private val direccionText: TextView = itemView.findViewById(R.id.textRefugioDireccion)
        private val telefonoText: TextView = itemView.findViewById(R.id.textRefugioTelefono)
        private val emailText: TextView = itemView.findViewById(R.id.textRefugioEmail)
        private val capacidadText: TextView = itemView.findViewById(R.id.textRefugioCapacidad)
        private val deleteButton: Button = itemView.findViewById(R.id.btnEliminarRefugio)

        fun bind(refugio: Refugio, deleteListener: (Refugio) -> Unit) {
            nombreText.text = refugio.nombre
            direccionText.text = refugio.direccion
            telefonoText.text = refugio.telefono
            emailText.text = refugio.email
            capacidadText.text = "Capacidad: ${refugio.capacidad}"
            deleteButton.setOnClickListener { deleteListener(refugio) }
        }
    }
}
