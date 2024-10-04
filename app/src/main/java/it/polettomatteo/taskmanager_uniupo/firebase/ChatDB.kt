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

        fun getOldMesssages(userMail: String, callback: (Bundle?) -> Unit) {
            val db = FirebaseFirestore
            .getInstance()


            db.collection("chat")
                .whereEqualTo("user0", userMail)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if(!querySnapshot.isEmpty){
                        val docs = querySnapshot.documents

                        for(doc in docs){
                            if(doc.data != null){
                                db.collection("chat")
                                    .document(doc.id)
                                    .collection("messages")
                                    .orderBy("timestamp", Query.Direction.ASCENDING)
                                    .get()
                                    .addOnSuccessListener { result ->
                                        val documents = result.documents
                                        val bundle = Bundle()

                                        for ((i, docIn) in documents.withIndex()) {
                                            val data = docIn.data
                                            if(data != null){
                                                var sender = ""
                                                if(data["sender"] == true){
                                                    sender = doc.data?.get("user0").toString()
                                                }else{
                                                    sender = doc.data?.get("user1").toString()
                                                }


                                                val tmp = Message(
                                                    docIn.id,
                                                    sender,
                                                    data["text"].toString(),
                                                    data["timestamp"] as Timestamp
                                                )
                                                bundle.putSerializable(i.toString(), tmp)
                                            }
                                        }
                                        callback(bundle)
                                    }

                                    .addOnFailureListener{
                                        it.printStackTrace()
                                        callback(null)
                                    }
                            }
                        }
                    }
                }
                .addOnFailureListener{
                    it.printStackTrace()
                    callback(null)
                }
        }
    }
}






