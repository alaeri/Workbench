package com.alaeri.log.synth

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlin.math.sin


/**
 * Created by Emmanuel Requier on 28/05/2021.
 * How can we make sounds that help us identify logs that are emitted in realtime ?
 *
 * We could emit sounds on distinct frequencies for threads:
 * Mainthread -> high pitch
 * BackgroundThreads -> lower pitches
 * How can we modulate to identify each log ? ...
 *
 * Shape the enveloppe of the sine wave with the name?
 * Change between sine/square/triangle waves with ...
 *
 * How can we identify entities in parameters/returned objects and receivers?
 * Knowing that each log has
 *
 */
class Player {



    fun play(note: Double){
        val duration = 10 // duration of sound
        val sampleRate : Int = 22050// Hz (maximum frequency is 7902.13Hz (B8))
        val numSamples = duration * sampleRate
        val samples = DoubleArray(numSamples)
        val buffer = ShortArray(numSamples)

        val range = 0 until numSamples
        range.forEach { i ->
            samples[i] = sin(2 * Math.PI * i / (sampleRate / note)) // Sine wave
            buffer[i] = (samples[i] * Short.MAX_VALUE).toInt().toShort()  // Higher amplitude increases volume
        }

        val audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            sampleRate, AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT, buffer.size,
            AudioTrack.MODE_STATIC
        )

        audioTrack.write(buffer, 0, buffer.size)
        audioTrack.play()

    }
}