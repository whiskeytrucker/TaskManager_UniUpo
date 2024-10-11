package it.polettomatteo.taskmanager_uniupo.firebase

import android.os.Bundle
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import it.polettomatteo.taskmanager_uniupo.dataclass.Message
import java.time.LocalDateTime
import java.util.Locale

val TAG = "FBMsgService"

class FBMsgService {
    companion object{
        fun getUserToken(){
            val msgSer = FirebaseMessaging.getInstance()

            msgSer.token
                .addOnCompleteListener{task ->
                    if(!task.isSuccessful){
                        Log.w(TAG, "Il fetch del Token non è avvenuto correttamente.", task.exception)
                        return@addOnCompleteListener
                    }

                    val token = task.result

                    val msg = token.toString()
                    Log.d(TAG, msg)
                }
        }
    }
}









