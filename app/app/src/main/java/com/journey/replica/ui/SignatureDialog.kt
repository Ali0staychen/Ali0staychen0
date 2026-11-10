package com.journey.replica.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.view.MotionEvent
import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.io.File
import java.io.FileOutputStream

@Composable
fun SignatureDialog(onDismiss: () -> Unit, onSaved: (Uri) -> Unit) {
    val context = LocalContext.current
    var padView by remember { mutableStateOf<SignaturePadView?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Draw signature") },
        text = {
            Column {
                AndroidView(
                    factory = { SignaturePadView(it).also { v -> padView = v } },
                    modifier = Modifier.fillMaxWidth().height(240.dp)
                )
                Text("Use your finger to sign above.", style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val uri = padView?.saveToCache(context)
                if (uri != null) onSaved(uri) else onDismiss()
            }) { Text("Save") }
        },
        dismissButton = {
            Row {
                TextButton(onClick = { padView?.clear() }) { Text("Clear") }
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        }
    )
}

class SignaturePadView(context: Context) : View(context) {
    private val path = android.graphics.Path()
    private val paint = Paint().apply {
        isAntiAlias = true
        color = android.graphics.Color.BLACK
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 8f
    }
    private val paths = mutableListOf<Pair<android.graphics.Path, Paint>>()

    init { setBackgroundColor(android.graphics.Color.WHITE) }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paths.forEach { (p, pa) -> canvas.drawPath(p, pa) }
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x; val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> path.moveTo(x, y)
            MotionEvent.ACTION_MOVE -> { path.lineTo(x, y); invalidate() }
            MotionEvent.ACTION_UP -> {
                val p = android.graphics.Path(path)
                paths.add(p to Paint(paint))
                path.reset()
                invalidate()
            }
        }
        return true
    }

    fun clear() { paths.clear(); path.reset(); invalidate() }

    fun saveToCache(context: Context): Uri? {
        if (width == 0 || height == 0) return null
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        draw(c)
        val dir = File(context.cacheDir, "signatures").apply { mkdirs() }
        val file = File(dir, "sig_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return androidx.core.content.FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
    }
}
