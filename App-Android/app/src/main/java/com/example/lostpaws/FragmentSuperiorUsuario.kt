package com.example.lostpaws

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton


class FragmentSuperiorUsuario : Fragment() {

    private var fragmentSuperiorChangeListener: OnFragmentSuperiorChangeListener? = null
    private var fragmentChangeListener: OnFragmentChangeListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_superior_usuario, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Asegúrate de que la actividad implemente los listeners
        fragmentSuperiorChangeListener = context as? OnFragmentSuperiorChangeListener
        fragmentChangeListener = context as? OnFragmentChangeListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val botonAjustes = view.findViewById<ImageButton>(R.id.btnAjustes)

        botonAjustes.setOnClickListener {
            fragmentChangeListener?.onFragmentChange(Ajustes())
            fragmentSuperiorChangeListener?.onFragmentSuperiorChange(FragmentSuperiorInvitado())
        }
    }

}