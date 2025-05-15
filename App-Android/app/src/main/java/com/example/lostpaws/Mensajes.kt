package com.example.lostpaws

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class Mensajes : Fragment() {

    private lateinit var recyclerViewMensajes: RecyclerView
    private lateinit var editTextMensaje: EditText
    private lateinit var btnEnviar: Button
    private lateinit var textNombre: TextView

    // Variables para almacenar los datos del chat
    private var usuarioId: String? = null
    private var usuarioNombre: String? = null
    private var usuarioEmail: String? = null
    private var duenyoId: String? = null
    private var duenyoNombre: String? = null
    private var duenyoEmail: String? = null
    private var currentEmail: String? = null
    private var chatId: String? = null


    // Base de datos
    private lateinit var mensajesRef: DatabaseReference

    companion object {
        // Método estático para crear una nueva instancia con parámetros
        fun newInstance(chatId: String, usuarioId: String, usuarioNombre: String, usuarioEmail: String,
                        duenyoId: String, duenyoNombre: String, duenyoEmail: String, currentEmail: String): Mensajes {
            val fragment = Mensajes()
            val args = Bundle()
            args.putString("CHAT_ID", chatId)
            args.putString("USUARIO_ID", usuarioId)
            args.putString("USUARIO_NOMBRE", usuarioNombre)
            args.putString("USUARIO_EMAIL", usuarioEmail)
            args.putString("DUENYO_ID", duenyoId)
            args.putString("DUENYO_NOMBRE", duenyoNombre)
            args.putString("DUENYO_EMAIL", duenyoEmail)
            args.putString("CURRENT_EMAIL", currentEmail)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Recuperar argumentos
        arguments?.let {
            chatId = it.getString("CHAT_ID")
            usuarioId = it.getString("USUARIO_ID")
            usuarioNombre = it.getString("USUARIO_NOMBRE")
            usuarioEmail = it.getString("USUARIO_EMAIL")
            duenyoId = it.getString("DUENYO_ID")
            duenyoNombre = it.getString("DUENYO_NOMBRE")
            duenyoEmail = it.getString("DUENYO_EMAIL")
            currentEmail = it.getString("CURRENT_EMAIL")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_mensajes, container, false)

        // Inicializar vistas
        recyclerViewMensajes = view.findViewById(R.id.recyclerViewMensajes)
        editTextMensaje = view.findViewById(R.id.editTextMensaje)
        btnEnviar = view.findViewById(R.id.btnEnviar)
        textNombre = view.findViewById(R.id.textNombre)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (currentEmail == duenyoEmail) {
            // Si el usuario actual es el dueño, mostrar información del usuario
            textNombre.text = usuarioNombre ?: "Chat"
        } else {
            // Si el usuario actual es el usuario, mostrar información del dueño
            textNombre.text = duenyoNombre ?: "Chat"
        }

        // Configurar RecyclerView
        recyclerViewMensajes.layoutManager = LinearLayoutManager(requireContext())
        // Aquí necesitarías crear un adaptador para los mensajes

        // Configurar referencia a Firebase para los mensajes
        if (chatId != null) {
            mensajesRef = FirebaseDatabase.getInstance().getReference("mensajes").child(chatId!!)
            cargarMensajes()
        } else {
            Toast.makeText(requireContext(), "Error: ID de chat no disponible", Toast.LENGTH_SHORT).show()
        }

        // Configurar botón de enviar
        btnEnviar.setOnClickListener {
            enviarMensaje()
        }

        // Log para depuración
        Log.d("Mensajes", "Información del chat: " +
                "ChatID=$chatId, UsuarioID=$usuarioId, UsuarioNombre=$usuarioNombre, " +
                "DuenyoID=$duenyoId, DuenyoNombre=$duenyoNombre")
    }

    private fun cargarMensajes() {
        // Implementación para cargar mensajes desde Firebase
        // Esta función deberá configurar un ValueEventListener para obtener
        // los mensajes de la conversación específica
    }

    private fun enviarMensaje() {
        val textoMensaje = editTextMensaje.text.toString().trim()

        if (textoMensaje.isEmpty()) {
            return
        }

        if (chatId == null || usuarioId == null) {
            Toast.makeText(requireContext(), "Error: Información de chat incompleta", Toast.LENGTH_SHORT).show()
            return
        }

        // Estructura ejemplo para guardar un mensaje
        val mensaje = hashMapOf(
            "texto" to textoMensaje,
            "emisorId" to usuarioId,
            "emisorNombre" to usuarioNombre,
            "timestamp" to ServerValue.TIMESTAMP
        )

        // Guardar mensaje en Firebase
        val nuevoMensajeRef = mensajesRef.push()
        nuevoMensajeRef.setValue(mensaje)
            .addOnSuccessListener {
                // Limpiar campo de texto
                editTextMensaje.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al enviar mensaje: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}