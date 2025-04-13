package com.example.lostpaws

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton


class Ajustes : Fragment() {

    private var fragmentChangeListener: OnFragmentChangeListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ajustes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Referencias de los botones

        val botonPrivacidad = view.findViewById<Button>(R.id.btnPrivacidad)
        val botonCerrarSesion = view.findViewById<Button>(R.id.btnCerrarSesion)
        val botonCambiarContrasenya = view.findViewById<Button>(R.id.btnCambiarContrasenya)
        val botonBorrarCuenta = view.findViewById<Button>(R.id.btnBorrarCuenta)
        val botonDonaciones = view.findViewById<Button>(R.id.btnHacerDonacion)

        // Referente al cambio de pestañas de cada pestaña

        botonPrivacidad.setOnClickListener{
            fragmentChangeListener?.onFragmentChange(Privacidad())
        }

        botonCerrarSesion.setOnClickListener{
            fragmentChangeListener?.onFragmentChange(CerrarSesion())
        }

        botonCambiarContrasenya.setOnClickListener{
            fragmentChangeListener?.onFragmentChange(CambiarContrasenya())
        }

        botonBorrarCuenta.setOnClickListener{
            fragmentChangeListener?.onFragmentChange(BorrarCuenta())
        }

        botonDonaciones.setOnClickListener{
            fragmentChangeListener?.onFragmentChange(Donaciones())
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Asegúrate de que la actividad implemente los listeners
        fragmentChangeListener = context as? OnFragmentChangeListener
    }

}