package it.polettomatteo.taskmanager_uniupo.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import it.polettomatteo.taskmanager_uniupo.R

var TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var currentUser: FirebaseUser? = null

    // --------------- FUNZIONI ACTIVITY ---------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser

        initToolbar(toolbar)


    }



    // --------------- FUNZIONI AUSILIARIE ---------------
    private fun initToolbar(toolbar: Toolbar){
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setHomeButtonEnabled(true)
        }

        val mainLayout = findViewById<DrawerLayout>(R.id.mainLayout)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)

        toolbar.setNavigationOnClickListener {
            mainLayout.openDrawer(GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.login -> {
                    val intent = Intent(this, AuthActivity::class.java)
                    startActivity(intent)
                }
                R.id.logout -> {
                    if(currentUser != null){
                        auth.signOut()

                        Toast.makeText(baseContext, "Logout Effettuato", Toast.LENGTH_SHORT).show()

                    }
                }

            }
            true
        }
    }

    fun updateUI(
        navigationView: NavigationView
    ){
        val isLoggedIn = currentUser != null
        val isNotLoggedIn = ! isLoggedIn

        Log.d("isLoggedIn", isLoggedIn.toString() + isNotLoggedIn.toString())

        val menu = navigationView.menu

        menu.findItem(R.id.login)?.isVisible = isNotLoggedIn
        menu.findItem(R.id.logout)?.isVisible = isLoggedIn

        recreate()
    }
}
