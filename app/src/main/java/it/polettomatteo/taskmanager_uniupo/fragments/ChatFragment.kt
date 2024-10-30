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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import it.polettomatteo.taskmanager_uniupo.adapters.ChatAdapter
import it.polettomatteo.taskmanager_uniupo.dataclass.Message
import it.polettomatteo.taskmanager_uniupo.firebase.ChatDB
import it.polettomatteo.taskmanager_uniupo.R

class ChatFragment: Fragment() {
    private var TAG = "ChatFragment"
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    private var listenerMsg: ListenerRegistration? = null
    private var receiversArr = ArrayList<String>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var spinUser1: Spinner
    private lateinit var sendBtn: Button
    private lateinit var toSend: EditText
    private lateinit var fieldUser: String

    private var chatArr = ArrayList<Message>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.recycler_chat, container, false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser

        sendBtn = view.findViewById(R.id.sendBtn)

        val bundle = this.arguments
        spinUser1 = view.findViewById(R.id.usersSpin)

        if(bundle != null){
            for(key in bundle.keySet()){
                if(key.compareTo("field") == 0) { fieldUser = bundle.getString(key).toString()}
                else { bundle.getString(key)?.let { receiversArr.add(it) } }
            }
        }

        val spinAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, receiversArr)
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinUser1.adapter = spinAdapter

        toSend = view.findViewById(R.id.editTextMessage)

        this.recyclerView = view.findViewById(R.id.recyclerViewChat)
        val chatAdapter = ChatAdapter(chatArr, requireContext())

        this.recyclerView.adapter = chatAdapter
        this.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)

        return view
    }

    override fun onStart(){
        super.onStart()

        spinUser1.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                listenerMsg?.remove()
                val userQuery = spinUser1.getItemAtPosition(spinUser1.selectedItemPosition).toString()
                currentUser?.email?.let { listenForMessages(it, userQuery) }

                if(currentUser != null){
                    currentUser!!.email?.let {
                        ChatDB.getOldMessages(fieldUser, it, userQuery){ results ->
                            if(results != null){
                                chatArr.clear()
                                for(key in results.keySet()){
                                    chatArr.add(results.getSerializable(key) as Message)
                                }

                                val chatAdapter = ChatAdapter(chatArr, requireContext())
                                recyclerView.adapter = chatAdapter
                            }
                        }
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) { }

        }


        sendBtn.setOnClickListener {
            val sender = spinUser1.getItemAtPosition(spinUser1.selectedItemPosition).toString()
            val msg = toSend.text.toString()
            if(msg.trim().compareTo("") == 0)Toast.makeText(requireContext(), "Il messaggio è vuoto!!", Toast.LENGTH_LONG).show()
            else{
                ChatDB.sendMessage(msg, sender, fieldUser){ result ->
                    if(result == null)Toast.makeText(requireContext(), "Impossibile inviare il messaggio!", Toast.LENGTH_SHORT).show()
                    else {toSend.setText(getString(R.string.empty))}
                }
            }



        }



    }


    fun listenForMessages(user0: String, user1: String) {
        val db = FirebaseFirestore.getInstance()
        var fieldToGet = ""

        var boolSender = true
        if(fieldUser.compareTo("user0") == 0)fieldToGet = "user1"
        else if(fieldUser.compareTo("user1") == 0){ fieldToGet = "user0"; boolSender = false}


        db.collection("chat")
            .whereEqualTo(fieldUser, user0)
            .whereEqualTo(fieldToGet, user1)
            .limit(1)
            .get()
            .addOnSuccessListener { docs ->
                for(doc in docs){
                    listenerMsg = db.collection("chat")
                        .document(doc.id)
                        .collection("messages")
                        .orderBy("timestamp", Query.Direction.ASCENDING)
                        .addSnapshotListener { snapshot, err ->
                            if (err != null) {
                                Log.e(TAG, err.toString())
                                return@addSnapshotListener
                            }


                            for (document in snapshot!!.documentChanges) {
                                if (document.type == DocumentChange.Type.ADDED) {
                                    val data = document.document.data

                                    val tmp = Message(
                                        boolSender,
                                        data["text"].toString(),
                                        data["timestamp"] as Timestamp
                                    )

                                    chatArr.add(tmp)

                                    val chatAdapter = ChatAdapter(chatArr, requireContext())
                                    recyclerView.adapter = chatAdapter
                                    recyclerView.adapter?.notifyItemChanged(chatArr.size - 1)
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