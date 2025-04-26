package com.example.lostpaws

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lostpaws.Data.Veterinario

class VeterinarioAdapter(
    private val context: Context,
    private val veterinarioList: List<Veterinario>,
    private val deleteListener: (Veterinario) -> Unit,
    private val showDeleteButton: Boolean = true
) : RecyclerView.Adapter<VeterinarioAdapter.VeterinarioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VeterinarioViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.veterinariocardview, parent, false)
        return VeterinarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: VeterinarioViewHolder, position: Int) {
        val veterinario = veterinarioList[position]
        holder.bind(veterinario, deleteListener)
    }

    override fun getItemCount(): Int = veterinarioList.size

    inner class VeterinarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreText: TextView = itemView.findViewById(R.id.textVeterinarioNombre)
        private val direccionText: TextView = itemView.findViewById(R.id.textVeterinarioDireccion)
        private val telefonoText: TextView = itemView.findViewById(R.id.textVeterinarioTelefono)
        private val emailText: TextView = itemView.findViewById(R.id.textVeterinarioEmail)
        private val puedeOperarText: TextView = itemView.findViewById(R.id.textVeterinarioPuedeOperar)
        private val deleteButton: Button = itemView.findViewById(R.id.btnEliminarVeterinario)

        fun bind(veterinario: Veterinario, deleteListener: (Veterinario) -> Unit) {
            nombreText.text = veterinario.nombre
            direccionText.text = veterinario.direccion
            telefonoText.text = veterinario.telefono
            emailText.text = veterinario.email
            puedeOperarText.text = if (veterinario.puede_operar) "Sí" else "No"

            if (showDeleteButton) {
                deleteButton.visibility = View.VISIBLE
                deleteButton.setOnClickListener { deleteListener(veterinario) }
            } else {
                deleteButton.visibility = View.GONE
            }
        }
    }
}
