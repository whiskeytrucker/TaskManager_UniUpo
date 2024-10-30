package it.polettomatteo.taskmanager_uniupo.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import it.polettomatteo.taskmanager_uniupo.R

class UserPageFragment: Fragment() {
    private lateinit var text1:TextView
    private lateinit var textType: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.userpage, container, false)

        text1 = view.findViewById(R.id.username)
        textType = view.findViewById(R.id.textType)

        val bundle = this.arguments
        if(bundle != null){
            text1.text = getString(
                R.string.welcome_back_user,
                bundle.getString("username").toString().substringBefore('@')
            )

            val tipo = bundle.getString("tipo").toString()

            val ty: String = if(tipo.compareTo("pm") == 0) "Project Manager"
            else if(tipo.compareTo("pl") == 0) "Project Leader"
            else "Developer"

            textType.text = getString(R.string.type_user, ty)
        }

        return view
    }
}