package com.example.detetorsonolencia.ruben_morais.camera

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GestorAlertas(private val contexto: Context) {

    private val vibrador: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val gestor = contexto.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        gestor.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        contexto.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    private val audioManager = contexto.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var geradorTom: ToneGenerator? = null
    private var tarefaSom: Job? = null
    private val scopeAlerta = CoroutineScope(Dispatchers.Main)

    fun ativarAlerta() {
        vibrarContinuo()
        iniciarSomContinuo()
    }

    fun pararAlerta() {
        tarefaSom?.cancel()
        tarefaSom = null
        vibrador.cancel()
        geradorTom?.release()
        geradorTom = null
    }

    private fun vibrarContinuo() {
        val padrao = longArrayOf(0, 600, 150, 600, 150, 600, 150, 600)
        val efeito = VibrationEffect.createWaveform(padrao, 1)
        vibrador.vibrate(efeito)
    }

    private fun iniciarSomContinuo() {
        tarefaSom?.cancel()
        tarefaSom = scopeAlerta.launch(Dispatchers.IO) {

            val volumeMaximo = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volumeMaximo, 0)

            while (isActive) {
                try {
                    geradorTom?.release()
                    geradorTom = ToneGenerator(AudioManager.STREAM_ALARM, 100)
                    geradorTom?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 800)
                    delay(900)
                    geradorTom?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 800)
                    delay(900)
                    geradorTom?.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 1000)
                    delay(1100)
                } catch (e: Exception) {
                    e.printStackTrace()
                    delay(500)
                }
            }
        }
    }
}