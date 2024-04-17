package it.polettomatteo.taskmanager_uniupo.firebase

import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import it.polettomatteo.taskmanager_uniupo.dataclass.User

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
    }
}
