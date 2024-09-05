package it.polettomatteo.taskmanager_uniupo.fragments

import android.annotation.SuppressLint
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
            listener?.let { context?.let { it1 -> TasksAdapter(userType, it1, tmp, it, modifyActivityListener) } }!!
        recyclerView.adapter = tasksAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)

        return view
    }


    val modifyActivityListener = object: TempActivity{
        override fun onStartNewTempActivity(data: Bundle) {
            var fragment = ModifyTaskFragment()

            if(data != null){
                fragment.arguments = data
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .addToBackStack(null)
                .commit()
        }

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
            Log.d(TAG, "key: ${key}\t Bundle: ${bundle.toString()}")
            val task = bundle.getSerializable("data") as Task
            val done = bundle.getString("done")

            if (done?.compareTo("mod") == 0){
                val i = findIndex(task)
                tmp.removeAt(i)
                tasksAdapter.notifyItemRemoved(i)
            }

            tmp.add(task)
            tmp.sortWith(compareBy{it.nome})
            tasksAdapter.notifyItemInserted(tmp.indexOf(task))
        }

    }

    override fun onPause() {
        savedBundle = this.arguments
        super.onPause()
    }

    private fun findIndex(toFind: Task):Int{
        for(task in tmp){
            if(task.id == toFind.id)return tmp.indexOf(task)
        }
        return -1
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
