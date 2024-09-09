package it.polettomatteo.taskmanager_uniupo.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
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
import it.polettomatteo.taskmanager_uniupo.dataclass.Project
import it.polettomatteo.taskmanager_uniupo.firebase.ProjectsDB
import it.polettomatteo.taskmanager_uniupo.firebase.ProjectsDB.Companion.fetchPL
import it.polettomatteo.taskmanager_uniupo.firebase.TasksDB
import it.polettomatteo.taskmanager_uniupo.firebase.UsersDB
import it.polettomatteo.taskmanager_uniupo.fragments.ProjectsViewFragment
import it.polettomatteo.taskmanager_uniupo.fragments.SubtasksViewFragment
import it.polettomatteo.taskmanager_uniupo.fragments.TasksViewFragment
import it.polettomatteo.taskmanager_uniupo.fragments.UserPageFragment
import it.polettomatteo.taskmanager_uniupo.interfaces.StartNewRecycler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(){
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    var userType: String = "NA"


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
        this.createFragment()
    }


    private fun createFragment(){
        if(currentUser != null){
            currentUser!!.email?.let {
                UsersDB.getUserType(currentUser!!.email.toString()){ bundle ->
                    if (bundle != null) {
                        userType = bundle.getString("tipo").toString()

                        var user: String = currentUser!!.email.toString()

                        if(userType.compareTo("NA") != 0){
                            if(userType.compareTo("d") == 0){
                                // sono un dev
                                // prendo le task del dev da idProject, faccio partire il fragment
                                ProjectsDB.getIdProjectAsDev(user){idProject ->
                                    if(idProject != null){
                                        TasksDB.getTasksAsDev(idProject, user){budnle ->
                                            if(budnle != null){
                                                taskListener.onStartNewRecylcerView(budnle)
                                            }

                                        }
                                    }


                                }

                            }else{
                                ProjectsDB.getProjects(user, userType) { bundle2 ->
                                    if (bundle2 != null) {
                                        this.setupFragment(ProjectsViewFragment(), bundle2)
                                    }
                                }
                            }



                        }
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
            .addToBackStack(null)
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.ses -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // --------------- FUNZIONI AUSILIARIE ---------------
    private fun initToolbar(toolbar: Toolbar){
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
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
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                }

                R.id.home2 -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }



                R.id.userpage -> {
                    if(currentUser != null) {
                        val bundle = Bundle()
                        bundle.putString("username", currentUser?.email)
                        bundle.putString("tipo", userType)
                        mainLayout.closeDrawer(navigationView)
                        setupFragment(UserPageFragment(), bundle)
                    }
                }

                R.id.chat -> {
                    val intent = Intent(this, ChatActivity::class.java)
                    this.startActivity(intent)
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

        var elements = arrayOf("home","logout","userpage","chat","ses")

        /*
        for(element in elements){
            val str = "R.id.${element}"
            menu.findItem(str.toInt())?.isVisible = isLoggedIn
        }*/

        menu.findItem(R.id.home2)?.isVisible = isLoggedIn
        menu.findItem(R.id.logout)?.isVisible = isLoggedIn
        menu.findItem(R.id.userpage)?.isVisible = isLoggedIn
        menu.findItem(R.id.chat)?.isVisible = isLoggedIn
        menu.findItem(R.id.ses)?.isVisible = isLoggedIn

        //if(!isLoggedIn)setupFragment(RecyclerViewFragment(), Bundle())

        this.recreate()
    }




    val taskListener = object: StartNewRecycler{
        override fun onStartNewRecylcerView(data: Bundle){
            var fragment = TasksViewFragment()

            if(data != null){
                data.putSerializable("tipo", userType)
                data.putSerializable("subtask_interface", subtaskListener)
                fragment.arguments = data
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .addToBackStack(null)
                .commit()

        }
    }

    val subtaskListener = object: StartNewRecycler{
        override fun onStartNewRecylcerView(data: Bundle){
            var fragment = SubtasksViewFragment()

            if(data != null){
                data.putSerializable("tipo", userType)
                fragment.arguments = data
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit()

        }


    }

}
