package edu.temple.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    lateinit var timerBinder : TimerService.TimerBinder
    var isConnected = false
    var running = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.textView).text = "100"


        val timerHandler = Handler(Looper.getMainLooper()) {
            findViewById<TextView>(R.id.textView).text = it.what.toString()
            true
        }


        val serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                timerBinder = service as TimerService.TimerBinder
                timerBinder.setHandler(timerHandler)
                isConnected = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                isConnected = false
            }

        }
        bindService(
            Intent(this, TimerService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )
        // switches from start to pause when pressed
        // when it paused, says unpause
        findViewById<Button>(R.id.startButton).setOnClickListener {
            if(isConnected) {
                if(!running) {
                    timerBinder.start(100)
                    findViewById<Button>(R.id.startButton).text = "Pause"
                    running = true
                } else if(!timerBinder.isRunning) {
                    timerBinder.pause()
                    findViewById<Button>(R.id.startButton).text = "Pause"
                } else {
                    timerBinder.pause()
                    findViewById<Button>(R.id.startButton).text = "Unpause"
                }

            }
        }
        
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            if(!timerBinder.isRunning) {
                timerBinder.pause()
            }
            timerBinder.stop()
            findViewById<Button>(R.id.startButton).text = "Start"
            running = false
        }
    }
}