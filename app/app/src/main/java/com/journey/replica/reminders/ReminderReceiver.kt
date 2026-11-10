package com.journey.replica.reminders

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.journey.replica.JourneyReplicaApp
import com.journey.replica.MainActivity
import com.journey.replica.R

import java.util.Calendar

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val label = intent.getStringExtra("label") ?: "Time to write in your journal"
        // Default allowed days "1".."7" mirrors Ls8/T;->z0 (ev_869ec36bd30b72d44c8e00049ff9d0deb1c9243cf4b1ab948379b2c06e531217)
        val daysOfWeek = intent.getStringExtra("daysOfWeek") ?: ReminderScheduler.DEFAULT_DAYS

        // Rule observed in com.journey.app.ReminderReceiver.onReceive
        // (Lcom/journey/app/ReminderReceiver;->onReceive, ev_30ce1ac8d9bded642d487938aff28f9699023df69b09179592799080d09f1710):
        // only fire if today's Calendar.DAY_OF_WEEK (1=Sun..7=Sat) is in the allowed-days set.
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK).toString()
        val allowed = daysOfWeek.split(',').map { it.trim() }.filter { it.isNotEmpty() }.toSet()

        if (allowed.isEmpty() || allowed.contains(today)) {
            val openIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pi = PendingIntent.getActivity(
                context, (System.currentTimeMillis() % Int.MAX_VALUE).toInt(), openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            // Channel id/importance aligned to e() (ev_0723f511c9f6aaa1ad58ddbcef4464be8baeed78186e0bdd732aa341cf366545):
            // "com.journey.app.reminder", IMPORTANCE_HIGH (4)
            val notif = NotificationCompat.Builder(context, JourneyReplicaApp.REMINDER_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_edit)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(label)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build()
            try {
                NotificationManagerCompat.from(context).notify((System.currentTimeMillis() % 100000).toInt(), notif)
            } catch (_: SecurityException) { }
        }
        // Scheduling/repeat is handled by AlarmManager.setRepeating as in Ls8/a;->j
        // (ev_1d0bbbfb93547273e3f5a72088f5d8738100a8007df05a1f51aa419187770eab); no per-fire reschedule needed.
    }
}
