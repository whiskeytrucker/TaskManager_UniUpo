package it.polettomatteo.taskmanager_uniupo.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.adapters.ChatAdapter
import it.polettomatteo.taskmanager_uniupo.dataclass.Message

class ChatFragment: Fragment() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.recycler_chat, container, false)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)



        val chatArr = ArrayList<Message>()

        val bundle = this.arguments

        if(bundle != null){
            for(key in bundle.keySet()){
                chatArr.add(bundle.getSerializable(key) as Message)
            }
        }

        this.recyclerView = view.findViewById(R.id.recyclerViewChat)

        val chatAdapter = ChatAdapter(chatArr)

        this.recyclerView.adapter = chatAdapter
        this.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)

        return view
    }


    fun listenForMessages(chatId: String) {
        FirebaseFirestore.getInstance()
            .collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    println("Errore nella lettura dei messaggi: $e")
                    return@addSnapshotListener
                }

                for (document in snapshots!!.documentChanges) {
                    if (document.type == DocumentChange.Type.ADDED) {
                        val message = document.document.toObject(Message::class.java)
                        println("Nuovo messaggio: ${message.text}")
                    }
                }
            }
    }

}