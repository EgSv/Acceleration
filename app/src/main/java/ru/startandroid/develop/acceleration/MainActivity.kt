package ru.startandroid.develop.acceleration

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {
    private var tvText: TextView? = null
    private var sensorManager: SensorManager? = null
    private var sensorAccel: Sensor? = null
    private var sensorLinAccel: Sensor? = null
    private var sensorGravity: Sensor? = null
    private var sb = StringBuilder()
    private var timer: Timer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvText = findViewById<View>(R.id.tvText) as TextView
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorAccel = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorLinAccel = sensorManager!!
            .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        sensorGravity = sensorManager!!.getDefaultSensor(Sensor.TYPE_GRAVITY)
    }

    override fun onResume() {
        super.onResume()
        sensorManager!!.registerListener(listener, sensorAccel,
            SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager!!.registerListener(listener, sensorLinAccel,
            SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager!!.registerListener(listener, sensorGravity,
            SensorManager.SENSOR_DELAY_NORMAL)
        timer = Timer()
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                runOnUiThread { showInfo() }
            }
        }
        timer!!.schedule(task, 0, 400)
    }

    override fun onPause() {
        super.onPause()
        sensorManager!!.unregisterListener(listener)
        timer!!.cancel()
    }

    private fun format(values: FloatArray): String {
        return String.format("%1$.1f\t\t%2$.1f\t\t%3$.1f", values[0], values[1],
            values[2])
    }

    fun showInfo() {
        sb.setLength(0)
        sb.append("Accelerometer: " + format(valuesAccel))
            .append("""
    
    
    Accel motion: ${format(valuesAccelMotion)}
    """.trimIndent())
            .append("""
    
    Accel gravity : ${format(valuesAccelGravity)}
    """.trimIndent())
            .append("""
    
    
    Lin accel : ${format(valuesLinAccel)}
    """.trimIndent())
            .append("""
    
    Gravity : ${format(valuesGravity)}
    """.trimIndent())
        tvText!!.text = sb
    }

    var valuesAccel = FloatArray(3)
    var valuesAccelMotion = FloatArray(3)
    var valuesAccelGravity = FloatArray(3)
    var valuesLinAccel = FloatArray(3)
    var valuesGravity = FloatArray(3)
    private var listener: SensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        override fun onSensorChanged(event: SensorEvent) {
            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    var i = 0
                    while (i < 3) {
                        valuesAccel[i] = event.values[i]
                        valuesAccelGravity[i] =
                            (0.1 * event.values[i] + 0.9 * valuesAccelGravity[i]).toFloat()
                        valuesAccelMotion[i] = (event.values[i]
                                - valuesAccelGravity[i])
                        i++
                    }
                }
                Sensor.TYPE_LINEAR_ACCELERATION -> {
                    var i = 0
                    while (i < 3) {
                        valuesLinAccel[i] = event.values[i]
                        i++
                    }
                }
                Sensor.TYPE_GRAVITY -> {
                    var i = 0
                    while (i < 3) {
                        valuesGravity[i] = event.values[i]
                        i++
                    }
                }
            }
        }
    }
}