package com.example.edukid_android.games

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.edukid_android.components.GameHeader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun PianoGame(navController: NavController) {
    val synthesizer = remember { PianoSynthesizer() }
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        onDispose {
            synthesizer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GameHeader(title = "Piano", score = 0, onBackClick = { navController.popBackStack() })
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Box(
                 modifier = Modifier
                     .weight(1f)
                     .fillMaxWidth()
                     .horizontalScroll(rememberScrollState())
            ) {
                Row(
                   modifier = Modifier
                       .fillMaxHeight()
                       .padding(horizontal = 16.dp)
                ) {
                    val octaves = 2
                    repeat(octaves) { octave ->
                        Octave(octave, synthesizer)
                    }
                }
            }
            
            Text(
                "Scroll to see more keys!",
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun Octave(octaveIndex: Int, synthesizer: PianoSynthesizer) {
    Box(modifier = Modifier.fillMaxHeight().width(350.dp)) {
        // White Keys
        Row(modifier = Modifier.fillMaxSize()) {
            val notes = listOf("C", "D", "E", "F", "G", "A", "B")
            notes.forEach { note ->
                PianoKey(
                    note = "$note$octaveIndex",
                    color = Color.White,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .border(1.dp, Color.Black),
                    synthesizer = synthesizer
                )
            }
        }
        
        // Black Keys (Overlay)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) 
                .padding(start = 35.dp) 
        ) {
            val blackNotes = listOf("C#", "D#", null, "F#", "G#", "A#", null)
            blackNotes.forEach { note ->
                if (note != null) {
                    PianoKey(
                        note = "$note$octaveIndex",
                        color = Color.Black,
                        modifier = Modifier
                            .width(30.dp)
                            .fillMaxHeight(),
                        synthesizer = synthesizer
                    )
                   Spacer(modifier = Modifier.width(20.dp))
                } else {
                    Spacer(modifier = Modifier.width(50.dp))
                }
            }
        }
    }
}

@Composable
fun PianoKey(
    note: String,
    color: Color,
    modifier: Modifier,
    synthesizer: PianoSynthesizer
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Play sound on press
    LaunchedEffect(isPressed) {
        if (isPressed) {
            synthesizer.playNote(note)
        }
    }

    val finalColor = if (isPressed) Color.Gray else color

    Box(
        modifier = modifier
            .background(finalColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { }
    )
}

// Simple Synthesizer
class PianoSynthesizer {
    private val sampleRate = 44100
    private var audioTrack: AudioTrack? = null

    init {
        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(minBufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()
    }

    suspend fun playNote(note: String) = withContext(Dispatchers.Default) {
        val freq = getFrequency(note)
        if (freq > 0) {
            val buffer = generateTone(freq, 500) // 500ms tone
            audioTrack?.write(buffer, 0, buffer.size)
        }
    }

    private fun generateTone(freqHz: Double, durationMs: Int): ShortArray {
        val numSamples = (durationMs * sampleRate / 1000)
        val sample = ShortArray(numSamples)
        val phaseIncrement = (2 * PI * freqHz) / sampleRate
        var phase = 0.0

        for (i in 0 until numSamples) {
            // Mix sine wave with some harmonics for "piano-like" sound
            val value = (sin(phase) * 0.7 + sin(phase * 2) * 0.2 + sin(phase * 4) * 0.1)
            // Apply envelope (fade out)
            val envelope = 1.0 - (i.toDouble() / numSamples)
            
            sample[i] = (value * envelope * Short.MAX_VALUE).toInt().toShort()
            phase += phaseIncrement
        }
        return sample
    }

    private fun getFrequency(note: String): Double {
        // Base frequencies for octave 0 (simplified)
        // C0 approx 16.35
        val baseFreqs = mapOf(
            "C" to 16.35, "C#" to 17.32, "D" to 18.35, "D#" to 19.45,
            "E" to 20.60, "F" to 21.83, "F#" to 23.12, "G" to 24.50,
            "G#" to 25.96, "A" to 27.50, "A#" to 29.14, "B" to 30.87
        )

        val noteName = note.filter { !it.isDigit() }
        val octaveChar = note.last { it.isDigit() }
        val octave = octaveChar.digitToInt() + 4 // Shift to middle C range (C4)

        val base = baseFreqs[noteName] ?: return 0.0
        return base * Math.pow(2.0, octave.toDouble())
    }

    fun release() {
        audioTrack?.stop()
        audioTrack?.release()
    }
}
