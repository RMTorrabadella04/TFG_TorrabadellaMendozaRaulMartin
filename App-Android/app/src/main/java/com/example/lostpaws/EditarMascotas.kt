package com.example.lostpaws

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference

class EditarMascota : Fragment() {

    private var fragmentChangeListener: OnFragmentChangeListener? = null
    private lateinit var tipoderaza: Spinner
    private lateinit var editTextNombre: EditText
    private lateinit var editTextRaza: EditText
    private lateinit var editTextChip: EditText
    private lateinit var editTextVacunas: EditText
    private lateinit var databaseReference: DatabaseReference
    private var fotoMascota: String = "pinguinoilerna.png"

    // Variables para los datos de la mascota
    private var mascotaId: String = ""
    private var mascotaNombre: String = ""
    private var mascotaTipo: String = ""
    private var mascotaRaza: String = ""
    private var mascotaChip: String = ""
    private var mascotaVacunas: String = ""
    private var mascotaFoto: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            mascotaId = it.getString("id", "")
            mascotaNombre = it.getString("nombre", "")
            mascotaTipo = it.getString("tipo", "")
            mascotaRaza = it.getString("raza", "")
            mascotaChip = it.getString("chip", "")
            mascotaVacunas = it.getString("vacunas", "")
            mascotaFoto = it.getString("foto", "pinguinoilerna.png")
            fotoMascota = mascotaFoto
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_editar_mascotas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseReference = FirebaseDatabase.getInstance().getReference("mascotas")

        tipoderaza = view.findViewById(R.id.SpinnerRaza)
        editTextNombre = view.findViewById(R.id.editTextNombre)
        editTextRaza = view.findViewById(R.id.editTextRaza)
        editTextChip = view.findViewById(R.id.editTextChip)
        editTextVacunas = view.findViewById(R.id.editTextVacunas)

        val mascotas = listOf("Gato", "Perro", "Hamster", "Pajaro", "Conejo", "Otro")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mascotas)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tipoderaza.adapter = adapter


        tipoderaza.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val tipoSeleccionado = mascotas[position]
                fotoMascota = when (tipoSeleccionado) {
                    "Gato" -> "gato.png"
                    "Perro" -> "perro.png"
                    "Hamster" -> "hamster.png"
                    "Pajaro" -> "pajaro.png"
                    "Conejo" -> "conejo.png"
                    else -> "pinguinoilerna.png"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                fotoMascota = mascotaFoto
            }
        }

        editTextNombre.setText(mascotaNombre)
        editTextRaza.setText(mascotaRaza)
        editTextChip.setText(mascotaChip)
        editTextVacunas.setText(mascotaVacunas)

        val tipoIndex = mascotas.indexOf(mascotaTipo)
        if (tipoIndex != -1) {
            tipoderaza.setSelection(tipoIndex)
        }

        val botonCambiar = view.findViewById<Button>(R.id.btnCambiar)

        botonCambiar.setOnClickListener {
            if (validarDatos()) {
                actualizarDatosEnFirebase()
                fragmentChangeListener?.onFragmentChange(Mascotas())
            }
        }
    }

    fun validarDatos(): Boolean {
        val nombre = editTextNombre.text.toString().trim()
        val raza = editTextRaza.text.toString().trim()
        val chip = editTextChip.text.toString().trim().lowercase()
        val vacunas = editTextVacunas.text.toString().trim()

        if (nombre.isEmpty()) {
            editTextNombre.error = "El nombre es obligatorio"
            return false
        }

        if (raza.isEmpty()) {
            editTextRaza.error = "La raza es obligatoria"
            return false
        }

        if (chip != "si" && chip != "no") {
            editTextChip.error = "Debes escribir Si o No"
            return false
        }

        if (vacunas.isEmpty()) {
            editTextVacunas.error = "Indica las vacunas o escribe 'ninguna'"
            return false
        }

        return true
    }

    fun getUserEmail(): String {
        val sharedPreferences = requireActivity().getSharedPreferences("Sesion", Context.MODE_PRIVATE)
        return sharedPreferences.getString("email", "") ?: ""
    }

    fun actualizarDatosEnFirebase() {
        val nombre = editTextNombre.text.toString().trim()
        val tipoMascota = tipoderaza.selectedItem.toString()
        val raza = editTextRaza.text.toString().trim()
        val chip = editTextChip.text.toString().trim()
        val vacunas = editTextVacunas.text.toString().trim()
        val userEmail = getUserEmail()

        val mascotaActualizada = hashMapOf(
            "id" to mascotaId,
            "nombre" to nombre,
            "tipo" to tipoMascota,
            "raza" to raza,
            "chip" to chip,
            "vacunas" to vacunas,
            "foto" to fotoMascota,
            "userEmail" to userEmail
        )
        databaseReference.child(mascotaId).setValue(mascotaActualizada)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Mascota actualizada correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentChangeListener) {
            fragmentChangeListener = context
        }
    }
}