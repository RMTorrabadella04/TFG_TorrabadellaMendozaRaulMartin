package com.example.lostpaws

import Data.Mascota
import android.app.AlertDialog
import android.content.Context
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
import com.google.firebase.database.*

class Mascotas : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MascotasAdapter
    private val mascotaList = mutableListOf<Mascota>()
    private var fragmentChangeListener: OnFragmentChangeListener? = null
    private lateinit var filtro: Spinner
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mascotas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseReference = FirebaseDatabase.getInstance().getReference("mascotas")

        recyclerView = view.findViewById(R.id.recicleviewmascota)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = MascotasAdapter(
            requireContext(),
            mascotaList,
            { mascota -> editarMascota(mascota) },
            { mascota -> eliminarMascota(mascota) }
        )

        recyclerView.adapter = adapter

        cargarMascotas()

        val botonAnyadir = view.findViewById<Button>(R.id.btnAnyadir)
        botonAnyadir.setOnClickListener {
            fragmentChangeListener?.onFragmentChange(AnyadirMascotas())
        }

        val botonBuscar = view.findViewById<Button>(R.id.btnBuscar)
        botonBuscar.setOnClickListener {
            filtrarMascotasPorTipo()
        }

        filtro = view.findViewById(R.id.FiltroMascota)
        val mascotas = listOf("Todos", "Gato", "Perro", "Hamster", "Pajaro", "Conejo", "Otro")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mascotas)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filtro.adapter = spinnerAdapter
    }

    private fun cargarMascotas() {
        val userEmail = getUserEmail()

        databaseReference.orderByChild("userEmail").equalTo(userEmail)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mascotaList.clear()

                    for (mascotaSnapshot in snapshot.children) {
                        val id = mascotaSnapshot.child("id").getValue(String::class.java) ?: ""
                        val nombre = mascotaSnapshot.child("nombre").getValue(String::class.java) ?: ""
                        val tipo = mascotaSnapshot.child("tipo").getValue(String::class.java) ?: ""
                        val raza = mascotaSnapshot.child("raza").getValue(String::class.java) ?: ""
                        val chip = mascotaSnapshot.child("chip").getValue(String::class.java) ?: ""
                        val vacunas = mascotaSnapshot.child("vacunas").getValue(String::class.java) ?: ""
                        val foto = mascotaSnapshot.child("foto").getValue(String::class.java) ?: "pinguinoilerna.png"
                        val email = mascotaSnapshot.child("userEmail").getValue(String::class.java) ?: ""

                        val mascota = Mascota(id, nombre, tipo, raza, chip, vacunas, foto, email)
                        mascotaList.add(mascota)
                    }

                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error al cargar mascotas: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun filtrarMascotasPorTipo() {
        val tipoSeleccionado = filtro.selectedItem.toString()

        if (tipoSeleccionado == "Todos") {
            cargarMascotas()
            return
        }

        val userEmail = getUserEmail()

        databaseReference.orderByChild("userEmail").equalTo(userEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mascotaList.clear()

                    for (mascotaSnapshot in snapshot.children) {
                        val tipo = mascotaSnapshot.child("tipo").getValue(String::class.java) ?: ""

                        if (tipo == tipoSeleccionado) {
                            val id = mascotaSnapshot.child("id").getValue(String::class.java) ?: ""
                            val nombre = mascotaSnapshot.child("nombre").getValue(String::class.java) ?: ""
                            val raza = mascotaSnapshot.child("raza").getValue(String::class.java) ?: ""
                            val chip = mascotaSnapshot.child("chip").getValue(String::class.java) ?: ""
                            val vacunas = mascotaSnapshot.child("vacunas").getValue(String::class.java) ?: ""
                            val foto = mascotaSnapshot.child("foto").getValue(String::class.java) ?: "pinguinoilerna.png"
                            val email = mascotaSnapshot.child("userEmail").getValue(String::class.java) ?: ""

                            val mascota = Mascota(id, nombre, tipo, raza, chip, vacunas, foto, email)
                            mascotaList.add(mascota)
                        }
                    }

                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error al filtrar mascotas: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getUserEmail(): String {
        val sharedPreferences = requireActivity().getSharedPreferences("Sesion", Context.MODE_PRIVATE)
        return sharedPreferences.getString("email", "") ?: ""
    }

    private fun editarMascota(mascota: Mascota) {

        val bundle = Bundle()
        bundle.putString("id", mascota.id)
        bundle.putString("nombre", mascota.nombre)
        bundle.putString("tipo", mascota.tipo)
        bundle.putString("raza", mascota.raza)
        bundle.putString("chip", mascota.chip)
        bundle.putString("vacunas", mascota.vacunas)
        bundle.putString("foto", mascota.foto)

        val editarFragment = EditarMascota()
        editarFragment.arguments = bundle

        fragmentChangeListener?.onFragmentChange(editarFragment)
    }

    private fun eliminarMascota(mascota: Mascota) {
        databaseReference.child(mascota.id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Mascota eliminada correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentChangeListener) {
            fragmentChangeListener = context
        }
    }
}