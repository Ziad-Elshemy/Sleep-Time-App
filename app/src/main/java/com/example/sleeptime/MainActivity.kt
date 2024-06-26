package com.example.sleeptime

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import java.io.IOException

class MainActivity : AppCompatActivity(), SensorEventListener {

    lateinit var switchButton: SwitchCompat
    private lateinit var sensorManager: SensorManager
    private var brightness: Sensor? = null
    private lateinit var text: TextView
    private lateinit var pb: CircularProgressBar

    var mediaPlayerTurnOff:MediaPlayer?=null
    var mediaPlayerThanks:MediaPlayer?=null
    var flag:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mediaPlayerTurnOff = MediaPlayer.create(this,R.raw.turn_lights_off)
        mediaPlayerThanks = MediaPlayer.create(this,R.raw.good_boy)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        switchButton = findViewById(R.id.switchButton)
        switchButton.isChecked = false
        switchButton.text = "Disabled"
        switchButton.setOnCheckedChangeListener { buttonView, isCheked ->
            if (isCheked){
                switchButton.isChecked = true
                switchButton.text = "Enabeld"
            }else{
                switchButton.isChecked = false
                switchButton.text = "Disabeld"
            }

        }

        text = findViewById(R.id.tv_text)
        pb = findViewById(R.id.circularProgressBar)

        setUpSensorStuff()
    }

    private fun setUpSensorStuff() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        brightness = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (switchButton.isChecked){
            if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
                val light1 = event.values[0]

                text.text = "Sensor: $light1\n${brightness(light1)}"
                pb.setProgressWithAnimation(light1)
                if (light1.toInt() in 5..3000){
//                startAudio()
                    mediaPlayerTurnOff?.start()
//                    Toast.makeText(this,"in start",Toast.LENGTH_SHORT).show()
                    flag = 1

                }else{
                    if (flag==1){
                        mediaPlayerTurnOff?.pause()
                        mediaPlayerThanks?.start()
                        flag = 0
                    }
//                stopAudio()
                }
            }
        }

    }

    private fun startAudio() {
        val audioUrl = "https://www.bensound.com/bensound-music/bensound-ukulele.mp3"
        mediaPlayerTurnOff = MediaPlayer()
        mediaPlayerTurnOff!!.setAudioStreamType(AudioManager.STREAM_MUSIC)

        try {
//            mediaPlayer!!.setDataSource(audioUrl)
            mediaPlayerTurnOff!!.prepare()
            mediaPlayerTurnOff!!.start()
        }catch (ex : IOException){
            ex.printStackTrace()
        }
        Toast.makeText(this,"Audio started playing",Toast.LENGTH_SHORT).show()
    }

    private fun stopAudio(){
        if (mediaPlayerTurnOff!!.isPlaying&&mediaPlayerTurnOff!=null){
            mediaPlayerTurnOff!!.stop()
            mediaPlayerTurnOff!!.reset()
            mediaPlayerTurnOff!!.release()
        }else{
            Toast.makeText(this,"Audio has not played",Toast.LENGTH_SHORT).show()
        }
    }

    private fun brightness(brightness: Float): String {

        return when (brightness.toInt()) {
            0 -> "نوم الهنا يا نجم"
            in 1..5 -> "نوم الهنا يا نجم"
            in 5..40 -> "يووووه بقي"
            in 41..5000 -> "يا بارد يا بارد"
            in 5001..25000 -> "Incredibly bright"
            else -> "This light will blind you"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onResume() {
        super.onResume()
        // Register a listener for the sensor.
        sensorManager.registerListener(this, brightness, SensorManager.SENSOR_DELAY_NORMAL)
    }


    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}