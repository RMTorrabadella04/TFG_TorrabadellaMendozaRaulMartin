package com.example.lostpaws

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.lostpaws.Data.Centro
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*

class DialogAbandono : DialogFragment() {

    private lateinit var spinner: Spinner
    private lateinit var editTextFecha: TextInputEditText
    private lateinit var btnCancelar: Button
    private lateinit var btnReportar: Button

    private val refugiosList = mutableListOf<Pair<String, Centro>>()
    private val nombresRefugios = mutableListOf<String>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = android.app.AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_reporte_abandono, null)

        spinner = view.findViewById(R.id.spinerRefugios)
        editTextFecha = view.findViewById(R.id.editTextFechaPerdida)
        btnCancelar = view.findViewById(R.id.btnCancelar)
        btnReportar = view.findViewById(R.id.btnReportar)

        cargarRefugiosDesdeFirebase()

        btnCancelar.setOnClickListener { dismiss() }

        btnReportar.setOnClickListener {
            val posicion = spinner.selectedItemPosition
            if (posicion != -1) {
                val (idRefugio, _) = refugiosList[posicion]
                val datos = mapOf(
                    "fechaAbandono" to editTextFecha.text.toString(),
                    "refugioId" to idRefugio
                )
                FirebaseDatabase.getInstance().getReference("reportes").push().setValue(datos)
                dismiss()
            }
        }

        builder.setView(view)
        return builder.create()
    }

    private fun cargarRefugiosDesdeFirebase() {
        val databaseRef = FirebaseDatabase.getInstance().getReference("centros")
        databaseRef.orderByChild("tipo").equalTo("refugio")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    refugiosList.clear()
                    nombresRefugios.clear()

                    for (refugioSnap in snapshot.children) {
                        val id = refugioSnap.key ?: continue
                        val centro = refugioSnap.getValue(Centro::class.java) ?: continue
                        refugiosList.add(Pair(id, centro))
                        nombresRefugios.add(centro.nombre)
                    }

                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nombresRefugios)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejo de error
                }
            })
    }
}
