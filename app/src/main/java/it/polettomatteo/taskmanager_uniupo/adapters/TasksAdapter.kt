package it.polettomatteo.taskmanager_uniupo.adapters


import android.annotation.SuppressLint
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
import com.google.firebase.Timestamp
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.dataclass.Task
import it.polettomatteo.taskmanager_uniupo.firebase.SubtasksDB
import it.polettomatteo.taskmanager_uniupo.firebase.TasksDB
import it.polettomatteo.taskmanager_uniupo.interfaces.StartNewRecycler
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.reflect.typeOf


val TAG = "TasksAdapter"
class TasksAdapter(private val context: Context, private var dataSet: ArrayList<Task>, private val listener: StartNewRecycler) : RecyclerView.Adapter<TasksAdapter.ViewHolder>(){


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView
        val descr: TextView
        val dev: TextView
        val expiring: TextView
        val progressTask: TextView
        val seekBar: SeekBar
        val modifyBtn: Button
        val deleteBtn: Button

        init {
            nome = view.findViewById(R.id.name)
            descr = view.findViewById(R.id.descr)
            dev = view.findViewById(R.id.dev)
            expiring = view.findViewById(R.id.expiring)
            seekBar = view.findViewById(R.id.seekBar)
            progressTask = view.findViewById(R.id.progressTask)

            modifyBtn = view.findViewById(R.id.modifyTask)
            deleteBtn = view.findViewById(R.id.deleteTask)

            seekBar.isEnabled = false
            seekBar.progress = 0
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


        val idTask = dataSet[position].id
        val idProject = dataSet[position].idPrg

        val view = holder.itemView

        holder.itemView.setOnClickListener {
            SubtasksDB.getSubtasks(idProject, idTask){bundle ->
                if(bundle != null){
                    dataSet.clear()
                    listener.onStartNewRecylcerView(bundle)
                }
            }
        }

        holder.modifyBtn.setOnClickListener{
            Toast.makeText(context, "NON ANCORA IMPLEMENTATO", Toast.LENGTH_SHORT).show()
        }

        holder.deleteBtn.setOnClickListener{
            TasksDB.deleteTask(idProject, idTask){result ->
                if(result == true){
                    Toast.makeText(context, "Task cancellata correttamente!", Toast.LENGTH_SHORT).show()
                    dataSet.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, dataSet.size)
                    notifyDataSetChanged()
                }else{
                    Toast.makeText(context, "Errore nella cancellazione!", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }



    override fun getItemCount() = dataSet.size



    private fun formatTimestamp(timestamp: Date): String{
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(timestamp)
    }

}