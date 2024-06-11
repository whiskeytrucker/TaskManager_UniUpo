package it.polettomatteo.taskmanager_uniupo.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.adapters.ProjectsAdapter
import it.polettomatteo.taskmanager_uniupo.adapters.TasksAdapter
import it.polettomatteo.taskmanager_uniupo.dataclass.Project
import it.polettomatteo.taskmanager_uniupo.dataclass.Task
import it.polettomatteo.taskmanager_uniupo.interfaces.StartNewRecycler

class TasksViewFragment: Fragment() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.recycler_view, container, false)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)


        recyclerView = view.findViewById(R.id.recyclerView)

        val tmp = ArrayList<Task>()

        val bundle = this.arguments

        var listener: StartNewRecycler? = null

        if(bundle != null){
            if(bundle.getSerializable("subtask_interface") != null){
                listener = bundle.getSerializable("subtask_interface") as StartNewRecycler
                bundle.remove("subtask_interface")
            }

            for(key in bundle.keySet()){
                tmp.add(bundle.getSerializable(key) as Task)
            }
        }

        tmp.sortWith(compareBy{it.nome})

        val customAdapter =
            listener?.let { TasksAdapter(tmp, it) }
        recyclerView.adapter = customAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)




        return view
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
