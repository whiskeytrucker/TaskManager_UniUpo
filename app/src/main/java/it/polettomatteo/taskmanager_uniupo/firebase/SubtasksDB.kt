package it.polettomatteo.taskmanager_uniupo.firebase

import android.os.Bundle
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import it.polettomatteo.taskmanager_uniupo.dataclass.Subtask

class SubtasksDB {
    companion object{
        private lateinit var idPrg: String
        private lateinit var idTask: String


        fun getSubtasks(idProject: String, idTsk: String, callback: (Bundle?) -> Unit){
            idPrg = idProject
            idTask = idTsk

            FirebaseFirestore
                .getInstance()
                .collection("projects")
                .document(idProject)
                .collection("task")
                .document(idTask)
                .collection("sotto_task")
                .get()
                .addOnSuccessListener { results ->
                    val documents = results.documents
                    val bundle = Bundle()
                    for((index, doc) in documents.withIndex()){
                        val data = doc.data
                        if (data != null) {
                            Log.d(TAG, data.toString())

                            val tmp = Subtask(
                                doc.id,
                                idTask,
                                idProject,
                                data["stato"].toString().toInt(),
                                data["subDescr"].toString(),
                                data["priorita"].toString().toInt(),
                                data["scadenza"] as Timestamp,
                                data["progress"].toString().toInt()
                            )

                            bundle.putSerializable(index.toString(), tmp)
                        }
                    }
                    callback(bundle)
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    callback(null)
                }
        }


        fun addSubtask(
            subDescr: String,
            priority: Int,
            state: Int,
            expiring: Timestamp
        ): Boolean {
            val data = hashMapOf(
                "subDescr" to subDescr,
                "priorita" to priority,
                "stato" to state,
                "scadenza" to expiring,
                "progress" to 0
            )

            FirebaseFirestore
                .getInstance()
                .collection("projects")
                .document(idPrg)
                .collection("task")
                .document(idTask)
                .collection("sotto_task")
                .document()
                .set(data)
            return true
        }


        fun modifySubtask(
            idSub: String,
            subDescr: String,
            priority: Int,
            state: Int,
            expiring: Timestamp
        ): Boolean {
            val data = hashMapOf(
                "subDescr" to subDescr,
                "priorita" to priority,
                "stato" to state,
                "scadenza" to expiring,
                "progress" to 0
            )

            FirebaseFirestore
                .getInstance()
                .collection("projects")
                .document(idPrg)
                .collection("task")
                .document(idTask)
                .collection("sotto_task")
                .document(idSub)
                .set(data)
            return true

        }
        fun deleteSubtask(idProject: String, idTask: String, idSubtask: String, callback: (Boolean?) -> Unit){
            FirebaseFirestore
                .getInstance()
                .collection("projects")
                .document(idProject)
                .collection("task")
                .document(idTask)
                .collection("sotto_task")
                .document(idSubtask)
                .delete()
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    callback(false)
                }
        }
    }
}