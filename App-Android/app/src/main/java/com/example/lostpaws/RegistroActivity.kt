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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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


            validacionDatos(this, name, email, password) { isValid ->
                if (isValid) {
                    // Si la validación fue exitosa, guarda la información
                    guardarInfoUsuarios(name, email, password)

                    // Navega a la pantalla de login
                    val intent = Intent(this, LogginActivity::class.java)
                    startActivity(intent)
                }
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

fun validacionDatos(context: Context, name: String, email: String, password: String, callback: (Boolean) -> Unit): Boolean {

    var correcto = true

    // Validar que la contraseña tenga al menos 6 caracteres
    if (password.length < 6) {
        Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show()
        correcto = false
    }

    // Validar que el correo contenga "@" y "."
    if (!email.contains("@") || !email.contains(".")) {
        Toast.makeText(context, "El correo electrónico no es válido.", Toast.LENGTH_SHORT).show()
        correcto = false
    }

    // Verificar si el correo ya existe en la base de datos
    if (correcto) {
        val database = FirebaseDatabase.getInstance().getReference("users")
        database.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // El correo ya existe en la base de datos
                    Toast.makeText(context, "El correo ya está registrado", Toast.LENGTH_SHORT).show()
                    callback(false) // Indica que el correo ya está registrado
                } else {
                    // Si el correo no existe, indica que la validación es exitosa
                    callback(true)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error en la base de datos", Toast.LENGTH_SHORT).show()
                callback(false) // En caso de error en la base de datos
            }
        })
    } else {
        callback(false) // En caso de que alguna validación falle
    }

    return true // La función siempre retorna true ya que la validación asincrónica se maneja en el callback
}

