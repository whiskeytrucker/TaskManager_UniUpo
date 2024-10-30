package it.polettomatteo.taskmanager_uniupo.firebase

import android.os.Bundle
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import it.polettomatteo.taskmanager_uniupo.dataclass.Message

class ChatDB {
    companion object{
        fun sendMessage(messageText: String, sender: String, fieldUser: String, callback: (Message?) -> Unit){
            val db = FirebaseFirestore
                    .getInstance()

            var fieldToSend = ""
            var senderBool = true

            if(fieldUser.compareTo("user0") == 0)fieldToSend = "user1"
            else if(fieldUser.compareTo("user1") == 0){fieldToSend = "user0"; senderBool = false}
            else callback(null)

            val msg = hashMapOf(
                "sender" to senderBool,
                "text" to messageText,
                "timestamp" to Timestamp.now()
            )

            val currentUser = FirebaseAuth.getInstance().currentUser


            if(currentUser != null){
                db.collection("chat")
                    .whereEqualTo(fieldUser, currentUser.email)
                    .whereEqualTo(fieldToSend, sender)
                    .get()
                    .addOnSuccessListener { docs ->
                        for (doc in docs.documents) {
                            db.collection("chat")
                                .document(doc.id)
                                .collection("messages")
                                .document()
                                .set(msg)
                                .addOnSuccessListener {
                                    val tmp = Message(
                                        senderBool,
                                        messageText,
                                        Timestamp.now()
                                    )

                                    callback(tmp)
                                }
                                .addOnFailureListener { callback(null) }
                        }
                    }
                    .addOnFailureListener { it.printStackTrace(); callback(null) }
            }

        }

        fun getReceivers(fieldUser: String, userMail: String, callback: (Bundle?) -> Unit) {
            val db = FirebaseFirestore
            .getInstance()

            var fieldToGet = ""

            if(fieldUser.compareTo("user0") == 0)fieldToGet = "user1"
            else if(fieldUser.compareTo("user1") == 0)fieldToGet = "user0"
            else callback(null)

            db.collection("chat")
                .whereEqualTo(fieldUser, userMail)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if(!querySnapshot.isEmpty){
                        val docs = querySnapshot.documents
                        val receivers = Bundle()

                        for((i, doc) in docs.withIndex()){
                            val data = doc.data
                            if(data != null){
                                receivers.putString(i.toString(), data[fieldToGet].toString())}
                        }

                        receivers.putString("field", fieldUser)
                        callback(receivers)
                    }else{
                        getReceivers("user1", userMail){ rec ->
                            callback(rec)
                        }
                    }
                }

                .addOnFailureListener{
                    it.printStackTrace()
                    callback(null)
                }
        }



        fun getOldMessages(fieldUser: String, user0: String, user1: String, callback: (Bundle?) -> Unit){
            val db = FirebaseFirestore
                .getInstance()

            var fieldToGet = ""

            if(fieldUser.compareTo("user0") == 0)fieldToGet = "user1"
            else if(fieldUser.compareTo("user1") == 0)fieldToGet = "user0"

            db.collection("chat")
                .whereEqualTo(fieldUser, user0)
                .whereEqualTo(fieldToGet, user1)
                .get()
                .addOnSuccessListener { docs ->
                    for(doc in docs.documents){
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
                                        var sender = data["sender"].toString().toBoolean()
                                        if(fieldUser.compareTo("user0") != 0)sender = !sender

                                        val tmp = Message(
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
                .addOnFailureListener {
                    it.printStackTrace()
                    callback(null)
                }
        }
    }
}









