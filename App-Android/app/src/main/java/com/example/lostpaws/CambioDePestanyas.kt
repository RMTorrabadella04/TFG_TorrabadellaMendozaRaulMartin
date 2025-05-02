package com.example.lostpaws

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat

class CambioDePestanyas : Fragment() {

    private var fragmentChangeListener: OnFragmentChangeListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cambio_de_pestanyas, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Referencias de los botones

        val botonCentro = view.findViewById<ImageButton>(R.id.btnCentro)
        val botonPerdido = view.findViewById<ImageButton>(R.id.btnPerdidos)
        val botonMascota = view.findViewById<ImageButton>(R.id.btnMascotas)
        val botonAbandonado = view.findViewById<ImageButton>(R.id.btnAbandonados)
        val botonChat = view.findViewById<ImageButton>(R.id.btnChats)



        // Colores

        val defaultColor = ContextCompat.getColor(requireContext(), R.color.verde_fondo2)
        val selectedColor = ContextCompat.getColor(requireContext(), R.color.verde_boton)

        // Función para resetear colores

        fun resetButtonColors() {
            botonCentro.setBackgroundColor(defaultColor)
            botonPerdido.setBackgroundColor(defaultColor)
            botonMascota.setBackgroundColor(defaultColor)
            botonAbandonado.setBackgroundColor(defaultColor)
            botonChat.setBackgroundColor(defaultColor)
        }

        // Configura el botón predeterminado en su color correspondiente

        resetButtonColors()
        botonPerdido.setBackgroundColor(selectedColor)

        // Listener de Centro cambia tanto el color como al fragment del chat

        botonCentro.setOnClickListener {
            resetButtonColors()
            botonCentro.setBackgroundColor(selectedColor)

            fragmentChangeListener?.onFragmentChange(Centros())
        }

        // Listener de Perdido cambia tanto el color como al fragment del chat

        botonPerdido.setOnClickListener {
            resetButtonColors()
            botonPerdido.setBackgroundColor(selectedColor)

            fragmentChangeListener?.onFragmentChange(Perdidos())
        }

        // Listener de Mascota cambia tanto el color como al fragment del chat

        botonMascota.setOnClickListener {
            resetButtonColors()
            botonMascota.setBackgroundColor(selectedColor)

            fragmentChangeListener?.onFragmentChange(Mascotas())
        }

        // Listener de Abandonado cambia tanto el color como al fragment del chat

        botonAbandonado.setOnClickListener {
            resetButtonColors()
            botonAbandonado.setBackgroundColor(selectedColor)

            fragmentChangeListener?.onFragmentChange(Abandonados())
        }

        // Listener de Chat cambia tanto el color como al fragment del chat

        botonChat.setOnClickListener {
            resetButtonColors()
            botonChat.setBackgroundColor(selectedColor)

            fragmentChangeListener?.onFragmentChange(Chats())
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Asegúrate de que el Activity implementa el OnFragmentChangeListener
        fragmentChangeListener = context as? OnFragmentChangeListener
    }
}