package it.polettomatteo.taskmanager_uniupo.firebase

import android.os.Bundle
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import it.polettomatteo.taskmanager_uniupo.dataclass.Project
import it.polettomatteo.taskmanager_uniupo.dataclass.Task

class TasksDB {
    companion object {
        fun getTasks(idProject: String, callback: (Bundle?) -> Unit) {
            FirebaseFirestore
                .getInstance()
                .collection("projects")
                .document(idProject)
                .collection("task")
                .get()
                .addOnSuccessListener { results ->
                    val documents = results.documents
                    val bundle = Bundle()
                    for((index, doc) in documents.withIndex()){
                        val data = doc.data
                        if (data != null) {
                            val tmp = Task(
                                doc.id,
                                idProject,
                                data["nome"].toString(),
                                data["descr"].toString(),
                                data["dev"].toString(),
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
    }
}