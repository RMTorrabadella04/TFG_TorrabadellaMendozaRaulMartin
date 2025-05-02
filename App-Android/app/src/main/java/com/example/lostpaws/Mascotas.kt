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
import android.app.DatePickerDialog
import android.widget.CheckBox
import com.example.lostpaws.Data.Centro
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Mascotas : Fragment() {

    private var db: DatabaseReference = FirebaseDatabase.getInstance().reference
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MascotasAdapter
    private val mascotaList = mutableListOf<Mascota>()
    private var fragmentChangeListener: OnFragmentChangeListener? = null
    private lateinit var filtro: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mascotas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseDatabase.getInstance().getReference("mascotas")

        recyclerView = view.findViewById(R.id.recicleviewmascota)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = MascotasAdapter(
            requireContext(),
            mascotaList,
            { mascota -> editarMascota(mascota) },
            { mascota -> eliminarMascota(mascota) },
            { mascota -> darPerdidaMascota(mascota) },
            { mascota -> abandonarMascota(mascota) }
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

        db.orderByChild("userEmail").equalTo(userEmail)
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

        db.orderByChild("userEmail").equalTo(userEmail)
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
        db.child(mascota.id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Mascota eliminada correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun darPerdidaMascota(mascota: Mascota) {
        val perdidaRef = FirebaseDatabase.getInstance().getReference("perdida")

        // Buscar si ya existe un reporte de pérdida para esta mascota
        perdidaRef.orderByChild("mascotaId").equalTo(mascota.id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Ya está marcada como perdida, así que eliminamos el reporte
                        for (child in snapshot.children) {
                            child.ref.removeValue()
                        }
                        Toast.makeText(requireContext(), "Reporte de pérdida eliminado", Toast.LENGTH_SHORT).show()
                    } else {
                        mostrarDialogoReportePerdida(mascota)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error al consultar el estado de pérdida", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun mostrarDialogoReportePerdida(mascota: Mascota) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_reporte_perdida, null)

        // Referencias a los elementos del dialog
        val editTextFecha = dialogView.findViewById<TextInputEditText>(R.id.editTextFechaPerdida)
        val editTextLugar = dialogView.findViewById<TextInputEditText>(R.id.editTextLugarPerdida)
        val editTextDescripcion = dialogView.findViewById<TextInputEditText>(R.id.editTextDescripcion)
        val editTextTelefono = dialogView.findViewById<TextInputEditText>(R.id.editTextTelefono)
        val checkBoxRecompensa = dialogView.findViewById<CheckBox>(R.id.checkBoxRecompensa)
        val layoutRecompensa = dialogView.findViewById<TextInputLayout>(R.id.layoutRecompensa)
        val editTextRecompensa = dialogView.findViewById<TextInputEditText>(R.id.editTextRecompensa)
        val btnCancelar = dialogView.findViewById<Button>(R.id.btnCancelar)
        val btnReportar = dialogView.findViewById<Button>(R.id.btnReportar)

        // Configurar fecha con DatePicker
        val calendar = Calendar.getInstance()

        editTextFecha.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    editTextFecha.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        // Configurar checkbox de recompensa
        checkBoxRecompensa.setOnCheckedChangeListener { _, isChecked ->
            layoutRecompensa.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        // Crear el diálogo
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Configurar botón Cancelar
        btnCancelar.setOnClickListener {
            alertDialog.dismiss()
        }

        // Configurar botón Reportar
        btnReportar.setOnClickListener {
            // Validar campos
            if (editTextFecha.text.isNullOrEmpty()) {
                editTextFecha.error = "La fecha es obligatoria"
                return@setOnClickListener
            }

            if (editTextLugar.text.isNullOrEmpty()) {
                editTextLugar.error = "El lugar es obligatorio"
                return@setOnClickListener
            }

            if (editTextTelefono.text.isNullOrEmpty()) {
                editTextTelefono.error = "El teléfono de contacto es obligatorio"
                return@setOnClickListener
            }

            if (checkBoxRecompensa.isChecked && editTextRecompensa.text.isNullOrEmpty()) {
                editTextRecompensa.error = "Ingrese la cantidad de recompensa"
                return@setOnClickListener
            }

            // Recopilar datos del formulario
            val fecha = editTextFecha.text.toString()
            val lugar = editTextLugar.text.toString()
            val descripcion = editTextDescripcion.text.toString()
            val telefono = editTextTelefono.text.toString()
            val hayRecompensa = checkBoxRecompensa.isChecked
            val recompensa = if (hayRecompensa) editTextRecompensa.text.toString() else "0"

            // Guardar reporte en Firebase
            guardarReportePerdida(mascota, fecha, lugar, descripcion, telefono, hayRecompensa, recompensa)

            // Cerrar el diálogo
            alertDialog.dismiss()
        }

        // Mostrar el diálogo
        alertDialog.show()
    }

    private fun guardarReportePerdida(
        mascota: Mascota,
        fecha: String,
        lugar: String,
        descripcion: String,
        telefono: String,
        hayRecompensa: Boolean,
        recompensa: String
    ) {
        // Crear un ID único para el reporte

        val perdidaRef = FirebaseDatabase.getInstance().getReference().child("perdida")
        val perdidaId = perdidaRef.push().key

        if (perdidaId == null) {
            Toast.makeText(requireContext(), "Error al generar ID del reporte", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear mapa con los datos del reporte
        val reporteMap = hashMapOf(
            "mascotaId" to mascota.id,
            "mascotaNombre" to mascota.nombre,
            "mascotaTipo" to mascota.tipo,
            "mascotaRaza" to mascota.raza,
            "mascotaChip" to mascota.chip,
            "mascotaFoto" to mascota.foto,
            "fechaReporte" to getCurrentDateTime(),
            "fechaPerdida" to fecha,
            "lugarPerdida" to lugar,
            "descripcion" to descripcion,
            "telefonoContacto" to telefono,
            "hayRecompensa" to hayRecompensa,
            "recompensa" to recompensa,
            "userEmail" to getUserEmail()
        )


        // Guardar reporte en Firebase
        perdidaRef.child(perdidaId).setValue(reporteMap)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Mascota reportada como perdida", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al reportar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun abandonarMascota(mascota: Mascota) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_reporte_abandono, null)

        val editTextFecha = dialogView.findViewById<TextInputEditText>(R.id.editTextFechaPerdida)
        val spinnerRefugios = dialogView.findViewById<Spinner>(R.id.spinerRefugios)
        val btnCancelar = dialogView.findViewById<Button>(R.id.btnCancelar)
        val btnReportar = dialogView.findViewById<Button>(R.id.btnReportar)

        val calendar = Calendar.getInstance()
        editTextFecha.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    editTextFecha.setText(formato.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        val refugiosList = mutableListOf<Pair<String, Centro>>()  // Pair<id, objeto>
        val nombresRefugios = mutableListOf<String>()

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nombresRefugios)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRefugios.adapter = adapter

        FirebaseDatabase.getInstance().getReference("centros")
            .orderByChild("tipo").equalTo("refugio")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    refugiosList.clear()
                    nombresRefugios.clear()

                    for (snap in snapshot.children) {
                        val id = snap.key ?: continue
                        val centro = snap.getValue(Centro::class.java) ?: continue
                        refugiosList.add(Pair(id, centro))
                        nombresRefugios.add(centro.nombre)
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error al cargar refugios", Toast.LENGTH_SHORT).show()
                }
            })

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnCancelar.setOnClickListener {
            alertDialog.dismiss()
        }

        btnReportar.setOnClickListener {
            if (editTextFecha.text.isNullOrEmpty()) {
                editTextFecha.error = "La fecha es obligatoria"
                return@setOnClickListener
            }

            if (refugiosList.isEmpty() || spinnerRefugios.selectedItemPosition == -1) {
                Toast.makeText(requireContext(), "Seleccione un refugio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fecha = editTextFecha.text.toString()
            val (refugioId, _) = refugiosList[spinnerRefugios.selectedItemPosition]

            guardarReporteAbandono(mascota, fecha, refugioId)

            alertDialog.dismiss()
        }

        alertDialog.show()
    }


    private fun guardarReporteAbandono(
        mascota: Mascota,
        fecha: String,
        refugioId: String
    ) {
        val abandonoRef = FirebaseDatabase.getInstance().getReference("abandonos")
        val abandonoId = abandonoRef.push().key

        if (abandonoId == null) {
            Toast.makeText(requireContext(), "Error al generar ID del reporte", Toast.LENGTH_SHORT).show()
            return
        }

        val reporteMap = hashMapOf(
            "mascotaId" to mascota.id,
            "mascotaNombre" to mascota.nombre,
            "mascotaTipo" to mascota.tipo,
            "mascotaRaza" to mascota.raza,
            "mascotaChip" to mascota.chip,
            "mascotaFoto" to mascota.foto,
            "fechaReporte" to getCurrentDateTime(),
            "fechaAbandono" to fecha,
            "refugioId" to refugioId,
            "userEmail" to getUserEmail()
        )

        abandonoRef.child(abandonoId).setValue(reporteMap)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Mascota reportada como abandonada", Toast.LENGTH_SHORT).show()
                eliminarMascota(mascota)
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al reportar abandono: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }



    // Función auxiliar para obtener la fecha y hora actual formateada
    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentChangeListener) {
            fragmentChangeListener = context
        }
    }
}