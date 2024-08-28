package it.polettomatteo.taskmanager_uniupo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.firebase.SubtasksDB
import java.util.Calendar


class AddSubtaskFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.new_subtask, container, false)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val submitBtn = view.findViewById<Button>(R.id.submitSubButton)

        submitBtn.setOnClickListener{
            val subDescr = view.findViewById<EditText>(R.id.modSubDescr).text.toString()
            val priority = view.findViewById<Spinner>(R.id.spinPriority).selectedItemPosition
            val state = view.findViewById<Spinner>(R.id.spinState).selectedItemPosition
            val dateS = view.findViewById<DatePicker>(R.id.modSubDate)
            val timeS = view.findViewById<TimePicker>(R.id.modSubTime)

            val expiring = getTimestamp(dateS, timeS)

            if(SubtasksDB.addSubtask(subDescr, priority, state, expiring)){
                Toast.makeText(context, "Dati salvati!", Toast.LENGTH_SHORT).show()

                requireActivity().supportFragmentManager.popBackStack();
            }
        }


        return view
    }




    private fun getTimestamp(dp: DatePicker, time: TimePicker): Timestamp {
        // Ottieni la data dal DatePicker
        val day = dp.dayOfMonth
        val month = dp.month
        val year = dp.year

        // Ottieni l'ora dal TimePicker
        val hour = time.hour
        val minute = time.minute

        // Crea un oggetto Calendar e imposta la data e l'ora
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // Ottieni l'oggetto Date dalla Calendar
        val date = calendar.time

        // Converti l'oggetto Date in un Timestamp di Firebase
        val timestamp = Timestamp(date)

        return timestamp
    }
}
