package it.polettomatteo.taskmanager_uniupo.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.firebase.ProjectsDB
import it.polettomatteo.taskmanager_uniupo.firebase.UsersDB
import it.polettomatteo.taskmanager_uniupo.fragments.ProjectsViewFragment
import it.polettomatteo.taskmanager_uniupo.fragments.SubtasksViewFragment
import it.polettomatteo.taskmanager_uniupo.fragments.TasksViewFragment
import it.polettomatteo.taskmanager_uniupo.fragments.UserPageFragment
import it.polettomatteo.taskmanager_uniupo.interfaces.StartNewRecycler

var TAG = "MainActivity"



class MainActivity : AppCompatActivity(){
    private lateinit var auth: FirebaseAuth
    var currentUser: FirebaseUser? = null


    // --------------- FUNZIONI ACTIVITY ---------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(applicationContext)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser

        initToolbar(toolbar)
    }

    override fun onStart(){
        super.onStart()
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frameLayout)

        if(currentFragment == null){
            this.createFragments()
        }
    }


    override fun onDestroy() {
        if(currentUser != null){
            auth.signOut()
        }
        super.onDestroy()
    }

    private fun createFragments(){
        if(currentUser != null){
            currentUser!!.email?.let {
                ProjectsDB.getProjects(it) { bundle ->
                    if (bundle != null) {
                        this.setupFragment(ProjectsViewFragment(), bundle)
                    }
                }
            }
        }

    }



    private fun setupFragment(fragment: Fragment, bundle: Bundle? = null) {
        if(bundle != null){
            bundle.putSerializable("task_interface",  taskListener)
            fragment.arguments = bundle
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
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
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                }

                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }



                R.id.userpage -> {
                    if(currentUser != null) {
                        UsersDB.getUserType(currentUser?.email.toString()) { bundle ->
                            if (bundle != null) {
                                bundle.putString("username", currentUser?.email)
                                mainLayout.closeDrawer(navigationView)
                                setupFragment(UserPageFragment(), bundle)
                            }
                        }
                    }
                }

                R.id.chat -> {
                    Toast.makeText(baseContext, "Non ancora implementato!", Toast.LENGTH_SHORT).show()
                }

                R.id.updateDataset -> {
                    Toast.makeText(baseContext, "Non ancora implementato!", Toast.LENGTH_SHORT).show()
                    mainLayout.closeDrawer(navigationView)
                }

            }
            true
        }

        updateUI(navigationView)
    }



    fun updateUI(
        navigationView: NavigationView
    ){
        val isLoggedIn = currentUser != null

        val menu = navigationView.menu

        menu.findItem(R.id.login)?.isVisible = !isLoggedIn

        var elements = arrayOf("home","logout","userpage","chat","updateDataset")

        /*
        for(element in elements){
            val str = "R.id.${element}"
            menu.findItem(str.toInt())?.isVisible = isLoggedIn
        }*/

        menu.findItem(R.id.home)?.isVisible = isLoggedIn
        menu.findItem(R.id.logout)?.isVisible = isLoggedIn
        menu.findItem(R.id.userpage)?.isVisible = isLoggedIn
        menu.findItem(R.id.chat)?.isVisible = isLoggedIn
        menu.findItem(R.id.updateDataset)?.isVisible = isLoggedIn

        //if(!isLoggedIn)setupFragment(RecyclerViewFragment(), Bundle())

        this.recreate()
    }




    val taskListener = object: StartNewRecycler{
        override fun onStartNewRecylcerView(data: Bundle){
            var fragment = TasksViewFragment()

            if(data != null){
                data.putSerializable("subtask_interface", subtaskListener)
                fragment.arguments = data
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit()

        }
    }

    val subtaskListener = object: StartNewRecycler{
        override fun onStartNewRecylcerView(data: Bundle){
            var fragment = SubtasksViewFragment()

            if(data != null){
                fragment.arguments = data
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit()

        }
    }

}
