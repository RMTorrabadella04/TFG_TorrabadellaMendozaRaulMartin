package com.example.lostpaws

import Data.Mensaje
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
    private lateinit var mensajesAdapter: MensajesAdapter
    private val listaMensajes = mutableListOf<Mensaje>()

    private var usuarioId: String? = null
    private var usuarioNombre: String? = null
    private var usuarioEmail: String? = null
    private var duenyoId: String? = null
    private var duenyoNombre: String? = null
    private var duenyoEmail: String? = null
    private var currentEmail: String? = null
    private var chatId: String? = null
    private var currentUserId: String? = null  // Add currentUserId variable

    private lateinit var mensajesRef: DatabaseReference

    companion object {
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
        arguments?.let {
            chatId = it.getString("CHAT_ID")
            usuarioId = it.getString("USUARIO_ID")
            usuarioNombre = it.getString("USUARIO_NOMBRE")
            usuarioEmail = it.getString("USUARIO_EMAIL")
            duenyoId = it.getString("DUENYO_ID")
            duenyoNombre = it.getString("DUENYO_NOMBRE")
            duenyoEmail = it.getString("DUENYO_EMAIL")
            currentEmail = it.getString("CURRENT_EMAIL")

            // Determine currentUserId based on email comparison
            currentUserId = if (currentEmail == duenyoEmail) duenyoId else usuarioId
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mensajes, container, false)

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

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true  // Para que los mensajes se vean desde abajo hacia arriba
        recyclerViewMensajes.layoutManager = layoutManager

        // Inicializar adaptador con el ID de usuario actual
        currentUserId?.let {
            mensajesAdapter = MensajesAdapter(requireContext(), listaMensajes, it)
            recyclerViewMensajes.adapter = mensajesAdapter
        } ?: run {
            Toast.makeText(requireContext(), "Error: ID de usuario actual no disponible", Toast.LENGTH_SHORT).show()
        }

        if (chatId != null) {
            mensajesRef = FirebaseDatabase.getInstance().getReference("mensajes").child(chatId!!)
            cargarMensajes()
        } else {
            Toast.makeText(requireContext(), "Error: ID de chat no disponible", Toast.LENGTH_SHORT).show()
        }

        btnEnviar.setOnClickListener {
            enviarMensaje()
        }

        // Log para depuración
        Log.d("Mensajes", "Información del chat: " +
                "ChatID=$chatId, UsuarioID=$usuarioId, UsuarioNombre=$usuarioNombre, " +
                "DuenyoID=$duenyoId, DuenyoNombre=$duenyoNombre, CurrentUserId=$currentUserId")
    }

    private fun cargarMensajes() {
        mensajesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaMensajes.clear()
                for (mensajeSnapshot in snapshot.children) {
                    try {
                        val id = mensajeSnapshot.key ?: ""
                        val texto = mensajeSnapshot.child("texto").getValue(String::class.java) ?: ""
                        val emisorId = mensajeSnapshot.child("emisorId").getValue(String::class.java) ?: ""
                        val emisorNombre = mensajeSnapshot.child("emisorNombre").getValue(String::class.java) ?: ""
                        val destinatarioId = mensajeSnapshot.child("destinatarioId").getValue(String::class.java) ?: ""
                        val destinatarioNombre = mensajeSnapshot.child("destinatarioNombre").getValue(String::class.java) ?: ""
                        val timestamp = mensajeSnapshot.child("timestamp").getValue(Long::class.java) ?: 0L

                        val mensaje = Mensaje(
                            id = id,
                            texto = texto,
                            emisorId = emisorId,
                            emisorNombre = emisorNombre,
                            destinatarioId = destinatarioId,
                            destinatarioNombre = destinatarioNombre,
                            timestamp = timestamp
                        )
                        listaMensajes.add(mensaje)
                    } catch (e: Exception) {
                        Log.e("Mensajes", "Error al procesar mensaje: ${e.message}")
                    }
                }

                mensajesAdapter.notifyDataSetChanged()

                // Hacer scroll al último mensaje si hay mensajes
                if (listaMensajes.isNotEmpty()) {
                    recyclerViewMensajes.scrollToPosition(listaMensajes.size - 1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Mensajes", "Error al cargar mensajes: ${error.message}")
                Toast.makeText(requireContext(), "Error al cargar mensajes: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
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

        var mensaje: HashMap<String, Any?>? = null

        if (currentEmail == duenyoEmail) {
            mensaje = hashMapOf(
                "texto" to textoMensaje,
                "emisorId" to duenyoId,
                "emisorNombre" to duenyoNombre,
                "destinatarioId" to usuarioId,
                "destinatarioNombre" to usuarioNombre,
                "timestamp" to ServerValue.TIMESTAMP
            )
        } else {
            mensaje = hashMapOf(
                "texto" to textoMensaje,
                "emisorId" to usuarioId,
                "emisorNombre" to usuarioNombre,
                "destinatarioId" to duenyoId,
                "destinatarioNombre" to duenyoNombre,
                "timestamp" to ServerValue.TIMESTAMP
            )
        }

        // Guardar mensaje en Firebase
        val nuevoMensajeRef = mensajesRef.push()
        nuevoMensajeRef.setValue(mensaje)
            .addOnSuccessListener {
                editTextMensaje.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al enviar mensaje: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}