package it.polettomatteo.taskmanager_uniupo.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ChatDB {
    companion object{
        fun sendMessage(chatID: String, messageText: String, sender: String,callback: (Boolean?) -> Unit){
            val db = FirebaseFirestore
                    .getInstance()
            val msg = hashMapOf(
                "mittente" to sender,
                "testo" to messageText,
                "timestamp" to FieldValue.serverTimestamp()
            )

            val currentUser = FirebaseAuth.getInstance().currentUser


            if(currentUser != null){
                db.collection("chat")
                    .document(chatID)
                    .collection("messages")
                    .add(msg)
                    .addOnSuccessListener{
                        callback(true)
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                        callback(false)
                    }
            }

        }
    }
}