package it.polettomatteo.taskmanager_uniupo.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.dataclass.Project
import it.polettomatteo.taskmanager_uniupo.firebase.NotificationDB
import it.polettomatteo.taskmanager_uniupo.firebase.TasksDB
import it.polettomatteo.taskmanager_uniupo.fragments.TasksViewFragment
import it.polettomatteo.taskmanager_uniupo.interfaces.StartNewRecycler


class ProjectsAdapter(private val userType: String, private val context: Context, private var dataSet: ArrayList<Project>, private val listener: StartNewRecycler) : RecyclerView.Adapter<ProjectsAdapter.ViewHolder>(){



    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView
        val content: TextView
        val author: TextView
        val assigned: TextView
        val progress: TextView
        val seekBar: SeekBar
        val promptPL: Button


        init {
            title = view.findViewById(R.id.title)
            content = view.findViewById(R.id.content)
            seekBar = view.findViewById(R.id.seekBar)
            progress = view.findViewById(R.id.progress)
            assigned = view.findViewById(R.id.assigned)
            author = view.findViewById(R.id.authorProject)

            promptPL = view.findViewById(R.id.promptPL)

            seekBar.isEnabled = false
            seekBar.progress = 0

            val perc = seekBar.progress

        }
    }

    private var filteredList: MutableList<Project> = dataSet.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // crea una nuova view, dove definisce la ui dell'elemento della lista
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.project_item, parent, false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var id = filteredList[position].id
        var auth = FirebaseAuth.getInstance()
        var currentUser = auth.currentUser

        // prendi l'elemento dal dataset e rimpazza i contenuti
        holder.title.text = filteredList[position].titolo
        holder.content.text = filteredList[position].descr

        if (currentUser != null) {
            if(currentUser.email?.compareTo(filteredList[position].assigned) == 0) {
                holder.assigned.visibility = View.GONE
                holder.author.text = "Project Manager: ${filteredList[position].autore}"
            }else{
                holder.author.visibility = View.GONE
                holder.assigned.text = "Project Leader: ${filteredList[position].assigned}"
            }
        }
        holder.progress.text = "${filteredList[position].progress}%"
        holder.seekBar.progress = filteredList[position].progress

        if(userType.compareTo("pm") == 0){
            holder.promptPL.visibility = View.VISIBLE
        }

        holder.promptPL.setOnClickListener {
            NotificationDB.promptUser(filteredList[position].assigned, filteredList[position].titolo){ result ->
                if(result == true) Toast.makeText(context, "Notifica inviata!", Toast.LENGTH_SHORT).show()
                else Toast.makeText(context, "Errore nel mandare la notifica", Toast.LENGTH_SHORT).show()
            }
        }

        holder.itemView.setOnClickListener {
            TasksDB.getTasks(id){bundle ->
                if(bundle != null){
                    dataSet.clear()
                    listener.onStartNewRecyclerView(bundle)
                }

            }

        }
    }

    fun filter(query: String){
        filteredList = if (query.isEmpty()){
            dataSet.toMutableList()
        }else{
            dataSet.toMutableList()
        }
        notifyDataSetChanged()
    }

    fun resetFilters(){
        filteredList = dataSet.toMutableList()
        notifyDataSetChanged()
    }



    override fun getItemCount() = filteredList.size
}
