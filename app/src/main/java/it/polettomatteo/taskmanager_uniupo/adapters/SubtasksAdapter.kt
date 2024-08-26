package it.polettomatteo.taskmanager_uniupo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
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
import it.polettomatteo.taskmanager_uniupo.firebase.SubtasksDB
import it.polettomatteo.taskmanager_uniupo.firebase.TasksDB
import it.polettomatteo.taskmanager_uniupo.interfaces.TempActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SubtasksAdapter(private val userType: String, private val context: Context, private var dataSet: ArrayList<Subtask>, private var listener: TempActivity): RecyclerView.Adapter<SubtasksAdapter.ViewHolder>() {

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

        var subpriority:String = ""
        var subState: String = ""

        if(dataSet[position].priorita <= 1) subpriority = "Bassa"
        else if(dataSet[position].priorita == 2) subpriority = "Media"
        else if(dataSet[position].priorita >= 3) subpriority = "Alta"

        if(dataSet[position].stato <= 1) subState = "TODO"
        else if(dataSet[position].stato == 2) subState = "Assigned"
        else if(dataSet[position].stato >= 3) subState = "Completed"

        holder.state.text = "Stato: ${subState}"
        holder.subDescr.text = "\"${dataSet[position].subDescr}\""
        holder.priority.text = "Priorita': ${subpriority}"
        holder.expiring.text = "Scadenza: ${formatTimestamp(Date(ms))}"
        holder.seekBar.isEnabled = false

        Log.d("SubtasksAdapter", "Subtask: " + dataSet[position].subDescr + "\tStato: " + dataSet[position].stato)

        // Riga 79 per vedere gli stati scritti bene
        if(dataSet[position].stato == 2){
            holder.progress.text = "${dataSet[position].progress}%"
            holder.seekBar.progress = dataSet[position].progress
        }else if(dataSet[position].stato >= 3){
            holder.priority.visibility = View.GONE
            holder.progress.visibility = View.GONE
            holder.seekBar.visibility = View.GONE
        }

        if((userType.compareTo("d") == 0 || userType.compareTo("pl") == 0)){
            if(dataSet[position].stato <= 2)holder.modifyBtn.visibility = View.VISIBLE
            holder.deleteBtn.visibility = View.VISIBLE

            holder.modifyBtn.setOnClickListener{
                val tmp = Bundle()

                tmp.putString("id", dataSet[position].id)
                tmp.putString("idPrg", dataSet[position].idPrg)
                tmp.putString("idTask", dataSet[position].idTask)

                tmp.putString("subDescr", dataSet[position].subDescr)
                tmp.putInt("priorita", dataSet[position].priorita)
                tmp.putInt("stato", dataSet[position].stato)
                tmp.putLong("expiring", ms)
                tmp.putInt("progress", dataSet[position].progress)

                listener.onStartNewTempActivity(Bundle())
            }

            holder.deleteBtn.setOnClickListener{
                SubtasksDB.deleteSubtask(dataSet[position].idPrg, dataSet[position].idTask, dataSet[position].id){ result ->
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



    }



    override fun getItemCount() = dataSet.size



    private fun formatTimestamp(timestamp: Date): String{
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(timestamp)
    }
}