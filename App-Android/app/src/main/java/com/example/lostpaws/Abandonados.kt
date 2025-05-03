package com.example.lostpaws

import Data.Abandono
import Data.Perdida
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Abandonados : Fragment() {

    private lateinit var filtro: Spinner
    private var db: DatabaseReference = FirebaseDatabase.getInstance().reference
    private lateinit var recyclerView: RecyclerView
    private val abandonadosList = mutableListOf<Abandono>()
    private lateinit var adapter: AbandonadosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_abandonados, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseDatabase.getInstance().getReference("abandonos")

        recyclerView = view.findViewById(R.id.recicleviewabandonados)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = AbandonadosAdapter(requireContext(), abandonadosList) { abandono ->
            contactar(abandono)
        }

        recyclerView.adapter = adapter

        cargarPerdidos()

        filtro = view.findViewById(R.id.FiltroAbandonados)
        val mascotas = listOf("Todos", "Gato", "Perro", "Hamster", "Pajaro", "Conejo", "Otro")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mascotas)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filtro.adapter = spinnerAdapter

        val botonBuscar = view.findViewById<Button>(R.id.btnBuscar)
        botonBuscar.setOnClickListener {
            filtrarPerdidosPorTipo()
        }
    }

    private fun cargarPerdidos() {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                abandonadosList.clear()

                for (perdidaSnapshot in snapshot.children) {
                    val id = perdidaSnapshot.child("mascotaId").getValue(String::class.java) ?: ""
                    val nombre = perdidaSnapshot.child("mascotaNombre").getValue(String::class.java) ?: ""
                    val tipo = perdidaSnapshot.child("mascotaTipo").getValue(String::class.java) ?: ""
                    val raza = perdidaSnapshot.child("mascotaRaza").getValue(String::class.java) ?: ""
                    val chip = perdidaSnapshot.child("mascotaChip").getValue(String::class.java) ?: ""
                    val foto = perdidaSnapshot.child("mascotaFoto").getValue(String::class.java) ?: "pinguinoilerna.png"
                    val fechaReporte = perdidaSnapshot.child("fechaReporte").getValue(String::class.java) ?: ""
                    val fechaAbandono = perdidaSnapshot.child("fechaAbandono").getValue(String::class.java) ?: ""
                    val refugioId = perdidaSnapshot.child("refugioId").getValue(String::class.java) ?: ""
                    val userEmail = perdidaSnapshot.child("userEmail").getValue(String::class.java) ?: ""

                    val abandono = Abandono(id, nombre, tipo, raza, chip, foto, fechaReporte, fechaAbandono,
                        refugioId, userEmail)
                    abandonadosList.add(abandono)
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error al cargar mascotas perdidas: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filtrarPerdidosPorTipo() {
        val tipoSeleccionado = filtro.selectedItem.toString()

        if (tipoSeleccionado == "Todos") {
            cargarPerdidos()
            return
        }

        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                abandonadosList.clear()

                for (perdidaSnapshot in snapshot.children) {
                    val tipo = perdidaSnapshot.child("mascotaTipo").getValue(String::class.java) ?: ""

                    if (tipo == tipoSeleccionado) {
                        val id = perdidaSnapshot.child("mascotaId").getValue(String::class.java) ?: ""
                        val nombre = perdidaSnapshot.child("mascotaNombre").getValue(String::class.java) ?: ""
                        val raza = perdidaSnapshot.child("mascotaRaza").getValue(String::class.java) ?: ""
                        val chip = perdidaSnapshot.child("mascotaChip").getValue(String::class.java) ?: ""
                        val foto = perdidaSnapshot.child("mascotaFoto").getValue(String::class.java) ?: "pinguinoilerna.png"
                        val fechaReporte = perdidaSnapshot.child("fechaReporte").getValue(String::class.java) ?: ""
                        val fechaAbandono = perdidaSnapshot.child("fechaAbandono").getValue(String::class.java) ?: ""
                        val refugioId = perdidaSnapshot.child("refugioId").getValue(String::class.java) ?: ""
                        val userEmail = perdidaSnapshot.child("userEmail").getValue(String::class.java) ?: ""

                        val abandono = Abandono(id, nombre, tipo, raza, chip, foto, fechaReporte, fechaAbandono,
                            refugioId, userEmail)
                        abandonadosList.add(abandono)
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error al filtrar mascotas abandonadas: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun contactar(abandono: Abandono){
        Toast.makeText(requireContext(), "Contactando al refugio de ${abandono.nombre}", Toast.LENGTH_SHORT).show()
    }
}
