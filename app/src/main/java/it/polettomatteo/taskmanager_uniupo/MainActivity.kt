package it.polettomatteo.taskmanager_uniupo

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.VideoView
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var tv: TextView
    private lateinit var executor: ScheduledExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv = findViewById<TextView>(R.id.textView)
        val vW = findViewById<VideoView>(R.id.videoView)

        executor = Executors.newSingleThreadScheduledExecutor()

        val videoPath = "android.resource://" + packageName + "/" + R.raw.diego

        startTimer()

        vW.setVideoURI(Uri.parse(videoPath))
        vW.start()
        vW.setOnPreparedListener{ mp ->
            mp.isLooping = true
        }


    }

    private fun startTimer(){
        executor.scheduleAtFixedRate({
            runOnUiThread{
                if(tv.text == "Sì Diego"){
                    tv.setText(R.string.text2)
                    Log.d("MainActivity","Vai Diego!!!")
                }else{
                    tv.setText(R.string.text1)
                }
            }
        }, 0, 1500, TimeUnit.MILLISECONDS)
    }

    override fun onDestroy() {
        super.onDestroy()

        executor.shutdown()
    }
}