package com.example.lostpaws

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import Data.Chat
import com.google.firebase.database.*

class Chats : Fragment() {

    private var fragmentChangeListener: OnFragmentChangeListener? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatsList: MutableList<Chat>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var chatListener: ValueEventListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener email del usuario actual
        val sharedPreferences = requireContext().getSharedPreferences("Sesion", Context.MODE_PRIVATE)
        val currentUserEmail = sharedPreferences.getString("email", "") ?: ""

        // Inicializar RecyclerView
        recyclerView = view.findViewById(R.id.recicleviewchat)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Inicializar la lista de chats
        chatsList = mutableListOf()

        // Configurar el adaptador
        chatAdapter = ChatAdapter(
            requireContext(),
            chatsList,
            { chat -> navegarAConversacion(chat, currentUserEmail) }
        )
        recyclerView.adapter = chatAdapter



        if (currentUserEmail.isEmpty()) {
            Toast.makeText(requireContext(), "No se pudo obtener el email del usuario", Toast.LENGTH_SHORT).show()
            return
        }

        // Inicializar Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("relacionChats")

        // Crear el listener para Firebase
        chatListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatsList.clear()

                for (chatSnapshot in snapshot.children) {
                    try {
                        val chat = chatSnapshot.getValue(Chat::class.java)
                        chat?.let {
                            // Solo añadir chats donde el usuario actual es dueño o usuario
                            if (it.duenyoEmail == currentUserEmail || it.usuarioEmail == currentUserEmail) {
                                chatsList.add(it)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("ChatsFragment", "Error al deserializar chat: ${e.message}")
                    }
                }

                // Notificar al adaptador de los cambios
                chatAdapter.notifyDataSetChanged()

                // Mostrar mensaje si no hay chats
                if (chatsList.isEmpty()) {
                    Toast.makeText(requireContext(), "No tienes chats activos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatsFragment", "Error al cargar chats: ${error.message}")
                Toast.makeText(requireContext(), "Error al cargar los chats", Toast.LENGTH_SHORT).show()
            }
        }

        // Añadir el listener inmediatamente después de crearlo
        databaseReference.addValueEventListener(chatListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Asegurarse de eliminar el listener al destruir el fragmento
        if (::databaseReference.isInitialized && ::chatListener.isInitialized) {
            databaseReference.removeEventListener(chatListener)
        }
    }

    private fun navegarAConversacion(chat: Chat, currentUserEmail: String) {

        val mensajesFragment = Mensajes.newInstance(
            chatId = chat.id,
            usuarioId = chat.usuarioId,
            usuarioNombre = chat.usuarioName,
            usuarioEmail= chat.usuarioEmail,
            duenyoId = chat.duenyoId,
            duenyoNombre = chat.duenyoName,
            duenyoEmail= chat.duenyoEmail,
            currentEmail= currentUserEmail
        )

        // Navegar al fragmento Mensajes
        fragmentChangeListener?.onFragmentChange(mensajesFragment)

        Toast.makeText(requireContext(), "Chat seleccionado: ${chat.duenyoName} - ${chat.usuarioName}", Toast.LENGTH_SHORT).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Asegúrate de que el Activity implementa el OnFragmentChangeListener
        fragmentChangeListener = context as? OnFragmentChangeListener
    }
}