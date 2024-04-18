package it.polettomatteo.taskmanager_uniupo.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.dataclass.Project

class ProjectsAdapter(private var dataSet: ArrayList<Project>) : RecyclerView.Adapter<ProjectsAdapter.ViewHolder>(){


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView
        val content: TextView
        val assigned: TextView
        val progress: TextView
        val seekBar: SeekBar

        init {
            title = view.findViewById(R.id.title)
            content = view.findViewById(R.id.content)
            seekBar = view.findViewById(R.id.seekBar)
            progress = view.findViewById(R.id.progress)
            assigned = view.findViewById(R.id.assigned)

            seekBar.isEnabled = false
            seekBar.progress = 0

            val perc = seekBar.progress

            progress.offsetLeftAndRight(perc)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // crea una nuova view, dove definisce la ui dell'elemento della lista
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_row_item, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // prendi l'elemento dal dataset e rimpazza i contenuti
        holder.title.text = dataSet[position].titolo
        holder.content.text = dataSet[position].descr
        holder.assigned.text = "Assegnato a: ${dataSet[position].assigned}"
        holder.progress.text = "${dataSet[position].progress}%"
        holder.seekBar.progress = dataSet[position].progress

    }



    override fun getItemCount() = dataSet.size

}