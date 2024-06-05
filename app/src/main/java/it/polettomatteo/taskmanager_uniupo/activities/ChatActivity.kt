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

class ChatActivity : AppCompatActivity (){
    private lateinit var auth: FirebaseAuth
    var currentUser: FirebaseUser? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemViewModel: ChatsListViewModel

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        setContentView(R.layout.chat_activity)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser

        val arrChat = ArrayList<Chat>()

        UsersDB.getUserChat(currentUser?.email.toString()){bundle ->
            if(bundle != null){
                for(key in bundle.keySet()){
                    arrChat.add(bundle.get(key) as Chat)
                }
            }
        }

        arrChat.forEach{item -> Log.d("ChatActivity", item.toString())}


        this.recyclerView = findViewById(R.id.recyclerview)
        this.recyclerView.layoutManager = LinearLayoutManager(this)
        this.recyclerView.adapter = ChatsAdapter(arrChat, this)

        this.itemViewModel = ViewModelProvider(this)[ChatsListViewModel::class.java]
        this.itemViewModel.chats.observe(this){ newArray ->
            if(newArray!=null) (this.recyclerView.adapter as ChatsAdapter).updateList(newArray)
        }

    }
}