package com.journey.replica

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.journey.replica.data.AppDatabase
import com.journey.replica.data.AppRepository
import com.journey.replica.reminders.ReminderScheduler

class JourneyReplicaApp : Application() {
    lateinit var database: AppDatabase
        private set
    lateinit var repository: AppRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
        repository = AppRepository(database, this)
        createNotificationChannel()
        ReminderScheduler.ensureDailyReschedule(this)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                REMINDER_CHANNEL_ID,
                "Journal Reminders",
                // Observed in ReminderReceiver.e: IMPORTANCE_HIGH (4)
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Daily reminders to write in your journal" }
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }

    companion object {
        // Channel id observed in original Lcom/journey/app/ReminderReceiver;->e(Landroid/content/Context;)V
        const val REMINDER_CHANNEL_ID = "com.journey.app.reminder"
    }
}
