package com.example.lostpaws

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class BorrarCuenta : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_borrar_cuenta, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val botonBorrarCuenta = view.findViewById<Button>(R.id.btnBorrarCuenta)

        botonBorrarCuenta.setOnClickListener{
            borrarCuenta()
        }

    }

    private fun obtenerSesion(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("Sesion", Context.MODE_PRIVATE)
        return sharedPreferences.getString("email", null)
    }

    private fun borrarCuenta() {
        val email = obtenerSesion(requireContext())

        if (email != null) {
            val database = FirebaseDatabase.getInstance().getReference("users")

            database.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (userSnapshot in snapshot.children) {
                                userSnapshot.ref.removeValue()
                            }
                            Toast.makeText(requireContext(), "Cuenta eliminada correctamente.", Toast.LENGTH_SHORT).show()

                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(requireContext(), "No se encontró la cuenta.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(requireContext(), "Error al eliminar la cuenta.", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(requireContext(), "No hay sesión iniciada.", Toast.LENGTH_SHORT).show()
        }
    }

}