package com.example.lostpaws

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class UsuarioOpcionesActivity : AppCompatActivity(), OnFragmentChangeListener,OnFragmentSuperiorChangeListener   {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_usuarioopciones)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fragment1 = FragmentSuperiorUsuario()

        if (savedInstanceState == null) {
            val fragmentManager: FragmentManager = supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.fragment_container1, fragment1)
            fragmentTransaction.add(R.id.fragment_container2, Usuario())
            fragmentTransaction.commit()
        }
    }

    override fun onFragmentChange(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        // Reemplazar el fragmento actual con el nuevo fragmento
        fragmentTransaction.replace(R.id.fragment_container2, fragment)
        fragmentTransaction.addToBackStack(null) // Agregar a la pila de retroceso si es necesario

        fragmentTransaction.commit()
    }

    override fun onFragmentSuperiorChange(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        // Reemplazar el fragmento actual con el nuevo fragmento
        fragmentTransaction.replace(R.id.fragment_container1, fragment)
        fragmentTransaction.addToBackStack(null) // Agregar a la pila de retroceso si es necesario

        fragmentTransaction.commit()
    }
}