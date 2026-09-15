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

object BikeAudioSystem {
    private var isMuted = false
    private val scope = CoroutineScope(Dispatchers.Default)

    fun toggleMute(): Boolean {
        isMuted = !isMuted
        return isMuted
    }

    fun isMuted(): Boolean = isMuted

    /**
     * High-rev screaming superbike inline-4 engine sound (10,000 - 15,000 RPM)
     */
    fun playBikeRev(speedKmh: Float) {
        if (isMuted) return
        scope.launch {
            // Superbikes rev higher than cars (220Hz base up to 900Hz screaming pitch)
            val basePitch = 220f + (speedKmh * 2.2f).coerceAtMost(980f)
            synthesizeBikeEngineTone(frequency = basePitch, durationMs = 85)
        }
    }

    fun playNitro(context: Context? = null) {
        if (isMuted) return
        scope.launch {
            synthesizeTone(startFreq = 400f, endFreq = 1200f, durationMs = 280, noiseMix = 0.5f)
        }
        context?.let { vibrate(it, 100) }
    }

    fun playWheelie(context: Context? = null) {
        if (isMuted) return
        scope.launch {
            synthesizeTone(startFreq = 650f, endFreq = 900f, durationMs = 120, noiseMix = 0.3f)
        }
        context?.let { vibrate(it, 40) }
    }

    fun playNearMiss(context: Context? = null) {
        if (isMuted) return
        scope.launch {
            synthesizeTone(startFreq = 900f, endFreq = 450f, durationMs = 140, noiseMix = 0.35f)
        }
        context?.let { vibrate(it, 30) }
    }

    fun playCoin(context: Context? = null) {
        if (isMuted) return
        scope.launch {
            synthesizeTone(startFreq = 1200f, endFreq = 1600f, durationMs = 90)
        }
        context?.let { vibrate(it, 20) }
    }

    fun playCrash(context: Context? = null) {
        if (isMuted) return
        scope.launch {
            synthesizeTone(startFreq = 250f, endFreq = 50f, durationMs = 400, noiseMix = 0.9f)
        }
        context?.let { vibrate(it, 350) }
    }

    fun playHorn(context: Context? = null) {
        if (isMuted) return
        scope.launch {
            synthesizeTone(startFreq = 480f, endFreq = 480f, durationMs = 150)
        }
        context?.let { vibrate(it, 50) }
    }

    private fun synthesizeBikeEngineTone(frequency: Float, durationMs: Int) {
        try {
            val sampleRate = 16000
            val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
            if (numSamples <= 0) return

            val buffer = ShortArray(numSamples)
            var phase = 0.0
            val phaseInc = 2.0 * Math.PI * frequency / sampleRate

            for (i in 0 until numSamples) {
                phase += phaseInc
                // High-rev screaming harmonic mix
                val tone = sin(phase) + 0.6 * sin(2.0 * phase) + 0.3 * sin(3.0 * phase) + 0.15 * sin(4.0 * phase)
                val noise = (Math.random() * 2.0 - 1.0) * 0.12
                val sample = (tone * 0.7 + noise).coerceIn(-1.0, 1.0)
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
                val noise = if (noiseMix > 0) (Math.random() * 2.0 - 1.0) * noiseMix else 0.0
                val mixed = (sine * (1f - noiseMix) + noise).coerceIn(-1.0, 1.0)
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
            Thread.sleep(durationMs.toLong() + 12)
            audioTrack.release()
        } catch (_: Exception) {}
    }

    private fun vibrate(context: Context, durationMs: Long) {
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
