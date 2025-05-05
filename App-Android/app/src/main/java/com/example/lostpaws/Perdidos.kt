package com.example.lostpaws

import Data.Mascota
import Data.Perdida
import android.content.Context
import android.content.SharedPreferences
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
    private var userId: String? = null
    private var userName: String? = null
    private var userEmail: String? = null
    private var duenyoId: String? = null
    private var duenyoName: String? = null
    private var duenyoEmail: String? = null

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

    private fun obtenerSesion(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("Sesion", Context.MODE_PRIVATE)
        return sharedPreferences.getString("email", null) // Devuelve el email guardado o null si no está presente
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
                    val duenyo = perdidaSnapshot.child("userEmail").getValue(String::class.java) ?: ""

                    val perdida = Perdida(id, nombre, tipo, raza, chip, foto, fechaPerdida, lugarPerdida,
                        descripcion, telefonoContacto, hayRecompensa, duenyo, recompensa)
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
                        val duenyo = perdidaSnapshot.child("userEmail").getValue(String::class.java) ?: ""

                        val perdida = Perdida(id, nombre, tipo, raza, chip, foto, fechaPerdida, lugarPerdida,
                            descripcion, telefonoContacto, hayRecompensa, duenyo, recompensa)
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

    private fun contactar(perdida: Perdida) {
        val userEmail = obtenerSesion(requireContext())
        val duenyoEmail = perdida.duenyo

        // Primero obtenemos información del usuario actual
        val userDatabase = FirebaseDatabase.getInstance().getReference("users")
        userDatabase.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(userSnapshot: DataSnapshot) {
                if (userSnapshot.exists()) {
                    var userId: String? = null
                    var userName: String? = null

                    for (currentUserSnapshot in userSnapshot.children) {
                        userId = currentUserSnapshot.key
                        userName = currentUserSnapshot.child("name").getValue(String::class.java)
                    }

                    if (userId != null && userName != null) {
                        // Ahora obtenemos información del dueño
                        val ownerDatabase = FirebaseDatabase.getInstance().getReference("users")
                        ownerDatabase.orderByChild("email").equalTo(duenyoEmail).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(ownerSnapshot: DataSnapshot) {
                                if (ownerSnapshot.exists()) {
                                    var duenyoId: String? = null
                                    var duenyoName: String? = null

                                    for (currentOwnerSnapshot in ownerSnapshot.children) {
                                        duenyoId = currentOwnerSnapshot.key
                                        duenyoName = currentOwnerSnapshot.child("name").getValue(String::class.java)
                                    }

                                    if (duenyoId != null && duenyoName != null) {
                                        // Ahora que tenemos ambos conjuntos de datos, creamos la relación
                                        crearRelacionChat(userId, userName, userEmail, duenyoId, duenyoName, duenyoEmail)
                                    } else {
                                        Toast.makeText(requireContext(), "No se pudo obtener la información del dueño", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(requireContext(), "No se encontró el dueño en la base de datos", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(requireContext(), "Error al buscar dueño: ${error.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    } else {
                        Toast.makeText(requireContext(), "No se pudo obtener tu información de usuario", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "No se encontró tu usuario en la base de datos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error en la base de datos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun crearRelacionChat(userId: String, userName: String, userEmail: String?,
                                  duenyoId: String, duenyoName: String, duenyoEmail: String?) {
        val relacionDuenyoUsuario = FirebaseDatabase.getInstance().getReference().child("relacionChats")
        val relacionDuenyoUsuarioId = relacionDuenyoUsuario.push().key

        if (relacionDuenyoUsuarioId == null) {
            Toast.makeText(requireContext(), "Error al generar ID de la relación", Toast.LENGTH_SHORT).show()
            return
        }

        val reporteMap = hashMapOf(
            "usuarioId" to userId,
            "usuarioName" to userName,
            "usuarioEmail" to userEmail,
            "usuarioTipo" to "Usuario",
            "duenyoId" to duenyoId,
            "duenyoName" to duenyoName,
            "duenyoEmail" to duenyoEmail,
            "duenyoTipo" to "Usuario"
        )

        relacionDuenyoUsuario.child(relacionDuenyoUsuarioId).setValue(reporteMap)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Contacto establecido con éxito", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al establecer contacto: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}