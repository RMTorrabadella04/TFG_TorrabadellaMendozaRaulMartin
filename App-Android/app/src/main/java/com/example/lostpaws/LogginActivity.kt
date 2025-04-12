package com.example.lostpaws

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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

class LogginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loggin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val editTextEmail = findViewById<EditText>(R.id.email)
        val editTextPassword = findViewById<EditText>(R.id.password)

        val btnLoggin = findViewById<Button>(R.id.loggin)
        btnLoggin.setOnClickListener {

            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (validacionLogin(this, email, password)) {
                verificarUsuario(email, password)
            }
        }
    }

    // Se encarga de verificar que el usuario exista en la base de datos

    private fun verificarUsuario(email: String, password: String) {
        val databaseAdmin = FirebaseDatabase.getInstance().getReference("admin")
        databaseAdmin.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val storedPassword = userSnapshot.child("password").getValue(String::class.java)
                        if (storedPassword == password) {
                            // Usuario autenticado correctamente
                            Toast.makeText(applicationContext, "Bienvenido Administrador", Toast.LENGTH_SHORT).show()

                            // Ir a la pantalla principal
                            val intent = Intent(applicationContext, AdminActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                } else {

                    val database = FirebaseDatabase.getInstance().getReference("users")
                    database.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                for (userSnapshot in snapshot.children) {
                                    val storedPassword = userSnapshot.child("password").getValue(String::class.java)
                                    if (storedPassword == password) {
                                        // Usuario autenticado correctamente
                                        Toast.makeText(applicationContext, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

                                        // Me guardo el correo para saber quien se ha registrado y pillar su info
                                        guardarSesion(email, applicationContext)

                                        // Ir a la pantalla principal
                                        val intent = Intent(applicationContext, MasterActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(applicationContext, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(applicationContext, "No existe una cuenta con este correo", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(applicationContext, "Error en la base de datos", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Error en la base de datos", Toast.LENGTH_SHORT).show()
            }
        })


    }
}

fun guardarSesion(email: String, context: Context) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("Sesion", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("email", email) // Guardamos el email
    editor.apply() // Aplicamos los cambios
}

// Función para validar el email y la contraseña en el login
fun validacionLogin(context: Context, email: String, password: String): Boolean {
    if (password.isEmpty()) {
        Toast.makeText(context, "La contraseña no puede estar vacía.", Toast.LENGTH_SHORT).show()
        return false
    }

    if (!email.contains("@") || !email.contains(".")) {
        Toast.makeText(context, "El correo electrónico no es válido.", Toast.LENGTH_SHORT).show()
        return false
    }

    return true
}