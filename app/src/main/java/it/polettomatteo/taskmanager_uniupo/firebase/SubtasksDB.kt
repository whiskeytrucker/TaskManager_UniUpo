package it.polettomatteo.taskmanager_uniupo.firebase

import android.os.Bundle
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import it.polettomatteo.taskmanager_uniupo.dataclass.Subtask
import it.polettomatteo.taskmanager_uniupo.dataclass.Task

class SubtasksDB {
    companion object{
        fun getSubtasks(idProject: String, idTask: String, callback: (Bundle?) -> Unit){
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


        fun modifySubtask(){

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