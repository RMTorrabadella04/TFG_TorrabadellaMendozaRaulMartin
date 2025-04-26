package com.example.lostpaws

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
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

    companion object {
        var desdeAdmin: Boolean = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_centros, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (desdeAdmin) {
            view.findViewById<View>(R.id.main).setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sinFondo))
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

                if (searchType == "Refugios") {
                    centrosRList.clear()
                    adapter = RefugioAdapter(requireContext(), centrosRList, { refugio ->
                        if (desdeAdmin) eliminarRefugio(refugio)
                    }, showDeleteButton = desdeAdmin)
                } else {
                    centrosVList.clear()
                    adapter = VeterinarioAdapter(requireContext(), centrosVList, { veterinario ->
                        if (desdeAdmin) eliminarVeterinario(veterinario)
                    }, showDeleteButton = desdeAdmin)
                }

                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        botonBusqueda.setOnClickListener {
            val tipoBusqueda = spinner.selectedItem.toString()
            centrosRList.clear()
            centrosVList.clear()

            if (tipoBusqueda == "Refugios") {
                db.child("centros").orderByChild("tipo").equalTo("refugio")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (refSnapshot in snapshot.children) {
                                val refugio = refSnapshot.getValue(Refugio::class.java)
                                if (refugio != null) {
                                    centrosRList.add(refugio)
                                }
                            }
                            adapter.notifyDataSetChanged()
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
            } else {
                db.child("centros").orderByChild("tipo").equalTo("veterinario")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (vetSnapshot in snapshot.children) {
                                val veterinario = vetSnapshot.getValue(Veterinario::class.java)
                                if (veterinario != null) {
                                    centrosVList.add(veterinario)
                                }
                            }
                            adapter.notifyDataSetChanged()
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
            }
        }

        return view
    }

    private fun eliminarRefugio(refugio: Refugio) {
        db.child("centros").orderByChild("nombre").equalTo(refugio.nombre)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (item in snapshot.children) {
                        item.ref.removeValue()
                    }
                    centrosRList.remove(refugio)
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun eliminarVeterinario(veterinario: Veterinario) {
        db.child("centros").orderByChild("nombre").equalTo(veterinario.nombre)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (item in snapshot.children) {
                        item.ref.removeValue()
                    }
                    centrosVList.remove(veterinario)
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}
