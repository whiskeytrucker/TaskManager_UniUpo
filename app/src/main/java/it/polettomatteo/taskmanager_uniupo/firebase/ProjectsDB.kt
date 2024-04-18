package it.polettomatteo.taskmanager_uniupo.firebase

import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import it.polettomatteo.taskmanager_uniupo.dataclass.Project


var TAG = "Risultato query"

class ProjectsDB {
    companion object{
        fun getProjects(username: String, callback: (Bundle?) -> Unit) {
            FirebaseFirestore
                .getInstance()
                .collection("projects")
                .whereEqualTo("autore", username)
                .get()
                .addOnSuccessListener {results ->
                    val documents = results.documents
                    val bundle = Bundle()
                    for((index, doc) in documents.withIndex()){
                        val data = doc.data
                        if (data != null) {
                            val tmp = Project(
                                data["titolo"].toString(),
                                data["descr"].toString(),
                                data["assigned"].toString(),
                                data["autore"].toString(),
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
