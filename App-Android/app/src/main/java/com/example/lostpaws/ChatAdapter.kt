package com.example.lostpaws

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import Data.Chat

class ChatAdapter(
    private val context: Context,
    private val chatsList: List<Chat>,
    private val onChatClickListener: (Chat) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    // Obtener el email del usuario actual desde SharedPreferences
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("Sesion", Context.MODE_PRIVATE)
    private val currentUserEmail: String = sharedPreferences.getString("email", "") ?: ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chatcardview, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatsList[position]
        holder.bind(chat)
        holder.itemView.setOnClickListener {
            onChatClickListener(chat)
        }
    }

    override fun getItemCount(): Int = chatsList.size

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreTextView: TextView = itemView.findViewById(R.id.textNombre)
        private val tipoTextView: TextView = itemView.findViewById(R.id.textTipo)

        fun bind(chat: Chat) {

            if (currentUserEmail == chat.duenyoEmail) {
                // Si el usuario actual es el dueño, mostrar información del usuario
                nombreTextView.text = chat.usuarioName
                tipoTextView.text = chat.usuarioTipo // Ahora usando el nombre de campo corregido
            } else {
                // Si el usuario actual es el usuario, mostrar información del dueño
                nombreTextView.text = chat.duenyoName
                tipoTextView.text = chat.duenyoTipo
            }
        }
    }
}
