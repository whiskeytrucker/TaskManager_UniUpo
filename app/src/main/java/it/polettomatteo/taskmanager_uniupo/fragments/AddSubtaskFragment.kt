package it.polettomatteo.taskmanager_uniupo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import it.polettomatteo.taskmanager_uniupo.R


class AddSubtaskFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.new_subtask, container, false)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val priorita = view.findViewById<Spinner>(R.id.addSubPriority)

        context?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.priorita_subtask,
                android.R.layout.simple_spinner_item
            ).also{ adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                priorita.adapter = adapter
            }
        }


        return view
    }

}
