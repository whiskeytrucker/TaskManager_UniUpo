package it.polettomatteo.taskmanager_uniupo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.adapters.SubtasksAdapter
import it.polettomatteo.taskmanager_uniupo.dataclass.Subtask

class SubtasksViewFragment: Fragment() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.recycler_view, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        val tmp = ArrayList<Subtask>()

        val bundle = this.arguments

        if(bundle != null){
            for(key in bundle.keySet()){
                tmp.add(bundle.getSerializable(key) as Subtask)
            }
        }

        tmp.sortWith(compareBy{it.priorita})

        val customAdapter = SubtasksAdapter(tmp) // <-- Da cambiare con i dati presi da savedInstanceState
        recyclerView.adapter = customAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)

        return view
    }


}