package it.polettomatteo.taskmanager_uniupo.firebase

import android.os.Bundle
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import it.polettomatteo.taskmanager_uniupo.dataclass.Chat
import java.util.Date

class UsersDB {
    companion object{
        fun getUserType(username: String, callback: (Bundle?) -> Unit){
            FirebaseFirestore
                .getInstance()
                .collection("tipo_utenti")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener{result ->
                    val bundle = Bundle()
                    for(data in result.documents){
                        bundle.putSerializable("tipo", data["tipo"].toString())
                    }

                    callback(bundle)
                }
                .addOnFailureListener{
                    it.printStackTrace()
                    callback(null)
                }
        }

        fun getUserChat(username: String, callback: (Bundle?) -> Unit){
            FirebaseFirestore
                .getInstance()
                .collection("chat")
                .whereEqualTo("da", username)
                .get()
                .addOnSuccessListener {result ->
                    val bundle = Bundle()
                    val documents = result.documents
                    for((index, doc) in documents.withIndex()){
                        val data = doc.data

                        if(data != null){
                            val tmp = Chat(
                                data["id"].toString(),
                                data["da"].toString(),
                                data["a"].toString(),
                                data["ultimoMsg"].toString(),
                                data["data"] as Timestamp
                            )

                            bundle.putSerializable(index.toString(), tmp)
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
