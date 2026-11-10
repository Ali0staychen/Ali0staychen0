package com.journey.replica.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class AppRepository(private val db: AppDatabase, private val context: Context) {
    val entries: Flow<List<EntryWithDetails>> = db.entryDao().getAllEntries()
    val tags: Flow<List<TagEntity>> = db.entryDao().getAllTags()
    val reminders: Flow<List<ReminderEntity>> = db.reminderDao().getAll()

    fun entry(id: String) = db.entryDao().getEntry(id)
    fun entriesWithLocation() = db.entryDao().getEntriesWithLocation()
    fun entriesInMonth(year: Int, month: Int): Flow<List<EntryWithDetails>> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        val start = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        val end = cal.timeInMillis - 1
        return db.entryDao().getEntriesInRange(start, end)
    }
    fun entriesByMood(mood: Double) = db.entryDao().getEntriesByMood(mood)

    suspend fun saveEntry(entry: EntryEntity, attachments: List<AttachmentEntity>, tags: List<TagEntity>) {
        db.entryDao().saveEntryWithDetails(entry.copy(updatedAt = System.currentTimeMillis()), attachments, tags)
    }
    suspend fun deleteEntry(entry: EntryEntity) = db.entryDao().deleteEntry(entry)

    suspend fun upsertReminder(reminder: ReminderEntity): Long = db.reminderDao().upsert(reminder)
    suspend fun deleteReminder(reminder: ReminderEntity) = db.reminderDao().delete(reminder)
    suspend fun enabledReminders(): List<ReminderEntity> = db.reminderDao().getEnabled()
}
