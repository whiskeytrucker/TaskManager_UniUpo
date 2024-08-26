package it.polettomatteo.taskmanager_uniupo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import it.polettomatteo.taskmanager_uniupo.R
import java.util.Calendar
import java.util.Date

class ModifySubtaskFragment() : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.modify_subtask, container, false)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(arguments != null){
            val data = this.arguments

            if(data != null) {
                val id = data.getString("id")
                val idPrg = data.getString("idPrg")
                val idTask = data.getString("idTask")

                val modSubDescr = view.findViewById<EditText>(R.id.modSubDescr)
                val spinPriority = view.findViewById<Spinner>(R.id.spinPriority)
                val spinState = view.findViewById<Spinner>(R.id.spinState)
                val modSubDate = view.findViewById<DatePicker>(R.id.modSubDate)
                val modSubTime = view.findViewById<TimePicker>(R.id.modSubTime)

                modSubDescr.hint = data.getString("subDescr")

                if(data.getInt("priorita") <= 0){spinPriority.setSelection(0)}
                else if(data.getInt("priorita") >= 4){spinPriority.setSelection(4)}
                else spinPriority.setSelection(data.getInt("priorita"))

                if(data.getInt("stato") <= 0){spinState.setSelection(0)}
                else if(data.getInt("stato") >= 3){spinState.setSelection(4)}
                else spinState.setSelection(data.getInt("stato"))

                val ms = data.getLong("expiring")
                val date = Date(ms)

                val calendar = Calendar.getInstance()
                calendar.time = date

                modSubDate.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

                // Imposta l'ora nel TimePicker
                modSubTime.hour = calendar.get(Calendar.HOUR_OF_DAY)
                modSubTime.minute = calendar.get(Calendar.MINUTE)


                val submitBtn = view.findViewById<Button>(R.id.subModifySubButton)

                submitBtn.setOnClickListener{

                }












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