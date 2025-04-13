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
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CambiarContrasenya : Fragment() {

    private var fragmentChangeListener: OnFragmentChangeListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cambiar_contrasenya, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val botonCambiarContrasenya = view.findViewById<Button>(R.id.btnCambiarContrasenya)

        botonCambiarContrasenya.setOnClickListener{

            val editTextActual = requireView().findViewById<EditText>(R.id.ContrasenyaActual)
            val editTextNueva = requireView().findViewById<EditText>(R.id.ContrasenyaNueva)

            val actual = editTextActual.text.toString()
            val nueva = editTextNueva.text.toString()

            if(nueva.length>=6) {
                cambiarcontrasenya(actual, nueva)
            }else{
                Toast.makeText(context, "La contraseña Nueva debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Asegúrate de que la actividad implemente los listeners
        fragmentChangeListener = context as? OnFragmentChangeListener
    }

    private fun cambiarcontrasenya(actual: String, nueva: String) {
        val email = obtenerSesion(requireContext())

        if (email != null) {
            obtenerDatosUsuario(email) { contrasenya ->
                if (contrasenya != null) {
                    if (contrasenya == actual) {
                        val database = FirebaseDatabase.getInstance().getReference("users")
                        database.orderByChild("email").equalTo(email)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        for (userSnapshot in snapshot.children) {
                                            // Actualizamos la contraseña
                                            userSnapshot.ref.child("password").setValue(nueva)
                                            Toast.makeText(
                                                requireContext(),
                                                "Contraseña actualizada correctamente",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            fragmentChangeListener?.onFragmentChange(Ajustes())
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Error al actualizar la contraseña",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "La contraseña actual es incorrecta.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "No se pudo obtener la contraseña.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "No hay sesión iniciada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun obtenerDatosUsuario(email: String, callback: (String?) -> Unit) {
        val database = FirebaseDatabase.getInstance().getReference("users")
        database.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
            ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val contrasenya = userSnapshot.child("password").getValue(String::class.java)
                        callback(contrasenya)
                    }
                } else {
                    callback(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }



    private fun obtenerSesion(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("Sesion", Context.MODE_PRIVATE)
        return sharedPreferences.getString("email", null) // Devuelve el email guardado o null si no está presente
    }


}