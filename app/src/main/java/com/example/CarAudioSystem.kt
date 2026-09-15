package com.example

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

object CarAudioSystem {
    private var isMuted = false
    private val scope = CoroutineScope(Dispatchers.Default)

    fun toggleMute(): Boolean {
        isMuted = !isMuted
        return isMuted
    }

    fun isMuted(): Boolean = isMuted

    fun playEngineRev(speedKmh: Float) {
        if (isMuted) return
        scope.launch {
            val basePitch = 120f + (speedKmh * 1.8f).coerceAtMost(600f)
            synthesizeEngineTone(frequency = basePitch, durationMs = 90)
        }
    }

    fun playNitro(context: Context? = null) {
        if (isMuted) return
        scope.launch {
            synthesizeTone(startFreq = 300f, endFreq = 950f, durationMs = 280, noiseMix = 0.55f)
        }
        context?.let { vibrate(it, 120) }
    }

    fun playNearMiss(context: Context? = null) {
        if (isMuted) return
        scope.launch {
            synthesizeTone(startFreq = 800f, endFreq = 400f, durationMs = 150, noiseMix = 0.25f)
        }
        context?.let { vibrate(it, 35) }
    }

    fun playCoin(context: Context? = null) {
        if (isMuted) return
        scope.launch {
            synthesizeTone(startFreq = 1046f, endFreq = 1318f, durationMs = 100)
        }
        context?.let { vibrate(it, 20) }
    }

    fun playCrash(context: Context? = null) {
        if (isMuted) return
        scope.launch {
            synthesizeTone(startFreq = 220f, endFreq = 60f, durationMs = 350, noiseMix = 0.85f)
        }
        context?.let { vibrate(it, 400) }
    }

    fun playHorn(context: Context? = null) {
        if (isMuted) return
        scope.launch {
            synthesizeTone(startFreq = 420f, endFreq = 420f, durationMs = 160)
            synthesizeTone(startFreq = 420f, endFreq = 420f, durationMs = 160)
        }
        context?.let { vibrate(it, 60) }
    }

    private fun synthesizeEngineTone(frequency: Float, durationMs: Int) {
        try {
            val sampleRate = 16000
            val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
            if (numSamples <= 0) return

            val buffer = ShortArray(numSamples)
            var phase = 0.0
            val phaseInc = 2.0 * Math.PI * frequency / sampleRate

            for (i in 0 until numSamples) {
                phase += phaseInc
                val wave = sin(phase) + 0.5 * sin(2.0 * phase) + 0.25 * sin(3.0 * phase)
                val noise = (Math.random() * 2.0 - 1.0) * 0.15
                val sample = (wave + noise).coerceIn(-1.0, 1.0)
                buffer[i] = (sample * 16000).toInt().toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            Thread.sleep(durationMs.toLong() + 10)
            audioTrack.release()
        } catch (_: Exception) {}
    }

    private fun synthesizeTone(
        startFreq: Float,
        endFreq: Float,
        durationMs: Int,
        noiseMix: Float = 0f
    ) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
            if (numSamples <= 0) return

            val buffer = ShortArray(numSamples)
            var phase = 0.0

            for (i in 0 until numSamples) {
                val progress = i.toFloat() / numSamples
                val freq = startFreq + (endFreq - startFreq) * progress
                val phaseInc = 2.0 * Math.PI * freq / sampleRate
                phase += phaseInc

                val sine = sin(phase)
                val square = if (sine >= 0) 1.0 else -1.0
                val tone = (sine * 0.5 + square * 0.5)
                val noise = if (noiseMix > 0) (Math.random() * 2.0 - 1.0) * noiseMix else 0.0
                val mixed = (tone * (1f - noiseMix) + noise).coerceIn(-1.0, 1.0)
                val env = (1.0 - progress).coerceAtLeast(0.0)
                buffer[i] = (mixed * env * 22000).toInt().toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            Thread.sleep(durationMs.toLong() + 15)
            audioTrack.release()
        } catch (_: Exception) {}
    }

    fun vibrate(context: Context, durationMs: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                v?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }
}
