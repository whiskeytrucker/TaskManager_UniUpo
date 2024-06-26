package it.polettomatteo.taskmanager_uniupo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
        val view = inflater.inflate(R.layout.recycler_taskview, container, false)
        recyclerView = view.findViewById(R.id.recyclerTaskView)

        val tmp = ArrayList<Subtask>()

        val bundle = this.arguments

        if(bundle != null){
            var userType = bundle.getString("tipo")!!
            bundle.remove("tipo")

            for(key in bundle.keySet()){
                tmp.add(bundle.getSerializable(key) as Subtask)
            }
        }

        tmp.sortWith(compareBy{it.priorita})

        val customAdapter =
            context?.let { SubtasksAdapter(it, tmp) } // <-- Da cambiare con i dati presi da savedInstanceState
        recyclerView.adapter = customAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)

        return view
    }


    override fun onStart(){
        super.onStart()
        val goBackBtn = view?.findViewById<Button>(R.id.goBack)
        goBackBtn?.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack();
        }
    }

}