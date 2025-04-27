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

class AnyadirMascotas : Fragment() {

    private var fragmentChangeListener: OnFragmentChangeListener? = null
    private lateinit var tipoderaza: Spinner
    private lateinit var editTextNombre: EditText
    private lateinit var editTextRaza: EditText
    private lateinit var editTextChip: EditText
    private lateinit var editTextVacunas: EditText
    private lateinit var databaseReference: DatabaseReference
    private var fotoMascota: String = "pinguinoilerna.png" // Valor por defecto

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_anyadir_mascotas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("mascotas")

        // Inicializar vistas
        tipoderaza = view.findViewById(R.id.SpinnerRaza)
        editTextNombre = view.findViewById(R.id.editTextNombre)
        editTextRaza = view.findViewById(R.id.editTextRaza)
        editTextChip = view.findViewById(R.id.editTextChip)
        editTextVacunas = view.findViewById(R.id.editTextVacunas)

        val mascotas = listOf("Gato", "Perro", "Hamster", "Pajaro", "Conejo", "Otro")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mascotas)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        tipoderaza.adapter = adapter

        // Configurar listener para el spinner para seleccionar la foto según el tipo
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
                fotoMascota = "pinguinoilerna.png"
            }
        }

        val botonGuardar = view.findViewById<Button>(R.id.btnGuardar)

        botonGuardar.setOnClickListener {
            if (validarDatos()) {
                guardarDatosEnFirebase()
                fragmentChangeListener?.onFragmentChange(Mascotas())
            }
        }
    }

    fun validarDatos(): Boolean {
        val nombre = editTextNombre.text.toString().trim()
        val raza = editTextRaza.text.toString().trim()
        val chip = editTextChip.text.toString().trim().lowercase()
        val vacunas = editTextVacunas.text.toString().trim()

        // Validación del nombre
        if (nombre.isEmpty()) {
            editTextNombre.error = "El nombre es obligatorio"
            return false
        }

        // Validación de la raza
        if (raza.isEmpty()) {
            editTextRaza.error = "La raza es obligatoria"
            return false
        }

        // Validación del chip (debe ser "si" o "no")
        if (chip != "si" && chip != "no") {
            editTextChip.error = "Debes escribir Si o No"
            return false
        }

        // Validación de vacunas (opcional, pero podríamos establecer algún criterio)
        if (vacunas.isEmpty()) {
            editTextVacunas.error = "Indica las vacunas o escribe 'ninguna'"
            return false
        }

        return true
    }

    fun guardarDatosEnFirebase() {
        val nombre = editTextNombre.text.toString().trim()
        val tipoMascota = tipoderaza.selectedItem.toString()
        val raza = editTextRaza.text.toString().trim()
        val chip = editTextChip.text.toString().trim()
        val vacunas = editTextVacunas.text.toString().trim()

        // Generar un ID único para esta mascota
        val mascotaId = databaseReference.push().key

        if (mascotaId != null) {
            // Crear un mapa con los datos a guardar
            val mascota = hashMapOf(
                "id" to mascotaId,
                "nombre" to nombre,
                "tipo" to tipoMascota,
                "raza" to raza,
                "chip" to chip,
                "vacunas" to vacunas,
                "foto" to fotoMascota
            )

            // Guardar en Firebase
            databaseReference.child(mascotaId).setValue(mascota)
                .addOnSuccessListener {
                    // Mostrar mensaje de éxito
                    Toast.makeText(requireContext(), "Mascota guardada correctamente", Toast.LENGTH_SHORT).show()

                    // Limpiar los campos del formulario
                    editTextNombre.text.clear()
                    editTextRaza.text.clear()
                    editTextChip.text.clear()
                    editTextVacunas.text.clear()
                    tipoderaza.setSelection(0)
                }
                .addOnFailureListener { e ->
                    // Mostrar mensaje de error
                    Toast.makeText(requireContext(), "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Error al generar ID para la mascota", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentChangeListener) {
            fragmentChangeListener = context
        }
    }
}