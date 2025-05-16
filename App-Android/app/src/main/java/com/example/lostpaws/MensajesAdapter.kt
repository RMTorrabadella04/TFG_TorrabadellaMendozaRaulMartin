package com.example.lostpaws

import Data.Mensaje
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class MensajesAdapter(
    private val context: Context,
    private val mensajes: List<Mensaje>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_MENSAJE_ENVIADO = 1
    private val VIEW_TYPE_MENSAJE_RECIBIDO = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_MENSAJE_ENVIADO) {
            val view = LayoutInflater.from(context).inflate(R.layout.mensajeenviado, parent, false)
            EnviadoViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.mensajerecibido, parent, false)
            RecibidoViewHolder(view)
        }
    }

    override fun getItemCount(): Int = mensajes.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mensaje = mensajes[position]

        if (holder.itemViewType == VIEW_TYPE_MENSAJE_ENVIADO) {
            (holder as EnviadoViewHolder).bind(mensaje)
        } else {
            (holder as RecibidoViewHolder).bind(mensaje)
        }
    }

    // Compara para ver quien envia el mensaje

    override fun getItemViewType(position: Int): Int {
        val mensaje = mensajes[position]

        var currentEmail: String = getUserEmail()

        var currentUserId: String = ""

        obtenerDatosUsuario(currentEmail) { usuario ->
            currentUserId=usuario
        }

        return if (mensaje.emisorId == currentUserId) {
            VIEW_TYPE_MENSAJE_ENVIADO
        } else {
            VIEW_TYPE_MENSAJE_RECIBIDO
        }
    }

    inner class EnviadoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textoMensaje: TextView = itemView.findViewById(R.id.textoMensaje)
        private val horaEnviado: TextView = itemView.findViewById(R.id.textoHora)

        fun bind(mensaje: Mensaje) {
            textoMensaje.text = mensaje.texto
            horaEnviado.text = formatearFecha(mensaje.timestamp)
        }
    }

    inner class RecibidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textoMensaje: TextView = itemView.findViewById(R.id.textoMensaje)
        private val horaRecibido: TextView = itemView.findViewById(R.id.textoHora)

        fun bind(mensaje: Mensaje) {
            textoMensaje.text = mensaje.texto
            horaRecibido.text = formatearFecha(mensaje.timestamp)
        }
    }

    private fun formatearFecha(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    // Uso esto para saber quien es el usuario actual y para ver quien es el que envio el mensaje para ponerle un layout de mensaje u otro

    private fun getUserEmail(): String {
        val sharedPreferences = context.getSharedPreferences("Sesion", Context.MODE_PRIVATE)
        return sharedPreferences.getString("email", "") ?: ""
    }

    private fun obtenerDatosUsuario(email: String, callback: (String) -> Unit) {
        val database = FirebaseDatabase.getInstance().getReference("users")
        database.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var currentUserId: String = ""
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        currentUserId = userSnapshot.key.toString()
                        break
                    }
                    callback(currentUserId)
                } else {
                    callback("")
                    Log.d("ObtenerDatosUsuario", "No se encontró usuario con email: $email")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error en la base de datos: ${error.message}", Toast.LENGTH_SHORT).show()
                callback("")
                Log.e("ObtenerDatosUsuario", "Error al buscar usuario: ${error.message}")
            }
        })
    }

}