@file:Suppress("OverrideDeprecatedMigration")

package it.polettomatteo.taskmanager_uniupo.fragments

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.activities.ResultActivity
import it.polettomatteo.taskmanager_uniupo.adapters.ProjectsAdapter
import it.polettomatteo.taskmanager_uniupo.dataclass.Project
import it.polettomatteo.taskmanager_uniupo.interfaces.StartNewRecycler

class ProjectsViewFragment: Fragment() {
    private var savedBundle: Bundle? = null
    private lateinit var notificationManager: NotificationManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var tmpBut: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.recycler_projectview, container, false)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = view.findViewById(R.id.recyclerView)

        val tmp = ArrayList<Project>()
        val bundle: Bundle?


        if(savedBundle != null && this.arguments == null){bundle = savedBundle}
        else{bundle = this.arguments}


        var listener: StartNewRecycler? = null
        if(bundle != null){
            if(bundle.getSerializable("task_interface") != null){
                listener = bundle.getSerializable("task_interface") as StartNewRecycler
                bundle.remove("task_interface")
            }


            for(key in bundle.keySet()){tmp.add(bundle.getSerializable(key) as Project)}
        }

        tmpBut = view.findViewById(R.id.testNot)
        this.notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotifChannel(
            "it.camporapoletto.tempNot",
            "Diego News",
            "Tutte le notifiche da parte di DIEGO"
        )


        val customAdapter =
            listener?.let { ProjectsAdapter(tmp, it) } // <-- Da cambiare con i dati presi da savedInstanceState
        recyclerView.adapter = customAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)

        return view
    }

    override fun onStart() {
        super.onStart()

        tmpBut.setOnClickListener {
            sendNotification()
        }
    }

    override fun onPause() {
        super.onPause()
        savedBundle = this.arguments
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                Log.d("TasksViewFragment", parentFragmentManager.toString())

                parentFragmentManager.popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }






    fun createNotifChannel(
        id: String,
        channelName: String,
        channelDescription: String,
    ) {
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(id, channelName, importance)
        channel.description = channelDescription
        this.notificationManager.getNotificationChannel(channel.id)
            ?: this.notificationManager.createNotificationChannel(channel)
    }

    fun sendNotification(){
        val notificationID = 123
        val resultIntent = Intent(requireContext(), ResultActivity::class.java)

        val pending = PendingIntent.getActivity(
            requireContext(),
            0,
            resultIntent,
            PendingIntent.FLAG_IMMUTABLE
        )


        val channelID = "it.camporapoletto.tempNot"
        val not = Notification.Builder(requireContext(), channelID)
            .setContentTitle("Esempio Notifica")
            .setContentText("Questo è un esempio di Notifica, Si Diego, Vai Diego")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setChannelId(channelID)
            .setContentIntent(pending)
            .build()

        notificationManager.notify(notificationID, not)
    }

}