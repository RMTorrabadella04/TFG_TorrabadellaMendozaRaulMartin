package com.example.lostpaws

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner

class Perdidos : Fragment() {

    private lateinit var filtro: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_perdidos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        filtro = view.findViewById(R.id.FiltroPerdidas)
        val mascotas = listOf("Todos", "Gato", "Perro", "Hamster", "Pajaro", "Conejo", "Otro")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mascotas)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filtro.adapter = spinnerAdapter
    }
}