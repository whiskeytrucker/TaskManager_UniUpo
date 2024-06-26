package it.polettomatteo.taskmanager_uniupo.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.adapters.TAG
import it.polettomatteo.taskmanager_uniupo.adapters.TasksAdapter
import it.polettomatteo.taskmanager_uniupo.dataclass.Task
import it.polettomatteo.taskmanager_uniupo.interfaces.StartNewRecycler

class TasksViewFragment: Fragment() {
    private var savedBundle: Bundle? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var addStuffBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.recycler_taskview, container, false)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        addStuffBtn = view.findViewById(R.id.addStuff)

        recyclerView = view.findViewById(R.id.recyclerTaskView)

        val tmp = ArrayList<Task>()
        var bundle: Bundle?


        if(savedBundle != null && this.arguments == null){
            bundle = savedBundle
        }else{
           bundle = this.arguments
        }


        var listener: StartNewRecycler? = null
        var userType: String = ""

        if(bundle != null){

            for(key in bundle.keySet()){
                if(key.compareTo("tipo") == 0)userType = bundle.getString("tipo")!!
                else if(key.compareTo("subtask_interface") == 0)listener = bundle["subtask_interface"] as StartNewRecycler
                else tmp.add(bundle.getSerializable(key) as Task)
            }

            if(userType.compareTo("pl") == 0){
                addStuffBtn.visibility = View.VISIBLE
            }

        }

        tmp.sortWith(compareBy{it.nome})


        val customAdapter =
            listener?.let { context?.let { it1 -> TasksAdapter(it1, tmp, it) } }
        recyclerView.adapter = customAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)

        return view
    }


    override fun onStart(){
        super.onStart()

        addStuffBtn.setOnClickListener{
            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, AddTaskFragment())
                .addToBackStack(null)
                .commit()
        }

    }

    override fun onPause() {
        super.onPause()
        savedBundle = this.arguments
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                Log.d("TasksViewFragment", parentFragmentManager.toString())

                parentFragmentManager.popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
