package com.example.lostpaws

import Data.Mascota
import Data.Perdida
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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

class Perdidos : Fragment() {

    private lateinit var filtro: Spinner
    private var db: DatabaseReference = FirebaseDatabase.getInstance().reference
    private lateinit var recyclerView: RecyclerView
    private val perdidosList = mutableListOf<Perdida>()
    private lateinit var adapter: PerdidosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_perdidos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseDatabase.getInstance().getReference("perdida")

        recyclerView = view.findViewById(R.id.recicleviewperdidos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = PerdidosAdapter(requireContext(), perdidosList) { perdida ->
            contactar(perdida)
        }

        recyclerView.adapter = adapter

        cargarPerdidos()

        filtro = view.findViewById(R.id.FiltroPerdidas)
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
                perdidosList.clear()

                for (perdidaSnapshot in snapshot.children) {
                    val id = perdidaSnapshot.child("mascotaId").getValue(String::class.java) ?: ""
                    val nombre = perdidaSnapshot.child("mascotaNombre").getValue(String::class.java) ?: ""
                    val tipo = perdidaSnapshot.child("mascotaTipo").getValue(String::class.java) ?: ""
                    val raza = perdidaSnapshot.child("mascotaRaza").getValue(String::class.java) ?: ""
                    val chip = perdidaSnapshot.child("mascotaChip").getValue(String::class.java) ?: ""
                    val foto = perdidaSnapshot.child("mascotaFoto").getValue(String::class.java) ?: "pinguinoilerna.png"
                    val fechaPerdida = perdidaSnapshot.child("fechaPerdida").getValue(String::class.java) ?: ""
                    val lugarPerdida = perdidaSnapshot.child("lugarPerdida").getValue(String::class.java) ?: ""
                    val descripcion = perdidaSnapshot.child("descripcion").getValue(String::class.java) ?: ""
                    val telefonoContacto = perdidaSnapshot.child("telefonoContacto").getValue(String::class.java) ?: ""
                    val hayRecompensa = perdidaSnapshot.child("hayRecompensa").getValue(Boolean::class.java) ?: false
                    val recompensa = perdidaSnapshot.child("recompensa").getValue(String::class.java) ?: ""

                    val perdida = Perdida(id, nombre, tipo, raza, chip, foto, fechaPerdida, lugarPerdida,
                        descripcion, telefonoContacto, hayRecompensa, recompensa)
                    perdidosList.add(perdida)
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
                perdidosList.clear()

                for (perdidaSnapshot in snapshot.children) {
                    val tipo = perdidaSnapshot.child("mascotaTipo").getValue(String::class.java) ?: ""

                    if (tipo == tipoSeleccionado) {
                        val id = perdidaSnapshot.child("mascotaId").getValue(String::class.java) ?: ""
                        val nombre = perdidaSnapshot.child("mascotaNombre").getValue(String::class.java) ?: ""
                        val raza = perdidaSnapshot.child("mascotaRaza").getValue(String::class.java) ?: ""
                        val chip = perdidaSnapshot.child("mascotaChip").getValue(String::class.java) ?: ""
                        val foto = perdidaSnapshot.child("mascotaFoto").getValue(String::class.java) ?: "pinguinoilerna.png"
                        val fechaPerdida = perdidaSnapshot.child("fechaPerdida").getValue(String::class.java) ?: ""
                        val lugarPerdida = perdidaSnapshot.child("lugarPerdida").getValue(String::class.java) ?: ""
                        val descripcion = perdidaSnapshot.child("descripcion").getValue(String::class.java) ?: ""
                        val telefonoContacto = perdidaSnapshot.child("telefonoContacto").getValue(String::class.java) ?: ""
                        val hayRecompensa = perdidaSnapshot.child("hayRecompensa").getValue(Boolean::class.java) ?: false
                        val recompensa = perdidaSnapshot.child("recompensa").getValue(String::class.java) ?: ""

                        val perdida = Perdida(id, nombre, tipo, raza, chip, foto, fechaPerdida, lugarPerdida,
                            descripcion, telefonoContacto, hayRecompensa, recompensa)
                        perdidosList.add(perdida)
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error al filtrar mascotas perdidas: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun contactar(perdida: Perdida){
        Toast.makeText(requireContext(), "Contactando al dueño de ${perdida.nombre}", Toast.LENGTH_SHORT).show()
    }
}