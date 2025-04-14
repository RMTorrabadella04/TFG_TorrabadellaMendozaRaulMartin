package com.example.lostpaws

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Usuarios : Fragment() {

    private var userId: String? = null
    private var userName: String? = null
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val email = obtenerSesion(requireContext())

        if (email != null) {
            obtenerDatosUsuario(email)
        } else {
            Toast.makeText(requireContext(), "No hay sesión iniciada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_usuario, container, false)
    }

    private fun obtenerSesion(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("Sesion", Context.MODE_PRIVATE)
        return sharedPreferences.getString("email", null) // Devuelve el email guardado o null si no está presente
    }

    private fun obtenerDatosUsuario(email: String) {
        val database = FirebaseDatabase.getInstance().getReference("users")
        database.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        userId = userSnapshot.key // UID del usuario
                        userName = userSnapshot.child("name").getValue(String::class.java)
                        userEmail = userSnapshot.child("email").getValue(String::class.java)
                    }
                    cambiarTexto()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error en la base de datos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun cambiarTexto() {
        val textoId = requireView().findViewById<TextView>(R.id.textoId)
        textoId.text = "ID: " + userId

        val textoName = requireView().findViewById<TextView>(R.id.textoNombre)
        textoName.text = "Nombre: " + userName

        val textoEmail = requireView().findViewById<TextView>(R.id.textoEmail)
        textoEmail.text = "Email: " + userEmail
    }
}
