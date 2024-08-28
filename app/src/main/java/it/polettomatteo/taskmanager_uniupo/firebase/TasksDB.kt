package it.polettomatteo.taskmanager_uniupo.firebase

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import it.polettomatteo.taskmanager_uniupo.dataclass.Task
import java.lang.NumberFormatException

class TasksDB {

    companion object {
        private lateinit var idPrj: String
        fun getTasks(idProject: String, callback: (Bundle?) -> Unit) {
            idPrj = idProject

            FirebaseFirestore
                .getInstance()
                .collection("projects")
                .document(idProject)
                .collection("task")
                .get()
                .addOnSuccessListener { results ->
                    val documents = results.documents
                    val bundle = Bundle()
                    for ((index, doc) in documents.withIndex()) {
                        val data = doc.data
                        if (data != null) {
                            val progress: Int =
                                if (data["progress"].toString() != null && data["progress"].toString()
                                        .isNotEmpty()
                                ) {
                                    try {
                                        data["progress"].toString().toInt()
                                    } catch (e: NumberFormatException) {
                                        e.printStackTrace()
                                        0
                                    }
                                } else {
                                    0
                                }


                            val tmp = Task(
                                doc.id,
                                idProject,
                                data["nome"].toString(),
                                data["descr"].toString(),
                                data["dev"].toString(),
                                data["scadenza"] as Timestamp,
                                progress
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


        fun getTasksAsDev(idProject: String, developer: String, callback: (Bundle?) -> Unit) {

            FirebaseFirestore
                .getInstance()
                .collection("projects")
                .document(idProject)
                .collection("task")
                .whereEqualTo("dev", developer)
                .get()
                .addOnSuccessListener { results ->
                    val documents = results.documents
                    val bundle = Bundle()
                    for ((index, doc) in documents.withIndex()) {
                        val data = doc.data
                        if (data != null) {
                            val progress: Int =
                                if (data["progress"].toString() != null && data["progress"].toString()
                                        .isNotEmpty()
                                ) {
                                    try {
                                        data["progress"].toString().toInt()
                                    } catch (e: NumberFormatException) {
                                        e.printStackTrace()
                                        0
                                    }
                                } else {
                                    0
                                }


                            val tmp = Task(
                                doc.id,
                                idProject,
                                data["nome"].toString(),
                                data["descr"].toString(),
                                data["dev"].toString(),
                                data["scadenza"] as Timestamp,
                                progress
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


        fun addTask(
            title: String,
            descr: String,
            assigned: String,
            expiring: Timestamp
        ): Boolean {
            val data = hashMapOf(
                "nome" to title,
                "descr" to descr,
                "dev" to assigned,
                "scadenza" to expiring,
                "progess" to 0
            )

            if (data != null) {
                FirebaseFirestore
                    .getInstance()
                    .collection("projects")
                    .document(idPrj)
                    .collection("task")
                    .document()
                    .set(data)
                return true
            }
            return false
        }

        fun modifyTask(
            idTask: String,
            title: String,
            descr: String,
            assigned: String,
            expiring: Timestamp
        ): Boolean {
            val data = hashMapOf(
                "nome" to title,
                "descr" to descr,
                "dev" to assigned,
                "scadenza" to expiring,
                "progess" to 0
            )

            if (data != null) {
                FirebaseFirestore
                    .getInstance()
                    .collection("projects")
                    .document(idPrj)
                    .collection("task")
                    .document(idTask)
                    .set(data)
                return true
            }
            return false
        }

        fun deleteTask(idProject: String, idTask: String, callback: (Boolean?) -> Unit) {
            FirebaseFirestore
                .getInstance()
                .collection("projects")
                .document(idProject)
                .collection("task")
                .document(idTask)
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