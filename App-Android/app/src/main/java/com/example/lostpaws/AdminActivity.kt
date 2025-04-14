package com.example.lostpaws

import Data.Usuario
import Data.UsuarioVistaAdmin
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.example.lostpaws.UserAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AdminActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private val userList = mutableListOf<UsuarioVistaAdmin>()
    private val db: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        if (savedInstanceState == null) {
            val fragmentManager: FragmentManager = supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.fragment_container1, FragmentSuperiorInvitado())
            fragmentTransaction.commit()
        }

        val opcionesBusqueda = listOf("ID", "Nombre", "Email", "Todo")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesBusqueda)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinner: Spinner = findViewById(R.id.queTipodeBusqueda)
        spinner.adapter = spinnerAdapter

        // EditText para la búsqueda
        val editText: EditText = findViewById(R.id.textoBusqueda)

        val botonBusqueda: Button = findViewById(R.id.btnBuscar)

        // Cambiar el hint dependiendo de la opción seleccionada
        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                val searchType = parentView.getItemAtPosition(position).toString()

                when (searchType) {
                    "ID" -> editText.hint = "Introduce el ID"
                    "Nombre" -> editText.hint = "Introduce el Nombre"
                    "Email" -> editText.hint = "Introduce el Email"
                    "Todo" -> editText.hint = "Buscar todos los usuarios"
                }

                userList.clear()
                adapter.notifyDataSetChanged()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Si no se selecciona nada, el hint será para buscar todos los usuarios
                editText.hint = "Buscar todos los usuarios"
                userList.clear()
                adapter.notifyDataSetChanged()
            }
        })

        recyclerView = findViewById(R.id.userList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UserAdapter(this, userList) { usuario ->
            eliminarUsuario(usuario)
        }
        recyclerView.adapter = adapter

        // Fuera del spinner.setOnItemSelectedListener
        botonBusqueda.setOnClickListener {
            val searchType = spinner.selectedItem.toString()  // Opción actual del spinner
            realizarBusqueda(searchType)
        }

    }


    private fun realizarBusqueda(searchType: String) {
        val query = findViewById<EditText>(R.id.textoBusqueda).text.toString()

        userList.clear()

        when (searchType) {
            "Todo" -> {
                db.child("users")
                    .get()
                    .addOnSuccessListener { snapshot ->
                        for (child in snapshot.children) {
                            val id = child.key ?: ""
                            val name = child.child("name").getValue(String::class.java) ?: ""
                            val email = child.child("email").getValue(String::class.java) ?: ""
                            val password = child.child("password").getValue(String::class.java) ?: ""

                            val usuario = UsuarioVistaAdmin(id, name, email, password)
                            userList.add(usuario)
                        }
                        adapter.notifyDataSetChanged()
                    }
            }

            "ID" -> {
                db.child("users").child(query)  // query contiene el ID completo
                    .get()
                    .addOnSuccessListener { snapshot ->
                        userList.clear()
                        if (snapshot.exists()) {
                            val id = snapshot.key ?: ""
                            val name = snapshot.child("name").getValue(String::class.java) ?: ""
                            val email = snapshot.child("email").getValue(String::class.java) ?: ""
                            val password = snapshot.child("password").getValue(String::class.java) ?: ""

                            val usuario = UsuarioVistaAdmin(id, name, email, password)
                            userList.add(usuario)
                        }
                        adapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener {
                        // Manejar error
                    }
            }

            "Nombre" -> {

                db.child("users").orderByChild("name").equalTo(query)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        userList.clear()
                        for (child in snapshot.children) {
                            val id = child.key ?: ""
                            val name = child.child("name").getValue(String::class.java) ?: ""
                            val email = child.child("email").getValue(String::class.java) ?: ""
                            val password = child.child("password").getValue(String::class.java) ?: ""

                            val usuario = UsuarioVistaAdmin(id, name, email, password)
                            userList.add(usuario)
                        }
                        adapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener {
                        // Manejar error
                    }
            }

            "Email" -> {

                db.child("users").orderByChild("email").equalTo(query)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        userList.clear()
                        for (child in snapshot.children) {
                            val id = child.key ?: ""
                            val name = child.child("name").getValue(String::class.java) ?: ""
                            val email = child.child("email").getValue(String::class.java) ?: ""
                            val password = child.child("password").getValue(String::class.java) ?: ""

                            val usuario = UsuarioVistaAdmin(id, name, email, password)
                            userList.add(usuario)
                        }
                        adapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener {
                        // Manejar error
                    }
            }
        }
    }



    private fun eliminarUsuario(usuario: UsuarioVistaAdmin) {
        usuario.id?.let {
            db.child("users").child(it).removeValue()
                .addOnSuccessListener {
                    userList.remove(usuario)
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    // Manejar error al eliminar usuario
                }
        }
    }

}
