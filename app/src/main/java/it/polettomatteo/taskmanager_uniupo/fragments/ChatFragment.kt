package it.polettomatteo.taskmanager_uniupo.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.adapters.ChatAdapter
import it.polettomatteo.taskmanager_uniupo.dataclass.Message
import it.polettomatteo.taskmanager_uniupo.firebase.ChatDB

class ChatFragment: Fragment() {
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    private var receiversArr = ArrayList<String>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var spinUser1: Spinner


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.recycler_chat, container, false)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser

        val bundle = this.arguments
        spinUser1 = view.findViewById<Spinner>(R.id.usersSpin)

                if(bundle != null){
            for(key in bundle.keySet()){
                bundle.getString(key)?.let { receiversArr.add(it) }
            }
        }

        val spinAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, receiversArr)
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinUser1.adapter = spinAdapter


        val userQuery = spinUser1.getItemAtPosition(spinUser1.selectedItemPosition).toString()
        val chatArr = ArrayList<Message>()

        if(currentUser != null){
            currentUser!!.email?.let {
                ChatDB.getOldMessages(it, userQuery){ results ->
                    if(results != null){
                        for(key in results.keySet()){
                            chatArr.add(results.getSerializable(key) as Message)
                        }
                    }
                }
            }
        }

        this.recyclerView = view.findViewById(R.id.recyclerViewChat)

        var chatAdapter = ChatAdapter(chatArr, requireContext())

        this.recyclerView.adapter = chatAdapter
        this.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)

        return view
    }

    override fun onStart(){
        super.onStart()

        spinUser1.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val userQuery = spinUser1.getItemAtPosition(spinUser1.selectedItemPosition).toString()
                val chatArr = ArrayList<Message>()

                if(currentUser != null){
                    currentUser!!.email?.let {
                        ChatDB.getOldMessages(it, userQuery){ results ->
                            if(results != null){
                                for(key in results.keySet()){
                                    chatArr.add(results.getSerializable(key) as Message)
                                }

                                val chatAdapter = ChatAdapter(chatArr, requireContext())

                                recyclerView.adapter = chatAdapter
                                chatAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                }



            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }


    fun listenForMessages(chatId: String) {
        FirebaseFirestore.getInstance()
            .collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    println("Errore nella lettura dei messaggi: $e")
                    return@addSnapshotListener
                }

                for (document in snapshots!!.documentChanges) {
                    if (document.type == DocumentChange.Type.ADDED) {
                        val message = document.document.toObject(Message::class.java)
                        println("Nuovo messaggio: ${message.text}")
                    }
                }
            }
    }

}