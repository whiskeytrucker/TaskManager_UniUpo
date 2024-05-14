package it.polettomatteo.taskmanager_uniupo.adapters


import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.dataclass.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.reflect.typeOf


val TAG = "TasksAdapter"
class TasksAdapter(private var dataSet: ArrayList<Task>) : RecyclerView.Adapter<TasksAdapter.ViewHolder>(){


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView
        val descr: TextView
        val dev: TextView
        val expiring: TextView
        val progressTask: TextView
        val seekBar: SeekBar


        init {
            nome = view.findViewById(R.id.name)
            descr = view.findViewById(R.id.descr)
            dev = view.findViewById(R.id.dev)
            expiring = view.findViewById(R.id.expiring)
            seekBar = view.findViewById(R.id.seekBar)
            progressTask = view.findViewById(R.id.progressTask)

            seekBar.isEnabled = false
            seekBar.progress = 0

            val perc = seekBar.progress

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // crea una nuova view, dove definisce la ui dell'elemento della lista
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item, parent, false)

        return ViewHolder(view)
    }



    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val sec = dataSet[position].expire.seconds
        val ns = dataSet[position].expire.nanoseconds

        val ms = sec * 1000 + ns /  1000000


        // prendi l'elemento dal dataset e rimpazza i contenuti
        holder.nome.text = dataSet[position].nome
        holder.descr.text = dataSet[position].descr
        holder.dev.text = "Developer: ${dataSet[position].dev}"
        holder.expiring.text = "Scadenza: ${formatTimestamp(Date(ms))}"
        holder.progressTask.text = "${dataSet[position].progress}%"
        holder.seekBar.progress = dataSet[position].progress

        val perc = dataSet[position].progress


        /*
        val view = holder.itemView

        holder.itemView.setOnClickListener {view.setOnClickListener {
            Log.d(TAG, "Hai premuto la View, dio canaglia")
            //
        }}*/
    }



    override fun getItemCount() = dataSet.size



    fun formatTimestamp(timestamp: Date): String{
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(timestamp)
    }

}