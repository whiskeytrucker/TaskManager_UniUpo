package it.polettomatteo.taskmanager_uniupo.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.dataclass.Message

class ChatActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recycler_chat)

        val currentUser = FirebaseAuth.getInstance().currentUser


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