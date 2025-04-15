package com.example.lostpaws

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditarUsuario : Fragment() {

    private var fragmentChangeListener: OnFragmentChangeListener? = null
    private var userId: String? = null
    private var userName: String? = null
    private var userEmail: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_editar_usuario, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textId = view.findViewById<TextView>(R.id.textId)
        val editTextNombre = view.findViewById<EditText>(R.id.editTextNombre)
        val editTextEmail = view.findViewById<EditText>(R.id.editTextEmail)
        val btnCambiar = view.findViewById<Button>(R.id.btnCambiar)

        val email = obtenerSesion(requireContext())

        if (email != null) {
            obtenerDatosUsuario(email) { id, nombre, correo ->
                userId = id
                userName = nombre
                userEmail = correo

                // Mostramos los datos actuales en los campos
                textId.text = "ID: $userId"
                editTextNombre.setText(userName)
                editTextEmail.setText(userEmail)
            }
        } else {
            Toast.makeText(requireContext(), "No hay sesión iniciada", Toast.LENGTH_SHORT).show()
        }

        btnCambiar.setOnClickListener {
            val nuevoNombre = editTextNombre.text.toString().trim()
            val nuevoEmail = editTextEmail.text.toString().trim()

            if (nuevoNombre.isEmpty() || nuevoEmail.isEmpty()) {
                Toast.makeText(requireContext(), "Ambos campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(nuevoEmail).matches()) {
                Toast.makeText(requireContext(), "El formato del email no es válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Sólo actualizar si algún dato ha cambiado
            if (nuevoNombre != userName || nuevoEmail != userEmail) {
                actualizarDatosUsuario(nuevoNombre, nuevoEmail)
            } else {
                Toast.makeText(requireContext(), "No se detectaron cambios en los datos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Asegúrate de que la actividad implemente los listeners
        fragmentChangeListener = context as? OnFragmentChangeListener
    }

    private fun obtenerSesion(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("Sesion", Context.MODE_PRIVATE)
        return sharedPreferences.getString("email", null) // Devuelve el email guardado o null si no está presente
    }

    private fun guardarSesion(context: Context, email: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("Sesion", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.apply()
    }

    private fun obtenerDatosUsuario(email: String, callback: (String?, String?, String?) -> Unit) {
        val database = FirebaseDatabase.getInstance().getReference("users")
        database.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val id = userSnapshot.key // UID del usuario
                        val nombre = userSnapshot.child("name").getValue(String::class.java)
                        val correo = userSnapshot.child("email").getValue(String::class.java)
                        callback(id, nombre, correo)
                        return
                    }
                }
                callback(null, null, null)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error en la base de datos", Toast.LENGTH_SHORT).show()
                callback(null, null, null)
            }
        })
    }

    private fun actualizarDatosUsuario(nuevoNombre: String, nuevoEmail: String) {
        userId?.let { id ->
            val database = FirebaseDatabase.getInstance().getReference("users").child(id)

            // Verificar si el nuevo email ya existe (solo si está cambiando)
            if (nuevoEmail != userEmail) {
                comprobarEmailExistente(nuevoEmail) { existe ->
                    if (existe) {
                        Toast.makeText(requireContext(), "El email ya está en uso", Toast.LENGTH_SHORT).show()
                    } else {
                        // Actualizar los datos
                        val actualizaciones = HashMap<String, Any>()
                        actualizaciones["name"] = nuevoNombre
                        actualizaciones["email"] = nuevoEmail

                        database.updateChildren(actualizaciones)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                                // Actualizar la sesión con el nuevo email
                                guardarSesion(requireContext(), nuevoEmail)

                                // Volver al fragmento de ajustes
                                fragmentChangeListener?.onFragmentChange(Usuarios())
                            }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(), "Error al actualizar los datos", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            } else {
                // Solo actualizar el nombre
                database.child("name").setValue(nuevoNombre)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Nombre actualizado correctamente", Toast.LENGTH_SHORT).show()
                        fragmentChangeListener?.onFragmentChange(Usuarios())
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Error al actualizar el nombre", Toast.LENGTH_SHORT).show()
                    }
            }
        } ?: run {
            Toast.makeText(requireContext(), "No se pudo identificar al usuario", Toast.LENGTH_SHORT).show()
        }
    }

    private fun comprobarEmailExistente(email: String, callback: (Boolean) -> Unit) {
        val database = FirebaseDatabase.getInstance().getReference("users")
        database.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callback(snapshot.exists())
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error al verificar el email", Toast.LENGTH_SHORT).show()
                callback(true) // Por seguridad, asumimos que existe
            }
        })
    }
}