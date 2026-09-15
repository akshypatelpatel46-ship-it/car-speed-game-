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

object RetroAudioSystem {
    private var isMuted = false
    private val scope = CoroutineScope(Dispatchers.Default)

    fun toggleMute(): Boolean {
        isMuted = !isMuted
        return isMuted
    }

    fun isMuted(): Boolean = isMuted

    fun playSwordSwing(context: Context? = null) {
        if (isMuted) return
        scope.launch {
            // White noise burst descending pitch for whoosh
            synthesizeTone(startFreq = 650f, endFreq = 180f, durationMs = 120, noiseMix = 0.35f)
        }
        context?.let { vibrate(it, 40) }
    }

    fun playStep(context: Context? = null) {
        if (isMuted) return
        scope.launch {
            synthesizeTone(startFreq = 140f, endFreq = 90f, durationMs = 45, noiseMix = 0.2f)
        }
    }

    fun playCoin(context: Context? = null) {
        if (isMuted) return
        scope.launch {
            // B5 then E6 arpeggio chime
            synthesizeTone(startFreq = 987f, endFreq = 987f, durationMs = 60)
            synthesizeTone(startFreq = 1318f, endFreq = 1318f, durationMs = 120)
        }
        context?.let { vibrate(it, 25) }
    }

    fun playHit(context: Context? = null) {
        if (isMuted) return
        scope.launch {
            synthesizeTone(startFreq = 220f, endFreq = 110f, durationMs = 90, noiseMix = 0.6f)
        }
        context?.let { vibrate(it, 70) }
    }

    fun playTalk(context: Context? = null) {
        if (isMuted) return
        scope.launch {
            synthesizeTone(startFreq = 440f, endFreq = 520f, durationMs = 50)
        }
    }

    fun playHeal(context: Context? = null) {
        if (isMuted) return
        scope.launch {
            synthesizeTone(startFreq = 523f, endFreq = 659f, durationMs = 70)
            synthesizeTone(startFreq = 784f, endFreq = 1046f, durationMs = 150)
        }
        context?.let { vibrate(it, 50) }
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
            var currentPhase = 0.0

            for (i in 0 until numSamples) {
                val progress = i.toFloat() / numSamples
                val freq = startFreq + (endFreq - startFreq) * progress
                val phaseIncrement = 2.0 * Math.PI * freq / sampleRate
                currentPhase += phaseIncrement

                // Square wave with harmonics for 16-bit retro warmth
                val sine = sin(currentPhase)
                val square = if (sine >= 0) 1.0 else -1.0
                val tone = (sine * 0.4 + square * 0.6)

                // Optional noise
                val noise = if (noiseMix > 0) (Math.random() * 2.0 - 1.0) * noiseMix else 0.0
                val mixed = (tone * (1f - noiseMix) + noise).coerceIn(-1.0, 1.0)

                // Envelope: quick attack, linear decay
                val envelope = (1.0 - progress).coerceAtLeast(0.0)
                buffer[i] = (mixed * envelope * 24000).toInt().toShort()
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
            Thread.sleep(durationMs.toLong() + 20)
            audioTrack.release()
        } catch (_: Exception) {
            // Audio generation safely ignored on unsupported emulator state
        }
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
        } catch (_: Exception) {
            // Safe fallback
        }
    }
}
