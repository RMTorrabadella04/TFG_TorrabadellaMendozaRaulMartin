package com.example.lostpaws

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class AdminCentrosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_centros)

        val botonCentro = this.findViewById<Button>(R.id.btnCentros)
        val fragment2 = AnyadirCentro()

        if (savedInstanceState == null) {
            val fragmentManager: FragmentManager = supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.fragment_container1, FragmentSuperiorInvitado())
            fragmentTransaction.add(R.id.fragment_container2, fragment2)
            fragmentTransaction.commit()
        }

        val botonAdminUsers: Button = findViewById(R.id.btnAdminUsers)

        botonAdminUsers.setOnClickListener {
            val intent = Intent(this, AdminActivity::class.java)
            startActivity(intent)
        }

        var isFirstClick = true

        botonCentro.setOnClickListener{
            if (isFirstClick) {
                Centros.desdeAdmin = true
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment_container2, Centros())
                fragmentTransaction.commit()
                botonCentro.setText("AÑADIR CENTROS")
                isFirstClick = false
            } else {
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment_container2, AnyadirCentro())
                fragmentTransaction.commit()
                botonCentro.setText("VER CENTROS")
                isFirstClick = true
            }
        }

    }
}
