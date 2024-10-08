package it.polettomatteo.taskmanager_uniupo.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.snap
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import it.polettomatteo.taskmanager_uniupo.adapters.ChatAdapter
import it.polettomatteo.taskmanager_uniupo.dataclass.Message
import it.polettomatteo.taskmanager_uniupo.firebase.ChatDB
import it.polettomatteo.taskmanager_uniupo.R

class ChatFragment: Fragment() {
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    private var receiversArr = ArrayList<String>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var spinUser1: Spinner
    private lateinit var sendBtn: Button
    private lateinit var toSend: EditText

    private lateinit var chatArr: ArrayList<Message>

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

        sendBtn = view.findViewById(R.id.sendBtn)

        val bundle = this.arguments
        spinUser1 = view.findViewById(R.id.usersSpin)

        if(bundle != null){
            for(key in bundle.keySet()){
                bundle.getString(key)?.let { receiversArr.add(it) }
            }
        }

        val spinAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, receiversArr)
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinUser1.adapter = spinAdapter


        val userQuery = spinUser1.getItemAtPosition(spinUser1.selectedItemPosition).toString()
        chatArr = ArrayList()

        if(currentUser != null){
            currentUser!!.email?.let {
                ChatDB.getOldMessages(it, userQuery){ results ->
                    if(results != null){
                        for(key in results.keySet()){
                            val msg = results.getSerializable(key) as Message
                            chatArr.add(msg)
                        }
                    }
                }
            }
        }

        toSend = view.findViewById(R.id.editTextMessage)

        this.recyclerView = view.findViewById(R.id.recyclerViewChat)

        var chatAdapter = ChatAdapter(chatArr, requireContext())

        this.recyclerView.adapter = chatAdapter
        this.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)

        return view
    }

    override fun onStart(){
        listenForMessages()
        super.onStart()

        spinUser1.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val userQuery = spinUser1.getItemAtPosition(spinUser1.selectedItemPosition).toString()
                val chatArr = ArrayList<Message>()

                listenForMessages()

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


        sendBtn.setOnClickListener {
            val sender = spinUser1.getItemAtPosition(spinUser1.selectedItemPosition).toString()
            val msg = toSend.text.toString()
            if(msg.trim().compareTo("") == 0)Toast.makeText(requireContext(), "Il messaggio è vuoto!!", Toast.LENGTH_LONG).show()
            else{
                ChatDB.sendMessage(msg, sender){ result ->
                    if(result == null)Toast.makeText(requireContext(), "Impossibile inviare il messaggio!", Toast.LENGTH_SHORT).show()
                    else {
                        toSend.setText("")
                        chatArr.add(result)
                        val chatAdapter = ChatAdapter(chatArr, requireContext())
                        recyclerView.adapter = chatAdapter
                        chatAdapter.notifyItemChanged(chatArr.size - 1)
                    }
                }
            }



        }



    }


    fun listenForMessages() {
        val db = FirebaseFirestore.getInstance()
        db.collection("chat")
                .whereEqualTo("user0", currentUser?.email)
                .whereEqualTo("user1", spinUser1.getItemAtPosition(spinUser1.selectedItemPosition).toString())
                .get()
                .addOnSuccessListener { docs ->
                    for(doc in docs){
                        db.collection("chat")
                            .document(doc.id)
                            .collection("messages")
                            .orderBy("timestamp", Query.Direction.ASCENDING)
                            .addSnapshotListener { snapshot, err ->
                                if (err != null) {
                                    Log.e("ChatFragment", err.toString())
                                    return@addSnapshotListener
                                }


                                for (document in snapshot!!.documentChanges) {
                                    if (document.type == DocumentChange.Type.ADDED) {
                                        val data = document.document.data

                                        val tmp = Message(
                                            data["sender"].toString().toBoolean(),
                                            data["text"].toString(),
                                            data["timestamp"] as Timestamp
                                        )

                                        chatArr.add(tmp)
                                        val chatAdapter = ChatAdapter(chatArr, requireContext())
                                        recyclerView.adapter = chatAdapter
                                        chatAdapter.notifyItemChanged(chatArr.size - 1)
                                    }
                                }
                            }
                    }

                }
                .addOnFailureListener {
                    it.printStackTrace()
                }

    }

}