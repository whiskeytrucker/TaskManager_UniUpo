package it.polettomatteo.taskmanager_uniupo.firebase

import android.os.Bundle
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import it.polettomatteo.taskmanager_uniupo.dataclass.Message

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

        fun getOldMessages(chatID: String, callback: (Bundle?) -> Unit) {
            val db = FirebaseFirestore
            .getInstance()


            db.collection("chat")
                .document(chatID)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { result ->
                    val documents = result.documents
                    val bundle = Bundle()

                    for ((i, doc) in documents.withIndex()) {
                        val data = doc.data
                        if(data != null){
                            val tmp = Message(
                                doc.id,
                                data["sender"].toString(),
                                data["text"].toString(),
                                data["timestamp"] as Timestamp
                            )
                            bundle.putSerializable(i.toString(), tmp)
                        }
                    }

                    callback(bundle)
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    callback(null)
                }
        }
    }
}