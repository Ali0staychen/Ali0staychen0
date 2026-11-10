package com.journey.replica.data

import androidx.room.*
import java.util.UUID

@Entity(tableName = "entries")
data class EntryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val text: String = "",
    val dateMillis: Long = System.currentTimeMillis(),
    val timezone: String = java.util.TimeZone.getDefault().id,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val weatherTempC: Double? = null,
    val weatherDescription: String? = null,
    val weatherIcon: String? = null,
    val weatherPlace: String? = null,
    val sentiment: Double? = null, // mood 1.0-5.0 (estimated scale)
    val activitySteps: Int? = null, // motion/activity (estimated as steps)
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "attachments",
    foreignKeys = [ForeignKey(
        entity = EntryEntity::class,
        parentColumns = ["id"],
        childColumns = ["entryId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("entryId")]
)
data class AttachmentEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val entryId: String,
    val uri: String,
    val mimeType: String,
    val displayName: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String
)

@Entity(
    tableName = "entry_tags",
    primaryKeys = ["entryId", "tagId"],
    foreignKeys = [
        ForeignKey(entity = EntryEntity::class, parentColumns = ["id"], childColumns = ["entryId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = TagEntity::class, parentColumns = ["id"], childColumns = ["tagId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("entryId"), Index("tagId")]
)
data class EntryTagCrossRef(
    val entryId: String,
    val tagId: String
)

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val enabled: Boolean = true,
    val label: String = "Daily journal reminder",
    /** Allowed days of week as Calendar.DAY_OF_WEEK values 1=Sun..7=Sat, CSV. */
    val daysOfWeek: String = "1,2,3,4,5,6,7"
)

data class EntryWithDetails(
    @Embedded val entry: EntryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "entryId"
    )
    val attachments: List<AttachmentEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = EntryTagCrossRef::class,
            parentColumn = "entryId",
            entityColumn = "tagId"
        )
    )
    val tags: List<TagEntity>
)
