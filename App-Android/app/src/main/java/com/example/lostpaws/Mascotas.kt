package com.example.lostpaws

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner

class Mascotas : Fragment() {

    private var fragmentChangeListener: OnFragmentChangeListener? = null
    private lateinit var filtro: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mascotas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var botonAnyadir = view.findViewById<Button>(R.id.btnAnyadir)

        botonAnyadir.setOnClickListener{

            fragmentChangeListener?.onFragmentChange(AnyadirMascotas())

        }

        var botonBuscar = view.findViewById<Button>(R.id.btnBuscar)

        filtro = view.findViewById(R.id.FiltroMascota)

        val mascotas = listOf("Gato", "Perro", "Hamster", "Pajaro", "Conejo", "Otro")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mascotas)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        filtro.adapter = adapter

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentChangeListener) {
            fragmentChangeListener = context
        }
    }


}