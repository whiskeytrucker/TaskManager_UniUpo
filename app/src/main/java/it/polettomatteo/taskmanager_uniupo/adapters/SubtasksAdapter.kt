package it.polettomatteo.taskmanager_uniupo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.dataclass.Subtask
import it.polettomatteo.taskmanager_uniupo.firebase.TasksDB
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SubtasksAdapter(private val context: Context, private var dataSet: ArrayList<Subtask>): RecyclerView.Adapter<SubtasksAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val state: TextView
        val subDescr: TextView
        val priority: TextView
        val expiring: TextView
        val progress: TextView
        val seekBar: SeekBar
        val modifyBtn: Button
        val deleteBtn: Button

        init{
            state = view.findViewById(R.id.state)
            subDescr = view.findViewById(R.id.subDescr)
            priority = view.findViewById(R.id.priority)
            expiring = view.findViewById(R.id.expiringSubtask)
            progress = view.findViewById(R.id.progressSubtask)
            seekBar = view.findViewById(R.id.seekBar)

            modifyBtn = view.findViewById(R.id.modifySubtask)
            deleteBtn = view.findViewById(R.id.deleteSubtask)

            seekBar.isEnabled = true
            seekBar.progress = 0


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.subtask_item, parent, false)

        return ViewHolder(view)
    }



    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sec = dataSet[position].scadenza.seconds
        val ns = dataSet[position].scadenza.nanoseconds

        val ms = sec * 1000 + ns /  1000000

        holder.state.text = "Stato: ${dataSet[position].stato}"
        holder.subDescr.text = "\"${dataSet[position].subDescr}\""
        holder.priority.text = "Priorita': ${dataSet[position].priorita}"
        holder.expiring.text = "Scadenza: ${formatTimestamp(Date(ms))}"


        if(dataSet[position].stato.compareTo("assigned") == 0){
            holder.progress.text = "${dataSet[position].progress}%"
            holder.seekBar.progress = dataSet[position].progress
        }else if(dataSet[position].stato.compareTo("completed") == 0){
            holder.priority.visibility = View.GONE
            holder.progress.visibility = View.GONE
            holder.seekBar.visibility = View.GONE
        }else if(dataSet[position].stato.compareTo("todo") == 0){
            holder.seekBar.isEnabled = false
        }



        holder.modifyBtn.setOnClickListener{
            Toast.makeText(context, "NON ANCORA IMPLEMENTATO", Toast.LENGTH_SHORT).show()
        }

        holder.deleteBtn.setOnClickListener{
            Toast.makeText(context, "NON ANCORA IMPLEMENTATO", Toast.LENGTH_SHORT).show()
        }

    }



    override fun getItemCount() = dataSet.size



    private fun formatTimestamp(timestamp: Date): String{
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(timestamp)
    }
}