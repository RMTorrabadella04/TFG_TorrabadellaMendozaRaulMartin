package com.example.lostpaws

import Data.Usuario
import Data.UsuarioVistaAdmin
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(
    private val context: Context,
    private val userList: List<UsuarioVistaAdmin>,
    private val deleteListener: (UsuarioVistaAdmin) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.usercardview, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user, deleteListener)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val userIdTextView: TextView = itemView.findViewById(R.id.textUserId)
        private val userNameTextView: TextView = itemView.findViewById(R.id.textUserName)
        private val userEmailTextView: TextView = itemView.findViewById(R.id.textUserEmail)
        private val userPasswordTextView: TextView = itemView.findViewById(R.id.textUserPassword) // Contraseña
        private val deleteButton: Button = itemView.findViewById(R.id.btnEliminar)

        fun bind(user: UsuarioVistaAdmin, deleteListener: (UsuarioVistaAdmin) -> Unit) {
            userIdTextView.text = user.id
            userNameTextView.text = user.name
            userEmailTextView.text = user.email
            userPasswordTextView.text = user.password
            deleteButton.setOnClickListener {
                deleteListener(user) // Llamamos a la función de eliminar cuando el botón es presionado
            }
        }
    }
}
