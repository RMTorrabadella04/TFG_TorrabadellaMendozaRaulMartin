package com.example.lostpaws

import Data.Usuario
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase

class RegistroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnRegistro = findViewById<Button>(R.id.registro)
        btnRegistro.setOnClickListener {

            val editTextName = findViewById<EditText>(R.id.nombre)
            val editTextEmail = findViewById<EditText>(R.id.email)
            val editTextPassword = findViewById<EditText>(R.id.password)

            val name = editTextName.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (validacionDatos(this, name, email, password)){
                guardarInfoUsuarios(name, email, password)

                val intent = Intent(this, LogginActivity::class.java)
                startActivity(intent)
            }


        }

        
    }
}

// Guarda la info del usuario en la base de datos

fun guardarInfoUsuarios(name: String, email: String, password: String){
    // Crear un objeto User
    val user = Usuario(name, email, password)

    // Obtener la referencia de Firebase Realtime Database
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("users") // Aquí "users" es el nodo donde se guardan los datos

    // Generar un ID único para el usuario (por ejemplo, utilizando push() para crear una nueva entrada)
    val userId = myRef.push().key // Esto genera una clave única para cada usuario

    // Guardar los datos
    if (userId != null) {
        myRef.child(userId).setValue(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Los datos se guardaron correctamente
                    println("Datos guardados correctamente")
                } else {
                    // Hubo un error
                    println("Error al guardar los datos: ${task.exception?.message}")

                }
            }
    }

}

// Se encarga de la validacion de los datos

fun validacionDatos(context: Context, name: String, email: String, password: String): Boolean {
    // Validar que la contraseña tenga al menos 6 caracteres
    if (password.length < 6) {
        Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show()
        return false
    }

    // Validar que el correo contenga "@" y "."
    if (!email.contains("@") || !email.contains(".")) {
        Toast.makeText(context, "El correo electrónico no es válido.", Toast.LENGTH_SHORT).show()

        return false
    }

    return true
}
