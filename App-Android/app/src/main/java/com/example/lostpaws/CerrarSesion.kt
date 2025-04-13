package com.example.lostpaws

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button


class CerrarSesion : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cerrar_sesion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val botonCerrarSesion = view.findViewById<Button>(R.id.btnCerrarSesion)

        botonCerrarSesion.setOnClickListener{
            cerrarsesion()
        }
    }

    private fun cerrarsesion(){

        val prefs = requireContext().getSharedPreferences("Sesion", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)

    }
}