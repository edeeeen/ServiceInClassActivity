package edu.temple.myapplication

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    lateinit var timerBinder : TimerService.TimerBinder
    var isConnected = false
    var running = false

    val serviceConnection = object : ServiceConnection {
        val timerHandler = Handler(Looper.getMainLooper()) {
            findViewById<TextView>(R.id.textView).text = it.what.toString()
            true
        }
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            timerBinder = service as TimerService.TimerBinder
            timerBinder.setHandler(timerHandler)
            isConnected = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isConnected = false
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.textView).text = "100"






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
                } else if(!running) {
                    timerBinder.pause()
                    running = true
                    findViewById<Button>(R.id.startButton).text = "Pause"
                } else {
                    timerBinder.pause()
                    running = false
                    findViewById<Button>(R.id.startButton).text = "Unpause"
                }

            }
        }
        
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            if(isConnected) {
                if (running) {
                    timerBinder.pause()
                    running = false
                }
            }
            timerBinder.stop()
            findViewById<Button>(R.id.startButton).text = "Start"
            running = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionStop -> {
                if(!running) {
                    timerBinder.pause()
                    running = false
                }
                timerBinder.stop()
                findViewById<Button>(R.id.startButton).text = "Start"
                running = false
            }
            R.id.actionStart -> {
                if(isConnected) {
                    if(!running) {
                        timerBinder.start(100)
                        findViewById<Button>(R.id.startButton).text = "Pause"
                        running = true
                    } else if(running) {
                        timerBinder.pause()
                        running = true
                        findViewById<Button>(R.id.startButton).text = "Pause"
                    } else {
                        timerBinder.pause()
                        running = true
                        findViewById<Button>(R.id.startButton).text = "Unpause"
                    }

                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onStart() {
        super.onStart()
        bindService(
            Intent(this, TimerService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onStop() {
        super.onStop()
        if(isConnected) {
            unbindService(serviceConnection)
        }
        isConnected = false
    }
}