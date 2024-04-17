package it.polettomatteo.taskmanager_uniupo.fragments

import android.os.Bundle
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
            text1.text = "Bentornato " + bundle["username"].toString() + "!"
            textType.text = "Sei un " + bundle["tipo"].toString().toUpperCase() + "."
        }

        return view
    }
}