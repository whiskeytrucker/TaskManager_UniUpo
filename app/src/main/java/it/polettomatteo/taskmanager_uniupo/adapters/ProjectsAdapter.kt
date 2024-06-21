package it.polettomatteo.taskmanager_uniupo.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.dataclass.Project
import it.polettomatteo.taskmanager_uniupo.firebase.TasksDB
import it.polettomatteo.taskmanager_uniupo.fragments.TasksViewFragment
import it.polettomatteo.taskmanager_uniupo.interfaces.StartNewRecycler


class ProjectsAdapter(private var dataSet: ArrayList<Project>, private val listener: StartNewRecycler) : RecyclerView.Adapter<ProjectsAdapter.ViewHolder>(){


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView
        val content: TextView
        val author: TextView
        val assigned: TextView
        val progress: TextView
        val seekBar: SeekBar


        init {
            title = view.findViewById(R.id.title)
            content = view.findViewById(R.id.content)
            seekBar = view.findViewById(R.id.seekBar)
            progress = view.findViewById(R.id.progress)
            assigned = view.findViewById(R.id.assigned)
            author = view.findViewById(R.id.authorProject)

            seekBar.isEnabled = false
            seekBar.progress = 0

            val perc = seekBar.progress

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // crea una nuova view, dove definisce la ui dell'elemento della lista
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.project_item, parent, false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var id = dataSet[position].id
        var auth = FirebaseAuth.getInstance()
        var currentUser = auth.currentUser

        // prendi l'elemento dal dataset e rimpazza i contenuti
        holder.title.text = dataSet[position].titolo
        holder.content.text = dataSet[position].descr

        if (currentUser != null) {
            if(currentUser.email?.compareTo(dataSet[position].assigned) == 0) {
                holder.assigned.visibility = View.GONE
                holder.author.text = "Project Manager: ${dataSet[position].autore}"
            }else{
                holder.author.visibility = View.GONE
                holder.assigned.text = "Project Leader: ${dataSet[position].assigned}"
            }
        }
        holder.progress.text = "${dataSet[position].progress}%"
        holder.seekBar.progress = dataSet[position].progress

        val perc = dataSet[position].progress

        val view = holder.itemView

        holder.itemView.setOnClickListener {
            TasksDB.getTasks(id){bundle ->
                if(bundle != null){
                    dataSet.clear()
                    listener.onStartNewRecylcerView(bundle)
                }

            }

        }
    }



    override fun getItemCount() = dataSet.size

}