package it.polettomatteo.taskmanager_uniupo.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import it.polettomatteo.taskmanager_uniupo.dataclass.Notification

class NotificationDB{
    companion object{

        fun promptUser(user: String, objTitle: String, callback: (Boolean?) -> Unit) {
            val loggedUser = FirebaseAuth.getInstance().currentUser?.email

            val toSend = hashMapOf(
                "channel" to "it.taskmanager.prompt",
                "channelDescr" to "Canale notifiche per i solleciti.",
                "channelTitle" to "Solleciti",
                "descr" to "L'utente ${loggedUser} ti ha sollecitato per l'oggetto: ${objTitle}",
                "title" to "Sollecito"
            )


            val db = FirebaseFirestore.getInstance()

            db.collection("notifiche")
                .whereEqualTo("user", user)
                .limit(1)
                .get()
                .addOnSuccessListener { docs ->
                    for (doc in docs) {
                        db.collection("notifiche")
                            .document(doc.id)
                            .collection("unseen")
                            .document()
                            .set(toSend, SetOptions.merge())
                            .addOnSuccessListener {
                                callback(true)
                            }
                            .addOnFailureListener {
                                it.printStackTrace()
                                callback(false)
                            }
                    }

                }
                .addOnFailureListener {
                    it.printStackTrace()
                    callback(false)
                }
        }


        fun getNotifications(user:String, callback: (ArrayList<Notification>?) -> Unit){
            val db = FirebaseFirestore.getInstance()

            db.collection("notifiche")
                .whereEqualTo("user", user)
                .limit(1)
                .get()
                .addOnSuccessListener { docs ->
                    for(doc in docs){
                        db.collection("notifiche")
                            .document(doc.id)
                            .collection("unseen")
                            .get()
                            .addOnSuccessListener { result ->
                                val nots = ArrayList<Notification>()
                                for(inDoc in result){
                                    val data = inDoc.data
                                    val tmpNot = Notification(
                                        inDoc.id,
                                        data["title"].toString(),
                                        data["descr"].toString(),
                                        data["channel"].toString(),
                                        data["channelTitle"].toString(),
                                        data["channelDescr"].toString()
                                    )
                                    nots.add(tmpNot)
                                }

                                callback(nots)
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