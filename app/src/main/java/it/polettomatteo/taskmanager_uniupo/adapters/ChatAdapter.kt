package it.polettomatteo.taskmanager_uniupo.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.dataclass.Message
import java.text.SimpleDateFormat
import java.util.Locale

class ChatAdapter(private val dataSet: ArrayList<Message>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.messageText)
        val messageFromText: TextView = view.findViewById(R.id.messageFromText)
        val messageTimestamp: TextView = view.findViewById(R.id.messageTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = dataSet[position]
        holder.messageText.text = message.text
        holder.messageFromText.text = "Da: ${message.sender}"
        holder.messageTimestamp.text = formatTimestamp(message.timestamp)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    private fun formatTimestamp(timestamp: Timestamp): String {
        val sdf = SimpleDateFormat("hh:mm a dd/MM/YYYY ", Locale.getDefault())
        return sdf.format(timestamp.toDate())
    }
}
