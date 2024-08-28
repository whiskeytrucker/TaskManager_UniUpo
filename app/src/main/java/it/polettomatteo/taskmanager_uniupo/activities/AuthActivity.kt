package it.polettomatteo.taskmanager_uniupo.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import it.polettomatteo.taskmanager_uniupo.R

class AuthActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var TAG = "Authentication"

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        auth = FirebaseAuth.getInstance()
    }

    public override fun onStart(){
        super.onStart()
        val currentUser = auth.currentUser

        val buttLogin = findViewById<Button>(R.id.firstBtn)
        buttLogin.setOnClickListener {
            // mettere controllo mail e password
            var textInput = findViewById<TextInputEditText>(R.id.email)
            //val email = textInput.text.toString()
            val email = "d1@testapp.com"

            textInput = findViewById(R.id.password)
            //var pass = textInput.text.toString()
            val pass = "admin123"

            Log.d("CURRENT_USER",currentUser?.email.toString())
            // check user already signed in
            if (currentUser == null) {
                login(email, pass)
            }
        }
    }





    fun login(
        email: String,
        password: String,
    ){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){task ->
                if(task.isSuccessful){
                    Toast.makeText(baseContext, "Login effettuato!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                }else{
                    Toast.makeText(baseContext, "Autenticazione fallita.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
