package com.journey.replica.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Transaction
    @Query("SELECT * FROM entries ORDER BY dateMillis DESC")
    fun getAllEntries(): Flow<List<EntryWithDetails>>

    @Transaction
    @Query("SELECT * FROM entries WHERE id = :id")
    fun getEntry(id: String): Flow<EntryWithDetails?>

    @Transaction
    @Query("SELECT * FROM entries WHERE latitude IS NOT NULL AND longitude IS NOT NULL ORDER BY dateMillis DESC")
    fun getEntriesWithLocation(): Flow<List<EntryWithDetails>>

    @Transaction
    @Query("SELECT * FROM entries WHERE dateMillis BETWEEN :start AND :end ORDER BY dateMillis DESC")
    fun getEntriesInRange(start: Long, end: Long): Flow<List<EntryWithDetails>>

    @Transaction
    @Query("SELECT * FROM entries WHERE sentiment = :mood ORDER BY dateMillis DESC")
    fun getEntriesByMood(mood: Double): Flow<List<EntryWithDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEntry(entry: EntryEntity)

    @Delete
    suspend fun deleteEntry(entry: EntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachment(attachment: AttachmentEntity)

    @Query("DELETE FROM attachments WHERE id = :id")
    suspend fun deleteAttachment(id: String)

    @Query("DELETE FROM attachments WHERE entryId = :entryId")
    suspend fun deleteAttachmentsForEntry(entryId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTag(tag: TagEntity): Long

    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags WHERE name = :name LIMIT 1")
    suspend fun getTagByName(name: String): TagEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntryTag(ref: EntryTagCrossRef)

    @Query("DELETE FROM entry_tags WHERE entryId = :entryId")
    suspend fun clearTagsForEntry(entryId: String)

    @Transaction
    suspend fun saveEntryWithDetails(
        entry: EntryEntity,
        attachments: List<AttachmentEntity>,
        tags: List<TagEntity>
    ) {
        upsertEntry(entry)
        deleteAttachmentsForEntry(entry.id)
        attachments.forEach { insertAttachment(it.copy(entryId = entry.id)) }
        clearTagsForEntry(entry.id)
        tags.forEach { tag ->
            val existing = getTagByName(tag.name.trim())
            val tagId = if (existing != null) existing.id else {
                val t = tag.copy(id = tag.id.ifBlank { java.util.UUID.randomUUID().toString() }, name = tag.name.trim())
                upsertTag(t)
                t.id
            }
            insertEntryTag(EntryTagCrossRef(entry.id, tagId))
        }
    }
}

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY hour, minute")
    fun getAll(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE enabled = 1")
    suspend fun getEnabled(): List<ReminderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(reminder: ReminderEntity): Long

    @Delete
    suspend fun delete(reminder: ReminderEntity)
}
