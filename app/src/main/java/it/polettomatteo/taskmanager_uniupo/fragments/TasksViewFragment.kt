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
import it.polettomatteo.taskmanager_uniupo.firebase.NotificationDB
import it.polettomatteo.taskmanager_uniupo.firebase.ProjectsDB
import it.polettomatteo.taskmanager_uniupo.firebase.TasksDB
import it.polettomatteo.taskmanager_uniupo.interfaces.StartNewRecycler
import it.polettomatteo.taskmanager_uniupo.interfaces.TempActivity
import kotlin.math.ceil

class TasksViewFragment: Fragment() {
    private var savedBundle: Bundle? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var addStuffBtn: Button
    private lateinit var goBackBtn: Button
    private lateinit var tasksAdapter: TasksAdapter
    private lateinit var userType: String
    private var tmp = ArrayList<Task>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("TasksViewFragment", "onCreateView()")
        val view = inflater.inflate(R.layout.recycler_taskview, container, false)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        addStuffBtn = view.findViewById(R.id.addStuff)
        goBackBtn = view.findViewById(R.id.goBack)
        recyclerView = view.findViewById(R.id.recyclerTaskView)


        var bundle: Bundle?
        tmp = ArrayList()


        if(savedBundle != null && this.arguments == null){
            bundle = savedBundle
        }else{
           bundle = this.arguments
        }


        if(bundle != null){
            for(key in bundle.keySet()){
                if(key.compareTo("tipo") == 0)userType = bundle.getString("tipo")!!
                else tmp.add(bundle.getSerializable(key) as Task)
            }

            if(userType.compareTo("pl") == 0){
                addStuffBtn.visibility = View.VISIBLE
            }

        }

        tmp.sortWith(compareBy{it.nome})


        tasksAdapter =
            subtaskListener.let { context?.let { it1 ->
                TasksAdapter(
                    userType,
                    it1,
                    tmp,
                    it,
                    modifyActivityListener,
                    deleteActivityListener
                )
            } }!!
        recyclerView.adapter = tasksAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)

        return view
    }


    override fun onStart(){
        Log.d("TasksViewFragment", "onStart()")
        super.onStart()

        goBackBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack();
        }

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

            var med = 0.0
            for(taskArr in tmp){med += taskArr.progress}

            med = ceil(med/tmp.size)
            ProjectsDB.updateTaskProgress(tmp[0].idPrg, med)

            if(med >= 100){
                NotificationDB.notifySuperior(tmp[0].idPrg)
            }
        }

    }

    override fun onPause() {
        Log.d("TasksViewFragment", "onPause()")
        savedBundle = this.arguments
        tasksAdapter = recyclerView.adapter as TasksAdapter
        super.onPause()
    }

    override fun onResume(){
        Log.d("TasksViewFragment", "onResume()")
        recyclerView.adapter = tasksAdapter
        super.onResume()
    }




    /* LISTENER */
    val subtaskListener = object: StartNewRecycler{
        override fun onStartNewRecyclerView(data: Bundle){
            val fragment = SubtasksViewFragment()

            data.putSerializable("tipo", userType)
            fragment.arguments = data

            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit()

        }
    }



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
}
