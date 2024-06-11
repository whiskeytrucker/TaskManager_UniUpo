package it.polettomatteo.taskmanager_uniupo.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.adapters.ChatsAdapter
import it.polettomatteo.taskmanager_uniupo.dataclass.Chat
import it.polettomatteo.taskmanager_uniupo.firebase.UsersDB
import it.polettomatteo.taskmanager_uniupo.models.ChatsListViewModel
import kotlinx.coroutines.*

class ChatActivity : AppCompatActivity (){
    private var arrChat = ArrayList<Chat>()
    private lateinit var auth: FirebaseAuth
    var currentUser: FirebaseUser? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemViewModel: ChatsListViewModel

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        setContentView(R.layout.chat_activity)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser

        var arrChat = ArrayList<Chat>()

        val tmp = CoroutineScope(Dispatchers.Main).launch {
            async{fetchData()}.await()

            continueWithData()
        }
    }

    private fun continueWithData() {
        Log.d("ChatActivity 3 PORCO DIO", arrChat.size.toString())


        this.recyclerView = findViewById(R.id.recyclerview)
        this.recyclerView.layoutManager = LinearLayoutManager(this)
        this.recyclerView.adapter = ChatsAdapter(arrChat, this)

        this.itemViewModel = ViewModelProvider(this)[ChatsListViewModel::class.java]
        this.itemViewModel.chats.observe(this) { newArray ->
            if (newArray != null) (this.recyclerView.adapter as ChatsAdapter).updateList(newArray)
        }
    }


    private fun fetchData(){
        UsersDB.getUserChat(currentUser?.email.toString()){bundle ->
            if(bundle != null){
                val array = ArrayList<Chat>()
                for(key in bundle.keySet()){
                    array.add(bundle[key] as Chat)
                    arrChat.add(bundle[key] as Chat)
                }
                Log.d("ChatActivity 1", array.size.toString())
                Log.d("ChatActivity 1", arrChat.size.toString())
            }
            Log.d("ChatActivity 2", arrChat.size.toString())
        }
    }
}