package com.journey.replica.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.journey.replica.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Aligned to original mechanics observed in Ls8/a;->j (s8.a.j, ev_1d0bbbfb93547273e3f5a72088f5d8738100a8007df05a1f51aa419187770eab):
 * - time from prefs semantics reminder_hour_2 (default 20, Ls8/T;->A0 ev_ae99a9e8b09d3e74037bb1bf6eb67aa1faa0a10692d8ecf47772226a99ec552d)
 *   and reminder_min_2 (default 0, Ls8/T;->B0 ev_cc4e43d809285fbdb8fc56d04261af7226de991c77442519c0fe96fe5d107c5c)
 * - Calendar.HOUR_OF_DAY=hour, MINUTE=minute, SECOND=0
 * - if trigger <= now, add 0x5265c00 (86,400,000 ms = 1 day)
 * - AlarmManager.setRepeating(type=1 RTC_WAKEUP, triggerAtMillis, interval=0x5265c00, pendingIntent)
 * - disabled: alarmManager.cancel(pi) and pi.cancel()
 * Replica extension: per-reminder (id/hour/minute/days) rather than single global pref; receiver gates by allowed days (1=Sun..7=Sat).
 */
object ReminderScheduler {
    // Observed in Ls8/a;->j: AlarmManager.setRepeating(..., interval=0x5265c00 ms = 1 day)
    const val DAY_INTERVAL_MS = 86_400_000L
    const val DEFAULT_HOUR = 20   // reminder_hour_2 default (Ls8/T;->A0)
    const val DEFAULT_MINUTE = 0  // reminder_min_2 default (Ls8/T;->B0)
    const val DEFAULT_DAYS = "1,2,3,4,5,6,7" // reminder_day_2 default 1..7 (Ls8/T;->z0)

    fun schedule(context: Context, id: Long, hour: Int, minute: Int, label: String, daysOfWeek: String = DEFAULT_DAYS) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("reminderId", id)
            putExtra("hour", hour)
            putExtra("minute", minute)
            putExtra("label", label)
            putExtra("daysOfWeek", daysOfWeek)
        }
        val pi = PendingIntent.getBroadcast(
            context, id.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // Observed in Ls8/a;->j: if time <= now, roll forward by 1 day (0x5265c00 ms)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }
        try {
            // Observed: setRepeating(type=1 RTC_WAKEUP, triggerAtMillis, interval=0x5265c00, pi)
            am.setRepeating(AlarmManager.RTC_WAKEUP, cal.timeInMillis, DAY_INTERVAL_MS, pi)
        } catch (_: Throwable) {
            // Fallback for restricted environments
            try {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
            } catch (_: Throwable) { }
        }
    }

    fun cancel(context: Context, id: Long) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ReminderReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            context, id.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Observed in Ls8/a;->j (disabled path): cancel alarm and PendingIntent
        am.cancel(pi)
        pi.cancel()
    }

    suspend fun rescheduleAll(context: Context) {
        val db = AppDatabase.getInstance(context)
        db.reminderDao().getEnabled().forEach { r ->
            schedule(context, r.id, r.hour, r.minute, r.label, r.daysOfWeek)
        }
    }

    fun ensureDailyReschedule(context: Context) {
        // Best-effort: reschedule on app start
        CoroutineScope(Dispatchers.IO).launch {
            rescheduleAll(context)
        }
    }
}
