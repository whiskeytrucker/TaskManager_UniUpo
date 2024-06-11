package it.polettomatteo.taskmanager_uniupo.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import it.polettomatteo.taskmanager_uniupo.dataclass.Chat

class ChatsListViewModel : ViewModel() {
    val chats: MutableLiveData<ArrayList<Chat>> by lazy { MutableLiveData<ArrayList<Chat>>() }

    init {
        //setupListener()
    }


    /*
    private fun setupListener() {
        FirebaseFirestore
            .getInstance()
            .collection("Chats")
            .where(
                Filter.or(
                    Filter.equalTo("user1", getUserMail().toString()),
                    Filter.equalTo("user2", getUserMail().toString())
                )
            )
            .addSnapshotListener { snapshot, error ->
                if (snapshot != null && error == null) {
                    val out = ArrayList<Chat>()
                    for (doc in snapshot) {
                        val c = doc.toObject(Chat::class.java)
                        c.id = doc.id
                        out.add(c)
                    }
                    out.sortByDescending { it.lastMessage }
                    this.chats.postValue(out)
                }
            }
    }*/
}
