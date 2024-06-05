package it.polettomatteo.taskmanager_uniupo.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.activities.SingleChatActivity
import it.polettomatteo.taskmanager_uniupo.dataclass.Chat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatsAdapter(private var mList: ArrayList<Chat>, private val context: Context) : RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sender: TextView = view.findViewById(R.id.sender)
        val lastMessage: TextView = view.findViewById(R.id.lastMessage)
        val lastMessageDate: TextView = view.findViewById(R.id.lastMessageDate)
    }






    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_chat_view, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]

        holder.sender.text = item.from
        holder.lastMessage.text = item.lastMessage
        //holder.lastMessageDate.text = formatTimestamp(item.lastMessageDate)

        holder.itemView.setOnClickListener{
            val intent = Intent(context, SingleChatActivity::class.java)
            intent.putExtra("suca", Bundle())
            context.startActivity(intent)
        }
    }

    class DiffCallback(private val oldList: List<Chat>, private val newList: List<Chat>) :
        DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }



    fun updateList(newList: List<Chat>) {
        val diffResult = DiffUtil.calculateDiff(DiffCallback(this.mList, newList))
        this.mList = ArrayList(newList)
        diffResult.dispatchUpdatesTo(this)
    }


    override fun getItemCount(): Int {
        return this.mList.size
    }


    private fun formatTimestamp(timestamp: Date): String{
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(timestamp)
    }

}