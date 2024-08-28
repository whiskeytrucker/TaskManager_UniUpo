package it.polettomatteo.taskmanager_uniupo.fragments

import android.os.Bundle
import android.util.Log
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
import it.polettomatteo.taskmanager_uniupo.interfaces.TempActivity

val TAG = "SubtasksViewFragment"

class SubtasksViewFragment: Fragment() {
    private var savedBundle: Bundle? = null
    private lateinit var addStuffBtn: Button
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.recycler_taskview, container, false)
        recyclerView = view.findViewById(R.id.recyclerTaskView)

        addStuffBtn = view.findViewById(R.id.addStuff)

        val tmp = ArrayList<Subtask>()

        val bundle = this.arguments
        var userType:String = ""


        if(bundle != null){
            if(bundle.getString("tipo") != null){
                userType = bundle.getString("tipo")!!
                bundle.remove("tipo")
            }


            for(key in bundle.keySet()){
                tmp.add(bundle.getSerializable(key) as Subtask)
            }

            if(userType.compareTo("d") == 0){
                addStuffBtn.visibility = View.VISIBLE
            }

        }

        tmp.sortWith(compareByDescending<Subtask>{it.priorita}.thenBy { it.scadenza })

        val customAdapter =
            context?.let { SubtasksAdapter(userType, it, tmp, modifyActivityListener) } // <-- Da cambiare con i dati presi da savedInstanceState
        recyclerView.adapter = customAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)

        return view
    }


    private val modifyActivityListener = object: TempActivity {
        override fun onStartNewTempActivity(data: Bundle) {
            var fragment = ModifySubtaskFragment()
            fragment.arguments = data

            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .addToBackStack(null)
                .commit()
        }

    }

    override fun onStart(){
        super.onStart()
        val goBackBtn = view?.findViewById<Button>(R.id.goBack)
        goBackBtn?.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack();
        }


        addStuffBtn.setOnClickListener{
            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, AddSubtaskFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onPause() {
        super.onPause()
        savedBundle = this.arguments
    }



}