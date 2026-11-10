package com.journey.replica.util

import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

class AudioRecorder(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null
    var isRecording: Boolean = false
        private set

    fun toggleRecord(): Uri? {
        return if (isRecording) stop() else { start(); null }
    }

    private fun start() {
        try {
            val dir = File(context.filesDir, "audio").apply { mkdirs() }
            outputFile = File(dir, "rec_${System.currentTimeMillis()}.m4a")
            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) MediaRecorder(context) else @Suppress("DEPRECATION") MediaRecorder()
            recorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile!!.absolutePath)
                prepare()
                start()
            }
            isRecording = true
        } catch (_: Exception) {
            recorder?.release(); recorder = null; isRecording = false
        }
    }

    private fun stop(): Uri? {
        return try {
            recorder?.apply { stop(); release() }
            recorder = null
            isRecording = false
            outputFile?.let {
                FileProvider.getUriForFile(context, context.packageName + ".fileprovider", it)
            }
        } catch (_: Exception) {
            recorder?.release(); recorder = null; isRecording = false; null
        }
    }
}
