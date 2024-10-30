package it.polettomatteo.taskmanager_uniupo.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.firebase.TasksDB
import java.util.Calendar
import java.util.Date

class ModifyTaskFragment() : Fragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.modify_task, container, false)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(this.arguments != null){
            val data = this.arguments

            if(data != null){
                val idTask = data.getString("idTask")

                val modTitle = view.findViewById<EditText>(R.id.modTitle)
                val modDescr = view.findViewById<EditText>(R.id.modDescr)
                val modAssigned = view.findViewById<EditText>(R.id.modAssigned)
                val progressText = view.findViewById<TextView>(R.id.progressTask)
                val seekBar = view.findViewById<SeekBar>(R.id.seekBar)
                val modDate = view.findViewById<DatePicker>(R.id.modDate)
                val modTime = view.findViewById<TimePicker>(R.id.modTime)

                modTitle.hint = data.getString("titolo")
                modDescr.hint = data.getString("descr")
                modAssigned.hint = data.getString("dev")

                progressText.text = "${data.getInt("progress")}%"
                seekBar.progress = data.getInt("progress")

                seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        progressText.text = "${progress}%"
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?){}
                    override fun onStopTrackingTouch(seekBar: SeekBar?){}
                })


                val ms = data.getLong("scadenza")
                val date = Date(ms)

                val calendar = Calendar.getInstance()
                calendar.time = date

                modDate.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

                modTime.hour = calendar.get(Calendar.HOUR_OF_DAY)
                modTime.minute = calendar.get(Calendar.MINUTE)

                val submitBtn = view.findViewById<Button>(R.id.submitModifyButton)




                submitBtn.setOnClickListener{
                    var title = modTitle.text.toString()
                    if(title.compareTo("") == 0)title = modTitle.hint.toString()

                    var descr = modDescr.text.toString()
                    if(descr.compareTo("") == 0)descr = modDescr.hint.toString()

                    var assigned = modAssigned.text.toString()
                    if(assigned.compareTo("") == 0)assigned = modAssigned.hint.toString()

                    var progress = seekBar.progress


                    val expiring = getTimestamp(modDate, modTime)


                    TasksDB.modifyTask(idTask.toString(), title, descr, assigned, progress, expiring) { bundle ->
                        if (bundle != null) {
                            if (bundle.getBoolean("result")) {
                                Toast.makeText(context, "Dati aggiornati!", Toast.LENGTH_SHORT).show()
                                requireActivity().supportFragmentManager.setFragmentResult("data",bundle)
                                requireActivity().supportFragmentManager.popBackStack();
                            }else{
                                Toast.makeText(context, "Errore nella modifica dei dati.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

            }

        }
        return view
    }



    private fun getTimestamp(dp: DatePicker, time: TimePicker): Timestamp {
        val day = dp.dayOfMonth
        val month = dp.month
        val year = dp.year

        val hour = time.hour
        val minute = time.minute

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val date = calendar.time

        return Timestamp(date)
    }
}

