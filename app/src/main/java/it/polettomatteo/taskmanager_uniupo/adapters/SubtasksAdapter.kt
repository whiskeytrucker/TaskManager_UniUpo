package it.polettomatteo.taskmanager_uniupo.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.dataclass.Subtask
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SubtasksAdapter(private var dataSet: ArrayList<Subtask>): RecyclerView.Adapter<SubtasksAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val state: TextView
        val checkbox: CheckBox
        val subDescr: TextView
        val priority: TextView
        val expiring: TextView
        val progress: TextView
        val seekBar: SeekBar

        init{
            state = view.findViewById(R.id.state)
            checkbox = view.findViewById(R.id.checkBox)
            subDescr = view.findViewById(R.id.subDescr)
            priority = view.findViewById(R.id.priority)
            expiring = view.findViewById(R.id.expiringSubtask)
            progress = view.findViewById(R.id.progressSubtask)
            seekBar = view.findViewById(R.id.seekBar)

            seekBar.isEnabled = false
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


        if(dataSet[position].stato.compareTo("assigned") != 0){
            holder.progress.visibility = View.GONE
            holder.seekBar.visibility = View.GONE
            holder.checkbox.visibility = View.GONE
        }else if(dataSet[position].stato.compareTo("completed") != 0){
            holder.checkbox.isChecked = true
            holder.expiring.visibility = View.GONE
            holder.priority.visibility = View.GONE
            holder.progress.visibility = View.GONE
            holder.seekBar.visibility = View.GONE
        }else {
            holder.progress.text = "${dataSet[position].progress}%"
            holder.seekBar.progress = dataSet[position].progress
            holder.checkbox.isChecked = false
        }




    }



    override fun getItemCount() = dataSet.size



    private fun formatTimestamp(timestamp: Date): String{
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(timestamp)
    }
}