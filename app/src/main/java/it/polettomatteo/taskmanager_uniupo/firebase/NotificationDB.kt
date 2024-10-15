package it.polettomatteo.taskmanager_uniupo.firebase

import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import it.polettomatteo.taskmanager_uniupo.dataclass.Notification

class NotificationDB {
    companion object {
        fun getChannelNames(mail: String, callback: (Bundle?) -> Unit) {
            val db = FirebaseFirestore.getInstance()

            db.collection("notifiche")
                .whereEqualTo("user", mail)
                .limit(1)
                .get()
                .addOnSuccessListener { docs ->
                    for (doc in docs.documents) {
                        db.collection("notifiche")
                            .document(doc.id)
                            .collection("channels")
                            .get()
                            .addOnSuccessListener { result ->
                                val bun = Bundle()
                                for (channel in result) {
                                    val tmp = Notification(
                                        channel["id"].toString(),
                                        channel["title"].toString(),
                                        channel["descr"].toString()
                                    )

                                    bun.putSerializable(channel.id, tmp)
                                }
                                callback(bun)
                            }
                            .addOnFailureListener {
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