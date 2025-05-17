package com.example.lostpaws

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        
        // Botones

        val btnIniciarSesion = findViewById<Button>(R.id.iniciarSesion)
        btnIniciarSesion.setOnClickListener {
            // Crea un Intent para iniciar SecondActivity
            val intent = Intent(this, LogginActivity::class.java)
            startActivity(intent)
        }

        val btnRegistro = findViewById<Button>(R.id.registro)
        btnRegistro.setOnClickListener {
            // Crea un Intent para iniciar SecondActivity
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }

        val btnInvitado = findViewById<Button>(R.id.invitado)
        btnInvitado.setOnClickListener {
            guardarSesion("invitado", applicationContext)
            val intent = Intent(this, InvitadoActivity::class.java)
            startActivity(intent)
        }
    }

    fun guardarSesion(email: String, context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("Sesion", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("email", email) // Guardamos el email
        editor.apply() // Aplicamos los cambios
    }

}