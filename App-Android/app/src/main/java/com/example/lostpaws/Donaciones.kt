package com.example.lostpaws

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelStore
import java.util.Calendar


class Donaciones : Fragment() {

    private var fragmentChangeListener: OnFragmentChangeListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_donaciones, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val botonEnviar = view.findViewById<Button>(R.id.btnEnviar)
        val cuentaEmisor = view.findViewById<EditText>(R.id.nCuentaEmisor)
        val fechaCaducidad = view.findViewById<EditText>(R.id.fCaducidad)
        val cvv = view.findViewById<EditText>(R.id.CVV)
        val cuentaDestinatario = view.findViewById<EditText>(R.id.nCuentaDestinatario)

        botonEnviar.setOnClickListener{

            val emisor = cuentaEmisor.text.toString()
            val caducidad = fechaCaducidad.text.toString()
            val codigoCVV = cvv.text.toString()
            val destinatario = cuentaDestinatario.text.toString()

            if(validaciondatos(emisor, caducidad, codigoCVV, destinatario)){
                Toast.makeText(context, "La donación se ha realizado con exito.", Toast.LENGTH_SHORT).show()

                fragmentChangeListener?.onFragmentChange(Ajustes())
            }
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Asegúrate de que la actividad implemente los listeners
        fragmentChangeListener = context as? OnFragmentChangeListener
    }

    fun validaciondatos(emisor: String, caducidad: String, codigoCVV: String, destinatario: String): Boolean {

        var correcto = true

        if (emisor.length != 16) {
            Toast.makeText(context, "El número de cuenta emisora debe tener 16 dígitos.", Toast.LENGTH_SHORT).show()
            correcto = false
        }

        if (destinatario.length != 16) {
            Toast.makeText(context, "El número de cuenta destinataria debe tener 16 dígitos.", Toast.LENGTH_SHORT).show()
            correcto = false
        }

        if (codigoCVV.length != 3) {
            Toast.makeText(context, "El CVV debe tener 3 dígitos.", Toast.LENGTH_SHORT).show()
            correcto = false
        }

        correcto = validarfechacaducidad(caducidad, correcto)

        return correcto
    }

    private fun validarfechacaducidad(fCaducidad: String, correcto: Boolean): Boolean{

        var correctoLocal = correcto

        // Validación del formato de la fecha
        if (!fCaducidad.matches(Regex("^\\d{2}/\\d{2}$"))) {
            Toast.makeText(context, "El formato de la fecha de caducidad es incorrecto. Usa MM/YY.", Toast.LENGTH_SHORT).show()
            correctoLocal =false
        }

        // Extraemos el mes y año
        val partes = fCaducidad.split("/")
        if (partes.size != 2) {
            Toast.makeText(context, "Fecha incompleta.", Toast.LENGTH_SHORT).show()
            return false
        }

        // Convertimos a enteros para hacer comparaciones
        try {
            val mes = partes[0].toInt()
            val año = partes[1].toInt()

            // Validamos el mes
            if (mes !in 1..12) {
                Toast.makeText(context, "El mes debe estar entre 01 y 12.", Toast.LENGTH_SHORT).show()
                correctoLocal =false
            }

            // Validamos el año
            val añoActual = Calendar.getInstance().get(Calendar.YEAR) % 100 // Año actual sin los dos primeros dígitos
            if (año < añoActual) {
                Toast.makeText(context, "El año de caducidad debe ser mayor o igual al año actual.", Toast.LENGTH_SHORT).show()
                correctoLocal =false
            }

        } catch (e: NumberFormatException) {
            Toast.makeText(context, "Mes y año deben ser numéricos.", Toast.LENGTH_SHORT).show()
            return false
        }

        return correctoLocal
    }


    // Para los Test (Debido a que usa toast)
    // Versión para tests de la función validaciondatos (elimina dependencias de Android)

    fun validacionDatosDonacionParaTest(emisor: String, caducidad: String, codigoCVV: String, destinatario: String): Boolean {
        var correcto = true

        if (emisor.length != 16) {
            correcto = false
        }

        if (destinatario.length != 16) {
            correcto = false
        }

        if (codigoCVV.length != 3) {
            correcto = false
        }

        correcto = validarFechaCaducidadParaTest(caducidad, correcto)

        return correcto
    }

    // Versión para tests de la función validarfechacaducidad
    fun validarFechaCaducidadParaTest(fCaducidad: String, correcto: Boolean): Boolean {
        var correctoLocal = correcto

        // Validación del formato de la fecha
        if (!fCaducidad.matches(Regex("^\\d{2}/\\d{2}$"))) {
            correctoLocal = false
            return correctoLocal
        }

        // Extraemos el mes y año
        val partes = fCaducidad.split("/")
        if (partes.size != 2) {
            return false
        }

        // Convertimos a enteros para hacer comparaciones
        try {
            val mes = partes[0].toInt()
            val año = partes[1].toInt()

            // Validamos el mes
            if (mes !in 1..12) {
                correctoLocal = false
            }

            // Validamos el año
            val añoActual = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100
            if (año < añoActual) {
                correctoLocal = false
            }

        } catch (e: NumberFormatException) {
            return false
        }

        return correctoLocal
    }

}