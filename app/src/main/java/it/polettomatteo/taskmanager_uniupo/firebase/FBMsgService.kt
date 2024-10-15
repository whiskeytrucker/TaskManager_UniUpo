package it.polettomatteo.taskmanager_uniupo.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import it.polettomatteo.taskmanager_uniupo.dataclass.Notification
import it.polettomatteo.taskmanager_uniupo.interfaces.NotificationAPIService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val TAG = "FBMsgService"

class FBMsgService: FirebaseMessagingService() {
    companion object{
        private var token = ""

        fun getUserToken(){
            val msgSer = FirebaseMessaging.getInstance()

            msgSer.token
                .addOnCompleteListener{task ->
                    if(!task.isSuccessful){
                        Log.w(TAG, "Il fetch del Token non è avvenuto correttamente.", task.exception)
                        return@addOnCompleteListener
                    }

                    token = task.result.toString()
                }
        }

        fun subscribeToTopic(){
            val msgSer = FirebaseMessaging.getInstance()

            msgSer.subscribeToTopic("test_notifica")
                .addOnCompleteListener{task ->
                    if(!task.isSuccessful){
                        Log.e(TAG, "Errore nell'iscrizione del topic!")
                    }else{
                        Log.d(TAG, "Sottoscritto!")
                    }
                }
        }




        fun sendNotification(){
            val apiService = Retrofit.Builder()
                .baseUrl("https://fcm.googleapis.com/v1/projects/taskmanager-uniupo-bb5f9/messages:send/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NotificationAPIService::class.java)

            val notificationData = Notification(
                token,
                "Test notifica 2",
                "Ciao! Sono un test!"
            )

            apiService.sendNotification(notificationData).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d(TAG, "Notifica inviata con successo")
                    Log.d(TAG, response.toString())

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e(TAG, "Errore nell'invio della notifica", t)
                }
            })
        }
    }
}









