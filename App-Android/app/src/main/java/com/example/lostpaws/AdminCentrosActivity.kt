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

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerView.Adapter<*>
    private val centrosRList = mutableListOf<Refugio>()
    private val centrosVList = mutableListOf<Veterinario>()
    private val db: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_centros)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            val fragmentManager: FragmentManager = supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.fragment_container1, FragmentSuperiorInvitado())
            fragmentTransaction.commit()
        }

        val opcionesBusqueda = listOf("Refugios", "Veterinarios")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesBusqueda)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinner: Spinner = findViewById(R.id.queTipodeBusqueda)
        spinner.adapter = spinnerAdapter

        val editText: EditText = findViewById(R.id.textoBusqueda)
        val botonBusqueda: Button = findViewById(R.id.btnBuscar)
        val botonAdminUsers: Button = findViewById(R.id.btnAdminUsers)

        recyclerView = findViewById(R.id.userList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                val searchType = parentView.getItemAtPosition(position).toString()

                when (searchType) {
                    "Refugios" -> {
                        editText.hint = "Introduce el Nombre del Refugio"
                        centrosRList.clear()
                        adapter = RefugioAdapter(this@AdminCentrosActivity, centrosRList) { refugio ->
                            // Acción de eliminar refugio (si la necesitas)
                        }
                    }
                    "Veterinarios" -> {
                        editText.hint = "Introduce el Nombre del Veterinario"
                        centrosVList.clear()
                        adapter = VeterinarioAdapter(this@AdminCentrosActivity, centrosVList) { veterinario ->
                            // Acción de eliminar veterinario (si la necesitas)
                        }
                    }
                }

                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                editText.hint = "Buscar todos los usuarios"
                centrosRList.clear()
                centrosVList.clear()
                adapter.notifyDataSetChanged()
            }
        }

        botonBusqueda.setOnClickListener {
            val queryText = editText.text.toString().trim()
            val tipoBusqueda = spinner.selectedItem.toString()

            if (tipoBusqueda == "Refugios") {
                centrosRList.clear()
                db.child("refugios").orderByChild("nombre").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (refSnapshot in snapshot.children) {
                            val refugio = refSnapshot.getValue(Refugio::class.java)
                            if (refugio != null && (queryText.isEmpty() || refugio.nombre.contains(queryText, ignoreCase = true))) {
                                centrosRList.add(refugio)
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Manejar error si lo deseas
                    }
                })
            } else if (tipoBusqueda == "Veterinarios") {
                centrosVList.clear()
                db.child("veterinarios").orderByChild("nombre").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (vetSnapshot in snapshot.children) {
                            val veterinario = vetSnapshot.getValue(Veterinario::class.java)
                            if (veterinario != null && (queryText.isEmpty() || veterinario.nombre.contains(queryText, ignoreCase = true))) {
                                centrosVList.add(veterinario)
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Manejar error si lo deseas
                    }
                })
            }
        }

        botonAdminUsers.setOnClickListener {
            val intent = Intent(this, AdminActivity::class.java)
            startActivity(intent)
        }
    }
}
