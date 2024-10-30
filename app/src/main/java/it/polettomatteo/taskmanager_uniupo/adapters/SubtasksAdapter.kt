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

class SubtasksAdapter(private val userType: String, private val context: Context, private var dataSet: ArrayList<Subtask>, private var modListener: TempActivity, private var commentListener: TempActivity): RecyclerView.Adapter<SubtasksAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val state: TextView = view.findViewById(R.id.state)
        val subDescr: TextView = view.findViewById(R.id.subDescr)
        val priority: TextView = view.findViewById(R.id.priority)
        val expiring: TextView = view.findViewById(R.id.expiringSubtask)
        val progress: TextView = view.findViewById(R.id.progressSubtask)
        val seekBar: SeekBar = view.findViewById(R.id.seekBar)

        val modifyBtn: Button = view.findViewById(R.id.modifySubtask)
        val deleteBtn: Button = view.findViewById(R.id.deleteSubtask)
        val commentBtn: Button = view.findViewById(R.id.viewSubComment)

        init{
            seekBar.isEnabled = false
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

        var subpriority = ""
        var subState = ""


        subpriority = when(dataSet[position].priorita){
            0 -> "Nessuna"
            1 -> "Bassa"
            2 -> "Media"
            3 -> "Alta"
            4 -> "URGENTE"
            else -> {
                "Errore"
            }
        }

        when(dataSet[position].stato){
            1 -> subState = "TODO"
            2 -> subState = "Assigned"
            3 -> subState = "Completed"
            else -> {
                subpriority = "Errore"
            }
        }


        holder.state.text = "Stato: $subState"
        holder.subDescr.text = "\"${dataSet[position].subDescr}\""
        holder.priority.text = "Priorita': $subpriority"
        holder.expiring.text = "Scadenza: ${formatTimestamp(Date(ms))}"


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

                tmp.putString("subDescr", dataSet[position].subDescr)
                tmp.putInt("priorita", dataSet[position].priorita)
                tmp.putInt("stato", dataSet[position].stato)
                tmp.putLong("expiring", ms)
                tmp.putInt("progress", dataSet[position].progress)

                modListener.onStartNewTempActivity(tmp)
            }

            holder.deleteBtn.setOnClickListener{
                SubtasksDB.deleteSubtask(dataSet[position].idPrg, dataSet[position].idTask, dataSet[position].id){ result ->
                    if(result == true){
                        Toast.makeText(context, "Task cancellata correttamente!", Toast.LENGTH_SHORT).show()
                        dataSet.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, dataSet.size)
                    }else{
                        Toast.makeText(context, "Errore nella cancellazione!", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }

        holder.commentBtn.setOnClickListener{
            SubtasksDB.getComments(dataSet[position].id){ result ->
                if(result != null){
                    commentListener.onStartNewTempActivity(result)
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