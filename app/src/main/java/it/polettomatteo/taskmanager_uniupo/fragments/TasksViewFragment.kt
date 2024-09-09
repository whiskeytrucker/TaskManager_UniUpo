package it.polettomatteo.taskmanager_uniupo.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.adapters.TasksAdapter
import it.polettomatteo.taskmanager_uniupo.dataclass.Task
import it.polettomatteo.taskmanager_uniupo.interfaces.StartNewRecycler
import it.polettomatteo.taskmanager_uniupo.interfaces.TempActivity

class TasksViewFragment: Fragment() {
    private var savedBundle: Bundle? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var addStuffBtn: Button
    private lateinit var tasksAdapter: TasksAdapter
    private var tmp = ArrayList<Task>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView:")
        val view = inflater.inflate(R.layout.recycler_taskview, container, false)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        addStuffBtn = view.findViewById(R.id.addStuff)
        recyclerView = view.findViewById(R.id.recyclerTaskView)


        var bundle: Bundle?
        tmp = ArrayList<Task>()


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



        tasksAdapter =
            listener?.let { context?.let { it1 -> TasksAdapter(userType, it1, tmp, it, modifyActivityListener, deleteActivityListener) } }!!
        recyclerView.adapter = tasksAdapter
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

        val key = "data"
        parentFragmentManager.setFragmentResultListener(key, this){key, bundle ->
            val task = bundle.getSerializable("data") as Task
            val done = bundle.getString("done")

            if (done?.compareTo("mod") == 0){
                val i = findIndex(task)
                if(i != -1){
                    tmp.removeAt(i)
                    tasksAdapter.notifyItemRemoved(i)
                }

            }

            tmp.add(task)
            tmp.sortWith(compareBy{it.nome})
            tasksAdapter.notifyItemInserted(tmp.indexOf(task))
        }

    }

    override fun onPause() {
        Log.d(TAG, "onPause:")
        savedBundle = this.arguments
        super.onPause()
    }

    override fun onResume(){
        Log.d(TAG, "onResume:")
        /*
        for(task in tmp){
            Log.d("AAAAAAAAAAAAAAAAAA", "${task.toString()}")
        }*/
        super.onResume()
        recyclerView.adapter = tasksAdapter
    }




    /* LISTENER */
    val modifyActivityListener = object: TempActivity{
        override fun onStartNewTempActivity(data: Bundle) {
            val fragment = ModifyTaskFragment()
            fragment.arguments = data

            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .addToBackStack(null)
                .commit()
        }

    }

    val deleteActivityListener = object: TempActivity{
        override fun onStartNewTempActivity(data: Bundle) {
            Log.d(TAG, "onStartNewTempActivity(deleteActivityListener):")
            val position = data.getInt("pos")
            val id = data.getString("id")
            tmp.removeAt(position)
            tasksAdapter.notifyItemRemoved(position)
            tasksAdapter.notifyItemRangeChanged(position, tmp.size)

            if(savedBundle != null){
                arguments?.remove(id?.let { findIndex(it, arguments!!) })
            }else{
                savedBundle?.remove(id?.let { findIndex(it, savedBundle!!) })
            }
            /*
            for(task in tmp){
                Log.d("AAAAAAAAAAAAAAAAAA", "${task.toString()}")
            }*/

        }
    }




    private fun findIndex(toFind: Task):Int{
        for(task in tmp){
            if(task.id == toFind.id)return tmp.indexOf(task)
        }
        return -1
    }

    private fun findIndex(toFind: String, bundle: Bundle):String{
        for(key in bundle.keySet()){
            if(key.compareTo("tipo") != 0 && key.compareTo("subtask_interface") != 0){
                val task = bundle.getSerializable(key) as Task
                if(task.id == toFind)return key
            }
        }
        return ""
    }




/*
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
*/
}
