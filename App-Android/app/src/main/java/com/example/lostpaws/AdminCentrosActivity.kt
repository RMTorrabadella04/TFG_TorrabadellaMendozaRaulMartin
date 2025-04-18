package com.example.lostpaws

import Data.UsuarioVistaAdmin
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lostpaws.Data.Refugio
import com.example.lostpaws.Data.Veterinario
import com.google.firebase.database.*

class AdminCentrosActivity : AppCompatActivity() {

    private val db: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_centros)

        if (savedInstanceState == null) {
            val fragmentManager: FragmentManager = supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.fragment_container1, FragmentSuperiorInvitado())
            fragmentTransaction.commit()
        }

        val extra: EditText = this.findViewById(R.id.editTextExtra)
        val opcionesBusqueda = listOf("Refugios", "Veterinarios")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesBusqueda)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinner: Spinner = this.findViewById(R.id.tipoCentro)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedView: View?,
                position: Int,
                id: Long
            ) {
                val searchType = parentView.getItemAtPosition(position).toString()

                //
                when (searchType) {
                    "Refugios" -> {

                        extra.hint = "Introduzca la Capacidad del Refugio"

                    }

                    "Veterinarios" -> {

                        extra.hint = "Introduzca si tiene Centro de Operaciones el Veterinario (Si/No)"

                    }
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        val botonAdminUsers: Button = findViewById(R.id.btnAdminUsers)

        botonAdminUsers.setOnClickListener {
            val intent = Intent(this, AdminActivity::class.java)
            startActivity(intent)
        }

        val botonGuardar: Button = findViewById(R.id.btnGuardar)

        botonGuardar.setOnClickListener {
            val nombre = findViewById<EditText>(R.id.editTextNombre).text.toString().trim()
            val direccion = findViewById<EditText>(R.id.editTextDireccion).text.toString().trim()
            val telefono = findViewById<EditText>(R.id.editTextTelefono).text.toString().trim()
            val email = findViewById<EditText>(R.id.editTextEmail).text.toString().trim()
            val extra = findViewById<EditText>(R.id.editTextExtra).text.toString().trim()
            val tipoCentro = spinner.selectedItem.toString()

            if (nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty() || email.isEmpty() || extra.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val centrosRef = db.child("centros")
            val centroId = centrosRef.push().key

            when (tipoCentro) {
                "Refugios" -> {
                    val capacidad = extra.toIntOrNull()
                    if (capacidad == null) {
                        Toast.makeText(this, "Capacidad inválida", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val refugio = Refugio(nombre, direccion, telefono, email, capacidad)
                    centroId?.let {
                        centrosRef.child(it).setValue(refugio)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Refugio guardado", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Error al guardar refugio", Toast.LENGTH_SHORT).show()
                            }
                    }
                }

                "Veterinarios" -> {
                    val puedeOperar = extra.equals("si", ignoreCase = true)
                    val veterinario = Veterinario(nombre, direccion, telefono, email, puedeOperar)
                    centroId?.let {
                        centrosRef.child(it).setValue(veterinario)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Veterinario guardado", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Error al guardar veterinario", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
        }

    }

}
