package com.example.lostpaws

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lostpaws.Data.Refugio
import com.example.lostpaws.Data.Veterinario
import com.google.firebase.database.*

class Centros : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerView.Adapter<*>
    private val centrosRList = mutableListOf<Refugio>()
    private val centrosVList = mutableListOf<Veterinario>()
    private val db: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_centros, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val opcionesBusqueda = listOf("Refugios", "Veterinarios")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opcionesBusqueda)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinner: Spinner = view.findViewById(R.id.queTipodeBusqueda)
        spinner.adapter = spinnerAdapter

        val botonBusqueda: Button = view.findViewById(R.id.btnBuscar)

        recyclerView = view.findViewById(R.id.centroList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedView: View?, position: Int, id: Long) {
                val searchType = parentView.getItemAtPosition(position).toString()

                when (searchType) {
                    "Refugios" -> {
                        centrosRList.clear()
                        adapter = RefugioAdapter(requireContext(), centrosRList) { refugio ->

                        }
                    }
                    "Veterinarios" -> {
                        centrosVList.clear()
                        adapter = VeterinarioAdapter(requireContext(), centrosVList) { veterinario ->
                        }
                    }
                }

                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                centrosRList.clear()
                centrosVList.clear()
                adapter.notifyDataSetChanged()
            }
        }

        botonBusqueda.setOnClickListener {
            // Obtener el tipo de búsqueda (refugios o veterinarios) desde el spinner
            val tipoBusqueda = spinner.selectedItem.toString()

            // Limpiar las listas antes de hacer una nueva consulta
            centrosRList.clear()
            centrosVList.clear()

            // Realizar la consulta correspondiente
            if (tipoBusqueda == "Refugios") {
                db.child("centros").orderByChild("tipo").equalTo("refugio")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            // Recorrer los resultados de la consulta
                            for (refSnapshot in snapshot.children) {
                                val refugio = refSnapshot.getValue(Refugio::class.java)
                                if (refugio != null) {
                                    centrosRList.add(refugio)
                                }
                            }
                            adapter.notifyDataSetChanged()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Manejar error si lo deseas
                        }
                    })
            } else if (tipoBusqueda == "Veterinarios") {
                db.child("centros").orderByChild("tipo").equalTo("veterinario")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            // Recorrer los resultados de la consulta
                            for (vetSnapshot in snapshot.children) {
                                val veterinario = vetSnapshot.getValue(Veterinario::class.java)
                                if (veterinario != null) {
                                    centrosVList.add(veterinario)
                                }
                            }
                            adapter.notifyDataSetChanged()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Manejar error si lo deseas
                        }
                    })
            }
        }

        return view
    }
}