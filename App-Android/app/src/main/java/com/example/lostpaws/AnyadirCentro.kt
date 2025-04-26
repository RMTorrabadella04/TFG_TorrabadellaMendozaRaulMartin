package com.example.lostpaws

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.lostpaws.Data.Refugio
import com.example.lostpaws.Data.Veterinario
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AnyadirCentro : Fragment() {

    private lateinit var db: DatabaseReference

    private lateinit var nombreEditText: EditText
    private lateinit var direccionEditText: EditText
    private lateinit var telefonoEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var extraEditText: EditText
    private lateinit var tipoCentroSpinner: Spinner
    private lateinit var botonGuardar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseDatabase.getInstance().reference
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_anyadir_centro, container, false)

        nombreEditText = view.findViewById(R.id.editTextNombre)
        direccionEditText = view.findViewById(R.id.editTextDireccion)
        telefonoEditText = view.findViewById(R.id.editTextTelefono)
        emailEditText = view.findViewById(R.id.editTextEmail)
        extraEditText = view.findViewById(R.id.editTextExtra)
        tipoCentroSpinner = view.findViewById(R.id.tipoCentro)
        botonGuardar = view.findViewById(R.id.btnGuardar)

        val opcionesBusqueda = listOf("Refugios", "Veterinarios")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opcionesBusqueda)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tipoCentroSpinner.adapter = spinnerAdapter

        tipoCentroSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedView: View?,
                position: Int,
                id: Long
            ) {
                when (parentView.getItemAtPosition(position).toString()) {
                    "Refugios" -> {
                        extraEditText.hint = "Introduzca la Capacidad del Refugio"
                    }
                    "Veterinarios" -> {
                        extraEditText.hint = "Introduzca si tiene Centro de Operaciones el Veterinario (Si/No)"
                    }
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        botonGuardar.setOnClickListener {
            guardarCentro()
        }

        return view
    }

    private fun guardarCentro() {
        val nombre = nombreEditText.text.toString().trim()
        val direccion = direccionEditText.text.toString().trim()
        val telefono = telefonoEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val extra = extraEditText.text.toString().trim()
        val tipoCentro = tipoCentroSpinner.selectedItem.toString()

        if (nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty() || email.isEmpty() || extra.isEmpty()) {
            Toast.makeText(requireContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "Correo inválido", Toast.LENGTH_SHORT).show()
            return
        }

        val centrosRef = db.child("centros")
        val centroId = centrosRef.push().key

        when (tipoCentro) {
            "Refugios" -> {
                val capacidad = extra.toIntOrNull()
                if (capacidad == null) {
                    Toast.makeText(requireContext(), "Capacidad inválida", Toast.LENGTH_SHORT).show()
                    return
                }

                val refugio = Refugio(nombre, direccion, telefono, email, capacidad)
                centroId?.let {
                    centrosRef.child(it).setValue(refugio)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Refugio guardado", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Error al guardar refugio", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            "Veterinarios" -> {
                val puedeOperar = extra.equals("si", ignoreCase = true)
                val veterinario = Veterinario(nombre, direccion, telefono, email, puedeOperar)
                centroId?.let {
                    centrosRef.child(it).setValue(veterinario)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Veterinario guardado", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Error al guardar veterinario", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }
}
