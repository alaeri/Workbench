package com.alaeri.log.synth

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log


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


    val duration = 0.1f // duration of sound
    val sampleRate : Int = 22050// Hz (maximum frequency is 7902.13Hz (B8))
    val bSize = AudioTrack.getMinBufferSize(sampleRate,AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)
    val numSamples = (duration * sampleRate.toFloat()).toInt()
    val samples = DoubleArray(numSamples)
    val buffer = ShortArray(numSamples)
    val phases = DoubleArray(16)

    val audioTrack = AudioTrack(
        AudioManager.STREAM_MUSIC,
        sampleRate, AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT, bSize,
        AudioTrack.MODE_STREAM
    ).apply {
        play()
    }




    fun play(notes: List<Double>){
        Log.d("PLAYER", "bsize: $bSize: duration: ${bSize.toFloat() / sampleRate}")
        val increments = calculateIncrements(notes)
        calculateSineWave(phases, increments)
        val range = 0 until numSamples
        range.forEach { i ->
            buffer[i] = (samples[i] * Short.MAX_VALUE).toInt().toShort()  // Higher amplitude increases volume
        }
        audioTrack.write(buffer, 0, buffer.size)
    }

    fun calculateIncrements(frequencies: List<Double>): DoubleArray {
        val twoPi = 2 * Math.PI
        val result = DoubleArray(frequencies.size)
        for (i in frequencies.indices) {
            result[i] = twoPi / sampleRate * frequencies[i]
        }
        return result
    }

    fun calculateSineWave(phases: DoubleArray, increments: DoubleArray) {
        for (i in 0 until numSamples) {
            var sum = 0.0
            for (j in increments.indices) {
                sum += Math.sin(phases[j])
                phases[j] += increments[j]
                if (phases[j] >= 2 * Math.PI) {
                    phases[j] -= 2 * Math.PI
                }
            }
            samples[i] = sum / phases.size
        }
    }
}