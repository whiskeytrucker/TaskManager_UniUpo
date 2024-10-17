package it.polettomatteo.taskmanager_uniupo.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.dataclass.Notification
import it.polettomatteo.taskmanager_uniupo.firebase.ChatDB
import it.polettomatteo.taskmanager_uniupo.firebase.ProjectsDB
import it.polettomatteo.taskmanager_uniupo.firebase.TasksDB
import it.polettomatteo.taskmanager_uniupo.firebase.UsersDB
import it.polettomatteo.taskmanager_uniupo.fragments.ProjectsViewFragment
import it.polettomatteo.taskmanager_uniupo.fragments.SubtasksViewFragment
import it.polettomatteo.taskmanager_uniupo.fragments.ChatFragment
import it.polettomatteo.taskmanager_uniupo.fragments.TasksViewFragment
import it.polettomatteo.taskmanager_uniupo.fragments.UserPageFragment
import it.polettomatteo.taskmanager_uniupo.interfaces.StartNewRecycler
import java.util.Random

class MainActivity : AppCompatActivity(){
    private var notifManager: NotificationManager? = null
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

        notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        initToolbar(toolbar)
    }

    override fun onStart(){
        super.onStart()
        if(currentUser != null){
            getNotifications{ notifications ->
                if(notifications != null){
                    for(notif in notifications)
                        createNotification(notif)
                }
            }
        }




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



    private fun setupFragment(fragment: Fragment, bundle: Bundle? = null, fromNav: Boolean = false) {
        if(bundle != null){
            if(!fromNav)bundle.putSerializable("task_interface",  taskListener)
            fragment.arguments = bundle
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .addToBackStack(null)
            .commit()
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

                R.id.register -> {
                    val intent = Intent(this, RegisterActivity::class.java)
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
                        setupFragment(UserPageFragment(), bundle, true)
                    }
                }

                R.id.chat -> {
                    if(currentUser != null){
                        currentUser?.email?.let {
                            ChatDB.getReceivers(it){ bundle ->
                                if(bundle != null){
                                    mainLayout.closeDrawer(navigationView)
                                    setupFragment(ChatFragment(), bundle, true)
                                }else{
                                    Toast.makeText(baseContext, "Nun ce stanno messaggi cumpà", Toast.LENGTH_SHORT).show()
                                }

                            }
                        }
                    }

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
        menu.findItem(R.id.register)?.isVisible = !isLoggedIn

        menu.findItem(R.id.home2)?.isVisible = isLoggedIn
        menu.findItem(R.id.logout)?.isVisible = isLoggedIn
        menu.findItem(R.id.userpage)?.isVisible = isLoggedIn
        menu.findItem(R.id.chat)?.isVisible = isLoggedIn

        this.recreate()
    }




    val taskListener = object: StartNewRecycler{
        override fun onStartNewRecylcerView(data: Bundle){
            var fragment = TasksViewFragment()

            data.putSerializable("tipo", userType)
            data.putSerializable("subtask_interface", subtaskListener)
            fragment.arguments = data

            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .addToBackStack(null)
                .commit()

        }
    }

    val subtaskListener = object: StartNewRecycler{
        override fun onStartNewRecylcerView(data: Bundle){
            val fragment = SubtasksViewFragment()

            data.putSerializable("tipo", userType)
            fragment.arguments = data

            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit()

        }
    }


    private fun getNotifications(callback: (ArrayList<Notification>?) -> Unit){
        val db = FirebaseFirestore.getInstance()

        val qry = db.collection("notifiche")
            .whereEqualTo("user", currentUser?.email)
            .limit(1)

            qry.get()
            .addOnSuccessListener { docs ->
                for(doc in docs){
                    db.collection("notifiche")
                        .document(doc.id)
                        .collection("unseen")
                        .get()
                        .addOnSuccessListener { result ->
                            val nots = ArrayList<Notification>()
                            for(docUS in result){
                                val data = docUS.data
                                val tmp = Notification(
                                    docUS.id,
                                    data["title"].toString(),
                                    data["descr"].toString(),
                                    data["channel"].toString(),
                                    data["channelTitle"].toString(),
                                    data["channelDescr"].toString()
                                )
                                nots.add(tmp)
                            }

                            callback(nots)
                        }
                }
            }
    }



    private fun createNotification(notifi: Notification) {
        val intent1 = Intent(this, DeleteNotification::class.java)
        intent1.putExtra("notification_id", notifi.id)

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent1,
            PendingIntent.FLAG_IMMUTABLE
        )


        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(notifi.channelID, notifi.channelTitle, importance)
        channel.description = notifi.channelDescr
        notifManager?.getNotificationChannel(channel.id)
            ?: notifManager?.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this@MainActivity, channel.id)
            .setContentTitle(notifi.title)
            .setContentText(notifi.descr)
            .setSmallIcon(R.drawable.ic_notification)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notifi.title))
            .setChannelId(channel.id)
            .setDeleteIntent(pendingIntent)
            .build()
        notifManager?.notify(Random(1000000).nextInt(), notification)
    }


}
