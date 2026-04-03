package com.example.detetorsonolencia.ruben_morais.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class GestorSensores(
    contexto: Context,
    private val aoAlterarLuz: (Float) -> Unit,
    private val aoAlterarAcelerometro: (Float, Float, Float) -> Unit
) {

    private val gestorSensores = contexto.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensorLuz = gestorSensores.getDefaultSensor(Sensor.TYPE_LIGHT)
    private val acelerometro = gestorSensores.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val ouvinte = object : SensorEventListener {
        override fun onSensorChanged(evento: SensorEvent) {
            when (evento.sensor.type) {
                Sensor.TYPE_LIGHT -> aoAlterarLuz(evento.values[0])
                Sensor.TYPE_ACCELEROMETER -> aoAlterarAcelerometro(
                    evento.values[0], evento.values[1], evento.values[2]
                )
            }
        }
        override fun onAccuracyChanged(sensor: Sensor?, precisao: Int) {}
    }

    fun iniciar() {
        sensorLuz?.let {
            gestorSensores.registerListener(ouvinte, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        acelerometro?.let {
            gestorSensores.registerListener(ouvinte, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun parar() {
        gestorSensores.unregisterListener(ouvinte)
    }

    fun temSensorLuz() = sensorLuz != null
    fun temAcelerometro() = acelerometro != null
}