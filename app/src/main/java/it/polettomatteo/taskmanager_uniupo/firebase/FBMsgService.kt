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

        fun sendNotification(){
            val key = "_DCClyZYFjWLkOuAjOFui6YGA9IFWx4-U3NTQp5uEac"
            val token =
                "fj4LYQx6QOW4yorj-CCBA3:APA91bF9BL_8fXtPPsBoDxTvxvOc6dzEjeFRZp7PI7b8VNr1tFLRTO4gJu2o6-TzxG-kqOZBrSIaJZFngW-XBKw6myZ3ZuhsTfMJ1HmprVrr75pq7ETKTd0acT69TPTPoA_XkDEMbyZg"

            val apiService = Retrofit.Builder()
                .baseUrl("https://fcm.googleapis.com/fcm/send/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NotificationAPIService::class.java)

            val notificationData = Notification(
                token,
                "Test notifica 2",
                "Ciao! Sono un test!"
            )

            apiService.sendNotification(key, notificationData).enqueue(object : Callback<ResponseBody> {
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









