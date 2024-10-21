@file:Suppress("OverrideDeprecatedMigration")

package it.polettomatteo.taskmanager_uniupo.fragments

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
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
    private lateinit var savedAdapter: ProjectsAdapter
    private var notificationManager: NotificationManager? = null
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("ProjectViewFragment", "onCreateView()")
        val view = inflater.inflate(R.layout.recycler_projectview, container, false)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = view.findViewById(R.id.recyclerView)

        val tmp = ArrayList<Project>()
        val bundle: Bundle?
        var userType: String = ""


        if(savedBundle != null && this.arguments == null){bundle = savedBundle}
        else{bundle = this.arguments}


        var listener: StartNewRecycler? = null
        if(bundle != null){
            for(key in bundle.keySet()){
                if(key.compareTo("tipo") == 0){
                    userType = bundle.getString("tipo")!!
                }else if(key.compareTo("task_interface") == 0){
                    listener = bundle.getSerializable("task_interface") as StartNewRecycler
                }else {
                    tmp.add(bundle.getSerializable(key) as Project)
                }
            }
        }

        notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val projectsAdapter =
            listener?.let { context?.let { it1 -> ProjectsAdapter(userType, it1, tmp, it) } }!!
        recyclerView.adapter = projectsAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)

        savedAdapter = projectsAdapter

        return view
    }

    override fun onPause() {
        Log.d("ProjectViewFragment", "onPause()")
        super.onPause()
        savedBundle = this.arguments
        savedAdapter = recyclerView.adapter as ProjectsAdapter
    }

    override fun onResume() {
        Log.d("ProjectViewFragment", "onResume()")
        super.onResume()
        recyclerView.adapter = savedAdapter
    }

}