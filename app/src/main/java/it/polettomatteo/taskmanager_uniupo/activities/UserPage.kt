package it.polettomatteo.taskmanager_uniupo.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import it.polettomatteo.taskmanager_uniupo.R

class UserPage : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.userpage)
        val textView = findViewById<TextView>(R.id.text1)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val str = "Bentornato " + currentUser?.email.toString() + "."
        textView.text = str


    }
}
