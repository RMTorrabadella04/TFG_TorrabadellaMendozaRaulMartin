package com.example.lostpaws

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.core.content.ContextCompat

class CambioPestanyasInvitado : Fragment() {

    private var fragmentChangeListener: OnFragmentChangeListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cambio_pestanyas_invitado, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val botonPerdido = view.findViewById<ImageButton>(R.id.btnPerdidos)
        val botonLogin = view.findViewById<Button>(R.id.btnLogin)
        val botonAbandonado = view.findViewById<ImageButton>(R.id.btnAbandonados)


        val defaultColor = ContextCompat.getColor(requireContext(), R.color.verde_fondo2)
        val selectedColor = ContextCompat.getColor(requireContext(), R.color.verde_boton2)

        fun resetButtonColors() {
            botonPerdido.setBackgroundColor(defaultColor)
            botonLogin.setBackgroundColor(defaultColor)
            botonAbandonado.setBackgroundColor(defaultColor)
        }

        resetButtonColors()
        botonPerdido.setBackgroundColor(selectedColor)

        botonPerdido.setOnClickListener {
            resetButtonColors()
            botonPerdido.setBackgroundColor(selectedColor)

            fragmentChangeListener?.onFragmentChange(Perdidos())
        }

        botonLogin.setOnClickListener {
            resetButtonColors()
            botonLogin.setBackgroundColor(selectedColor)

            val intent = Intent(requireContext(), LogginActivity::class.java)
            startActivity(intent)
        }


        botonAbandonado.setOnClickListener {
            resetButtonColors()
            botonAbandonado.setBackgroundColor(selectedColor)

            fragmentChangeListener?.onFragmentChange(Abandonados())
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Asegúrate de que el Activity implementa el OnFragmentChangeListener
        fragmentChangeListener = context as? OnFragmentChangeListener
    }
}