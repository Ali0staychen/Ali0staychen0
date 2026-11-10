package com.journey.replica.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class EntryDao_Impl implements EntryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<EntryEntity> __insertionAdapterOfEntryEntity;

  private final EntityInsertionAdapter<AttachmentEntity> __insertionAdapterOfAttachmentEntity;

  private final EntityInsertionAdapter<TagEntity> __insertionAdapterOfTagEntity;

  private final EntityInsertionAdapter<EntryTagCrossRef> __insertionAdapterOfEntryTagCrossRef;

  private final EntityDeletionOrUpdateAdapter<EntryEntity> __deletionAdapterOfEntryEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAttachment;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAttachmentsForEntry;

  private final SharedSQLiteStatement __preparedStmtOfClearTagsForEntry;

  public EntryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEntryEntity = new EntityInsertionAdapter<EntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `entries` (`id`,`title`,`text`,`dateMillis`,`timezone`,`address`,`latitude`,`longitude`,`weatherTempC`,`weatherDescription`,`weatherIcon`,`weatherPlace`,`sentiment`,`activitySteps`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EntryEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getText());
        statement.bindLong(4, entity.getDateMillis());
        statement.bindString(5, entity.getTimezone());
        if (entity.getAddress() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getAddress());
        }
        if (entity.getLatitude() == null) {
          statement.bindNull(7);
        } else {
          statement.bindDouble(7, entity.getLatitude());
        }
        if (entity.getLongitude() == null) {
          statement.bindNull(8);
        } else {
          statement.bindDouble(8, entity.getLongitude());
        }
        if (entity.getWeatherTempC() == null) {
          statement.bindNull(9);
        } else {
          statement.bindDouble(9, entity.getWeatherTempC());
        }
        if (entity.getWeatherDescription() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getWeatherDescription());
        }
        if (entity.getWeatherIcon() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getWeatherIcon());
        }
        if (entity.getWeatherPlace() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getWeatherPlace());
        }
        if (entity.getSentiment() == null) {
          statement.bindNull(13);
        } else {
          statement.bindDouble(13, entity.getSentiment());
        }
        if (entity.getActivitySteps() == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, entity.getActivitySteps());
        }
        statement.bindLong(15, entity.getCreatedAt());
        statement.bindLong(16, entity.getUpdatedAt());
      }
    };
    this.__insertionAdapterOfAttachmentEntity = new EntityInsertionAdapter<AttachmentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `attachments` (`id`,`entryId`,`uri`,`mimeType`,`displayName`,`createdAt`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AttachmentEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getEntryId());
        statement.bindString(3, entity.getUri());
        statement.bindString(4, entity.getMimeType());
        if (entity.getDisplayName() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getDisplayName());
        }
        statement.bindLong(6, entity.getCreatedAt());
      }
    };
    this.__insertionAdapterOfTagEntity = new EntityInsertionAdapter<TagEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `tags` (`id`,`name`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TagEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
      }
    };
    this.__insertionAdapterOfEntryTagCrossRef = new EntityInsertionAdapter<EntryTagCrossRef>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `entry_tags` (`entryId`,`tagId`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EntryTagCrossRef entity) {
        statement.bindString(1, entity.getEntryId());
        statement.bindString(2, entity.getTagId());
      }
    };
    this.__deletionAdapterOfEntryEntity = new EntityDeletionOrUpdateAdapter<EntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `entries` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EntryEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAttachment = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM attachments WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAttachmentsForEntry = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM attachments WHERE entryId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearTagsForEntry = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM entry_tags WHERE entryId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object upsertEntry(final EntryEntity entry, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfEntryEntity.insert(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAttachment(final AttachmentEntity attachment,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAttachmentEntity.insert(attachment);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertTag(final TagEntity tag, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTagEntity.insertAndReturnId(tag);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertEntryTag(final EntryTagCrossRef ref,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfEntryTagCrossRef.insert(ref);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteEntry(final EntryEntity entry, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfEntryEntity.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object saveEntryWithDetails(final EntryEntity entry,
      final List<AttachmentEntity> attachments, final List<TagEntity> tags,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> EntryDao.DefaultImpls.saveEntryWithDetails(EntryDao_Impl.this, entry, attachments, tags, __cont), $completion);
  }

  @Override
  public Object deleteAttachment(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAttachment.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAttachment.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAttachmentsForEntry(final String entryId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAttachmentsForEntry.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, entryId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAttachmentsForEntry.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearTagsForEntry(final String entryId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearTagsForEntry.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, entryId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearTagsForEntry.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<EntryWithDetails>> getAllEntries() {
    final String _sql = "SELECT * FROM entries ORDER BY dateMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"attachments", "entry_tags", "tags",
        "entries"}, new Callable<List<EntryWithDetails>>() {
      @Override
      @NonNull
      public List<EntryWithDetails> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
            final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
            final int _cursorIndexOfDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "dateMillis");
            final int _cursorIndexOfTimezone = CursorUtil.getColumnIndexOrThrow(_cursor, "timezone");
            final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
            final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
            final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
            final int _cursorIndexOfWeatherTempC = CursorUtil.getColumnIndexOrThrow(_cursor, "weatherTempC");
            final int _cursorIndexOfWeatherDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "weatherDescription");
            final int _cursorIndexOfWeatherIcon = CursorUtil.getColumnIndexOrThrow(_cursor, "weatherIcon");
            final int _cursorIndexOfWeatherPlace = CursorUtil.getColumnIndexOrThrow(_cursor, "weatherPlace");
            final int _cursorIndexOfSentiment = CursorUtil.getColumnIndexOrThrow(_cursor, "sentiment");
            final int _cursorIndexOfActivitySteps = CursorUtil.getColumnIndexOrThrow(_cursor, "activitySteps");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
            final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
            final ArrayMap<String, ArrayList<AttachmentEntity>> _collectionAttachments = new ArrayMap<String, ArrayList<AttachmentEntity>>();
            final ArrayMap<String, ArrayList<TagEntity>> _collectionTags = new ArrayMap<String, ArrayList<TagEntity>>();
            while (_cursor.moveToNext()) {
              final String _tmpKey;
              _tmpKey = _cursor.getString(_cursorIndexOfId);
              if (!_collectionAttachments.containsKey(_tmpKey)) {
                _collectionAttachments.put(_tmpKey, new ArrayList<AttachmentEntity>());
              }
              final String _tmpKey_1;
              _tmpKey_1 = _cursor.getString(_cursorIndexOfId);
              if (!_collectionTags.containsKey(_tmpKey_1)) {
                _collectionTags.put(_tmpKey_1, new ArrayList<TagEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipattachmentsAscomJourneyReplicaDataAttachmentEntity(_collectionAttachments);
            __fetchRelationshiptagsAscomJourneyReplicaDataTagEntity(_collectionTags);
            final List<EntryWithDetails> _result = new ArrayList<EntryWithDetails>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final EntryWithDetails _item;
              final EntryEntity _tmpEntry;
              final String _tmpId;
              _tmpId = _cursor.getString(_cursorIndexOfId);
              final String _tmpTitle;
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
              final String _tmpText;
              _tmpText = _cursor.getString(_cursorIndexOfText);
              final long _tmpDateMillis;
              _tmpDateMillis = _cursor.getLong(_cursorIndexOfDateMillis);
              final String _tmpTimezone;
              _tmpTimezone = _cursor.getString(_cursorIndexOfTimezone);
              final String _tmpAddress;
              if (_cursor.isNull(_cursorIndexOfAddress)) {
                _tmpAddress = null;
              } else {
                _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
              }
              final Double _tmpLatitude;
              if (_cursor.isNull(_cursorIndexOfLatitude)) {
                _tmpLatitude = null;
              } else {
                _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
              }
              final Double _tmpLongitude;
              if (_cursor.isNull(_cursorIndexOfLongitude)) {
                _tmpLongitude = null;
              } else {
                _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
              }
              final Double _tmpWeatherTempC;
              if (_cursor.isNull(_cursorIndexOfWeatherTempC)) {
                _tmpWeatherTempC = null;
              } else {
                _tmpWeatherTempC = _cursor.getDouble(_cursorIndexOfWeatherTempC);
              }
              final String _tmpWeatherDescription;
              if (_cursor.isNull(_cursorIndexOfWeatherDescription)) {
                _tmpWeatherDescription = null;
              } else {
                _tmpWeatherDescription = _cursor.getString(_cursorIndexOfWeatherDescription);
              }
              final String _tmpWeatherIcon;
              if (_cursor.isNull(_cursorIndexOfWeatherIcon)) {
                _tmpWeatherIcon = null;
              } else {
                _tmpWeatherIcon = _cursor.getString(_cursorIndexOfWeatherIcon);
              }
              final String _tmpWeatherPlace;
              if (_cursor.isNull(_cursorIndexOfWeatherPlace)) {
                _tmpWeatherPlace = null;
              } else {
                _tmpWeatherPlace = _cursor.getString(_cursorIndexOfWeatherPlace);
              }
              final Double _tmpSentiment;
              if (_cursor.isNull(_cursorIndexOfSentiment)) {
                _tmpSentiment = null;
              } else {
                _tmpSentiment = _cursor.getDouble(_cursorIndexOfSentiment);
              }
              final Integer _tmpActivitySteps;
              if (_cursor.isNull(_cursorIndexOfActivitySteps)) {
                _tmpActivitySteps = null;
              } else {
                _tmpActivitySteps = _cursor.getInt(_cursorIndexOfActivitySteps);
              }
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              final long _tmpUpdatedAt;
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
              _tmpEntry = new EntryEntity(_tmpId,_tmpTitle,_tmpText,_tmpDateMillis,_tmpTimezone,_tmpAddress,_tmpLatitude,_tmpLongitude,_tmpWeatherTempC,_tmpWeatherDescription,_tmpWeatherIcon,_tmpWeatherPlace,_tmpSentiment,_tmpActivitySteps,_tmpCreatedAt,_tmpUpdatedAt);
              final ArrayList<AttachmentEntity> _tmpAttachmentsCollection;
              final String _tmpKey_2;
              _tmpKey_2 = _cursor.getString(_cursorIndexOfId);
              _tmpAttachmentsCollection = _collectionAttachments.get(_tmpKey_2);
              final ArrayList<TagEntity> _tmpTagsCollection;
              final String _tmpKey_3;
              _tmpKey_3 = _cursor.getString(_cursorIndexOfId);
              _tmpTagsCollection = _collectionTags.get(_tmpKey_3);
              _item = new EntryWithDetails(_tmpEntry,_tmpAttachmentsCollection,_tmpTagsCollection);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<EntryWithDetails> getEntry(final String id) {
    final String _sql = "SELECT * FROM entries WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"attachments", "entry_tags", "tags",
        "entries"}, new Callable<EntryWithDetails>() {
      @Override
      @Nullable
      public EntryWithDetails call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
            final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
            final int _cursorIndexOfDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "dateMillis");
            final int _cursorIndexOfTimezone = CursorUtil.getColumnIndexOrThrow(_cursor, "timezone");
            final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
            final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
            final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
            final int _cursorIndexOfWeatherTempC = CursorUtil.getColumnIndexOrThrow(_cursor, "weatherTempC");
            final int _cursorIndexOfWeatherDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "weatherDescription");
            final int _cursorIndexOfWeatherIcon = CursorUtil.getColumnIndexOrThrow(_cursor, "weatherIcon");
            final int _cursorIndexOfWeatherPlace = CursorUtil.getColumnIndexOrThrow(_cursor, "weatherPlace");
            final int _cursorIndexOfSentiment = CursorUtil.getColumnIndexOrThrow(_cursor, "sentiment");
            final int _cursorIndexOfActivitySteps = CursorUtil.getColumnIndexOrThrow(_cursor, "activitySteps");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
            final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
            final ArrayMap<String, ArrayList<AttachmentEntity>> _collectionAttachments = new ArrayMap<String, ArrayList<AttachmentEntity>>();
            final ArrayMap<String, ArrayList<TagEntity>> _collectionTags = new ArrayMap<String, ArrayList<TagEntity>>();
            while (_cursor.moveToNext()) {
              final String _tmpKey;
              _tmpKey = _cursor.getString(_cursorIndexOfId);
              if (!_collectionAttachments.containsKey(_tmpKey)) {
                _collectionAttachments.put(_tmpKey, new ArrayList<AttachmentEntity>());
              }
              final String _tmpKey_1;
              _tmpKey_1 = _cursor.getString(_cursorIndexOfId);
              if (!_collectionTags.containsKey(_tmpKey_1)) {
                _collectionTags.put(_tmpKey_1, new ArrayList<TagEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipattachmentsAscomJourneyReplicaDataAttachmentEntity(_collectionAttachments);
            __fetchRelationshiptagsAscomJourneyReplicaDataTagEntity(_collectionTags);
            final EntryWithDetails _result;
            if (_cursor.moveToFirst()) {
              final EntryEntity _tmpEntry;
              final String _tmpId;
              _tmpId = _cursor.getString(_cursorIndexOfId);
              final String _tmpTitle;
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
              final String _tmpText;
              _tmpText = _cursor.getString(_cursorIndexOfText);
              final long _tmpDateMillis;
              _tmpDateMillis = _cursor.getLong(_cursorIndexOfDateMillis);
              final String _tmpTimezone;
              _tmpTimezone = _cursor.getString(_cursorIndexOfTimezone);
              final String _tmpAddress;
              if (_cursor.isNull(_cursorIndexOfAddress)) {
                _tmpAddress = null;
              } else {
                _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
              }
              final Double _tmpLatitude;
              if (_cursor.isNull(_cursorIndexOfLatitude)) {
                _tmpLatitude = null;
              } else {
                _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
              }
              final Double _tmpLongitude;
              if (_cursor.isNull(_cursorIndexOfLongitude)) {
                _tmpLongitude = null;
              } else {
                _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
              }
              final Double _tmpWeatherTempC;
              if (_cursor.isNull(_cursorIndexOfWeatherTempC)) {
                _tmpWeatherTempC = null;
              } else {
                _tmpWeatherTempC = _cursor.getDouble(_cursorIndexOfWeatherTempC);
              }
              final String _tmpWeatherDescription;
              if (_cursor.isNull(_cursorIndexOfWeatherDescription)) {
                _tmpWeatherDescription = null;
              } else {
                _tmpWeatherDescription = _cursor.getString(_cursorIndexOfWeatherDescription);
              }
              final String _tmpWeatherIcon;
              if (_cursor.isNull(_cursorIndexOfWeatherIcon)) {
                _tmpWeatherIcon = null;
              } else {
                _tmpWeatherIcon = _cursor.getString(_cursorIndexOfWeatherIcon);
              }
              final String _tmpWeatherPlace;
              if (_cursor.isNull(_cursorIndexOfWeatherPlace)) {
                _tmpWeatherPlace = null;
              } else {
                _tmpWeatherPlace = _cursor.getString(_cursorIndexOfWeatherPlace);
              }
              final Double _tmpSentiment;
              if (_cursor.isNull(_cursorIndexOfSentiment)) {
                _tmpSentiment = null;
              } else {
                _tmpSentiment = _cursor.getDouble(_cursorIndexOfSentiment);
              }
              final Integer _tmpActivitySteps;
              if (_cursor.isNull(_cursorIndexOfActivitySteps)) {
                _tmpActivitySteps = null;
              } else {
                _tmpActivitySteps = _cursor.getInt(_cursorIndexOfActivitySteps);
              }
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              final long _tmpUpdatedAt;
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
              _tmpEntry = new EntryEntity(_tmpId,_tmpTitle,_tmpText,_tmpDateMillis,_tmpTimezone,_tmpAddress,_tmpLatitude,_tmpLongitude,_tmpWeatherTempC,_tmpWeatherDescription,_tmpWeatherIcon,_tmpWeatherPlace,_tmpSentiment,_tmpActivitySteps,_tmpCreatedAt,_tmpUpdatedAt);
              final ArrayList<AttachmentEntity> _tmpAttachmentsCollection;
              final String _tmpKey_2;
              _tmpKey_2 = _cursor.getString(_cursorIndexOfId);
              _tmpAttachmentsCollection = _collectionAttachments.get(_tmpKey_2);
              final ArrayList<TagEntity> _tmpTagsCollection;
              final String _tmpKey_3;
              _tmpKey_3 = _cursor.getString(_cursorIndexOfId);
              _tmpTagsCollection = _collectionTags.get(_tmpKey_3);
              _result = new EntryWithDetails(_tmpEntry,_tmpAttachmentsCollection,_tmpTagsCollection);
            } else {
              _result = null;
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<EntryWithDetails>> getEntriesWithLocation() {
    final String _sql = "SELECT * FROM entries WHERE latitude IS NOT NULL AND longitude IS NOT NULL ORDER BY dateMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"attachments", "entry_tags", "tags",
        "entries"}, new Callable<List<EntryWithDetails>>() {
      @Override
      @NonNull
      public List<EntryWithDetails> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
            final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
            final int _cursorIndexOfDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "dateMillis");
            final int _cursorIndexOfTimezone = CursorUtil.getColumnIndexOrThrow(_cursor, "timezone");
            final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
            final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
            final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
            final int _cursorIndexOfWeatherTempC = CursorUtil.getColumnIndexOrThrow(_cursor, "weatherTempC");
            final int _cursorIndexOfWeatherDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "weatherDescription");
            final int _cursorIndexOfWeatherIcon = CursorUtil.getColumnIndexOrThrow(_cursor, "weatherIcon");
            final int _cursorIndexOfWeatherPlace = CursorUtil.getColumnIndexOrThrow(_cursor, "weatherPlace");
            final int _cursorIndexOfSentiment = CursorUtil.getColumnIndexOrThrow(_cursor, "sentiment");
            final int _cursorIndexOfActivitySteps = CursorUtil.getColumnIndexOrThrow(_cursor, "activitySteps");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
            final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
            final ArrayMap<String, ArrayList<AttachmentEntity>> _collectionAttachments = new ArrayMap<String, ArrayList<AttachmentEntity>>();
            final ArrayMap<String, ArrayList<TagEntity>> _collectionTags = new ArrayMap<String, ArrayList<TagEntity>>();
            while (_cursor.moveToNext()) {
              final String _tmpKey;
              _tmpKey = _cursor.getString(_cursorIndexOfId);
              if (!_collectionAttachments.containsKey(_tmpKey)) {
                _collectionAttachments.put(_tmpKey, new ArrayList<AttachmentEntity>());
              }
              final String _tmpKey_1;
              _tmpKey_1 = _cursor.getString(_cursorIndexOfId);
              if (!_collectionTags.containsKey(_tmpKey_1)) {
                _collectionTags.put(_tmpKey_1, new ArrayList<TagEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipattachmentsAscomJourneyReplicaDataAttachmentEntity(_collectionAttachments);
            __fetchRelationshiptagsAscomJourneyReplicaDataTagEntity(_collectionTags);
            final List<EntryWithDetails> _result = new ArrayList<EntryWithDetails>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final EntryWithDetails _item;
              final EntryEntity _tmpEntry;
              final String _tmpId;
              _tmpId = _cursor.getString(_cursorIndexOfId);
              final String _tmpTitle;
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
              final String _tmpText;
              _tmpText = _cursor.getString(_cursorIndexOfText);
              final long _tmpDateMillis;
              _tmpDateMillis = _cursor.getLong(_cursorIndexOfDateMillis);
              final String _tmpTimezone;
              _tmpTimezone = _cursor.getString(_cursorIndexOfTimezone);
              final String _tmpAddress;
              if (_cursor.isNull(_cursorIndexOfAddress)) {
                _tmpAddress = null;
              } else {
                _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
              }
              final Double _tmpLatitude;
              if (_cursor.isNull(_cursorIndexOfLatitude)) {
                _tmpLatitude = null;
              } else {
                _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
              }
              final Double _tmpLongitude;
              if (_cursor.isNull(_cursorIndexOfLongitude)) {
                _tmpLongitude = null;
              } else {
                _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
              }
              final Double _tmpWeatherTempC;
              if (_cursor.isNull(_cursorIndexOfWeatherTempC)) {
                _tmpWeatherTempC = null;
              } else {
                _tmpWeatherTempC = _cursor.getDouble(_cursorIndexOfWeatherTempC);
              }
              final String _tmpWeatherDescription;
              if (_cursor.isNull(_cursorIndexOfWeatherDescription)) {
                _tmpWeatherDescription = null;
              } else {
                _tmpWeatherDescription = _cursor.getString(_cursorIndexOfWeatherDescription);
              }
              final String _tmpWeatherIcon;
              if (_cursor.isNull(_cursorIndexOfWeatherIcon)) {
                _tmpWeatherIcon = null;
              } else {
                _tmpWeatherIcon = _cursor.getString(_cursorIndexOfWeatherIcon);
              }
              final String _tmpWeatherPlace;
              if (_cursor.isNull(_cursorIndexOfWeatherPlace)) {
                _tmpWeatherPlace = null;
              } else {
                _tmpWeatherPlace = _cursor.getString(_cursorIndexOfWeatherPlace);
              }
              final Double _tmpSentiment;
              if (_cursor.isNull(_cursorIndexOfSentiment)) {
                _tmpSentiment = null;
              } else {
                _tmpSentiment = _cursor.getDouble(_cursorIndexOfSentiment);
              }
              final Integer _tmpActivitySteps;
              if (_cursor.isNull(_cursorIndexOfActivitySteps)) {
                _tmpActivitySteps = null;
              } else {
                _tmpActivitySteps = _cursor.getInt(_cursorIndexOfActivitySteps);
              }
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              final long _tmpUpdatedAt;
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
              _tmpEntry = new EntryEntity(_tmpId,_tmpTitle,_tmpText,_tmpDateMillis,_tmpTimezone,_tmpAddress,_tmpLatitude,_tmpLongitude,_tmpWeatherTempC,_tmpWeatherDescription,_tmpWeatherIcon,_tmpWeatherPlace,_tmpSentiment,_tmpActivitySteps,_tmpCreatedAt,_tmpUpdatedAt);
              final ArrayList<AttachmentEntity> _tmpAttachmentsCollection;
              final String _tmpKey_2;
              _tmpKey_2 = _cursor.getString(_cursorIndexOfId);
              _tmpAttachmentsCollection = _collectionAttachments.get(_tmpKey_2);
              final ArrayList<TagEntity> _tmpTagsCollection;
              final String _tmpKey_3;
              _tmpKey_3 = _cursor.getString(_cursorIndexOfId);
              _tmpTagsCollection = _collectionTags.get(_tmpKey_3);
              _item = new EntryWithDetails(_tmpEntry,_tmpAttachmentsCollection,_tmpTagsCollection);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<EntryWithDetails>> getEntriesInRange(final long start, final long end) {
    final String _sql = "SELECT * FROM entries WHERE dateMillis BETWEEN ? AND ? ORDER BY dateMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, start);
    _argIndex = 2;
    _statement.bindLong(_argIndex, end);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"attachments", "entry_tags", "tags",
        "entries"}, new Callable<List<EntryWithDetails>>() {
      @Override
      @NonNull
      public List<EntryWithDetails> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
            final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
            final int _cursorIndexOfDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "dateMillis");
            final int _cursorIndexOfTimezone = CursorUtil.getColumnIndexOrThrow(_cursor, "timezone");
            final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
            final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
            final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
            final int _cursorIndexOfWeatherTempC = CursorUtil.getColumnIndexOrThrow(_cursor, "weatherTempC");
            final int _cursorIndexOfWeatherDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "weatherDescription");
            final int _cursorIndexOfWeatherIcon = CursorUtil.getColumnIndexOrThrow(_cursor, "weatherIcon");
            final int _cursorIndexOfWeatherPlace = CursorUtil.getColumnIndexOrThrow(_cursor, "weatherPlace");
            final int _cursorIndexOfSentiment = CursorUtil.getColumnIndexOrThrow(_cursor, "sentiment");
            final int _cursorIndexOfActivitySteps = CursorUtil.getColumnIndexOrThrow(_cursor, "activitySteps");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
            final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
            final ArrayMap<String, ArrayList<AttachmentEntity>> _collectionAttachments = new ArrayMap<String, ArrayList<AttachmentEntity>>();
            final ArrayMap<String, ArrayList<TagEntity>> _collectionTags = new ArrayMap<String, ArrayList<TagEntity>>();
            while (_cursor.moveToNext()) {
              final String _tmpKey;
              _tmpKey = _cursor.getString(_cursorIndexOfId);
              if (!_collectionAttachments.containsKey(_tmpKey)) {
                _collectionAttachments.put(_tmpKey, new ArrayList<AttachmentEntity>());
              }
              final String _tmpKey_1;
              _tmpKey_1 = _cursor.getString(_cursorIndexOfId);
              if (!_collectionTags.containsKey(_tmpKey_1)) {
                _collectionTags.put(_tmpKey_1, new ArrayList<TagEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipattachmentsAscomJourneyReplicaDataAttachmentEntity(_collectionAttachments);
            __fetchRelationshiptagsAscomJourneyReplicaDataTagEntity(_collectionTags);
            final List<EntryWithDetails> _result = new ArrayList<EntryWithDetails>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final EntryWithDetails _item;
              final EntryEntity _tmpEntry;
              final String _tmpId;
              _tmpId = _cursor.getString(_cursorIndexOfId);
              final String _tmpTitle;
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
              final String _tmpText;
              _tmpText = _cursor.getString(_cursorIndexOfText);
              final long _tmpDateMillis;
              _tmpDateMillis = _cursor.getLong(_cursorIndexOfDateMillis);
              final String _tmpTimezone;
              _tmpTimezone = _cursor.getString(_cursorIndexOfTimezone);
              final String _tmpAddress;
              if (_cursor.isNull(_cursorIndexOfAddress)) {
                _tmpAddress = null;
              } else {
                _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
              }
              final Double _tmpLatitude;
              if (_cursor.isNull(_cursorIndexOfLatitude)) {
                _tmpLatitude = null;
              } else {
                _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
              }
              final Double _tmpLongitude;
              if (_cursor.isNull(_cursorIndexOfLongitude)) {
                _tmpLongitude = null;
              } else {
                _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
              }
              final Double _tmpWeatherTempC;
              if (_cursor.isNull(_cursorIndexOfWeatherTempC)) {
                _tmpWeatherTempC = null;
              } else {
                _tmpWeatherTempC = _cursor.getDouble(_cursorIndexOfWeatherTempC);
              }
              final String _tmpWeatherDescription;
              if (_cursor.isNull(_cursorIndexOfWeatherDescription)) {
                _tmpWeatherDescription = null;
              } else {
                _tmpWeatherDescription = _cursor.getString(_cursorIndexOfWeatherDescription);
              }
              final String _tmpWeatherIcon;
              if (_cursor.isNull(_cursorIndexOfWeatherIcon)) {
                _tmpWeatherIcon = null;
              } else {
                _tmpWeatherIcon = _cursor.getString(_cursorIndexOfWeatherIcon);
              }
              final String _tmpWeatherPlace;
              if (_cursor.isNull(_cursorIndexOfWeatherPlace)) {
                _tmpWeatherPlace = null;
              } else {
                _tmpWeatherPlace = _cursor.getString(_cursorIndexOfWeatherPlace);
              }
              final Double _tmpSentiment;
              if (_cursor.isNull(_cursorIndexOfSentiment)) {
                _tmpSentiment = null;
              } else {
                _tmpSentiment = _cursor.getDouble(_cursorIndexOfSentiment);
              }
              final Integer _tmpActivitySteps;
              if (_cursor.isNull(_cursorIndexOfActivitySteps)) {
                _tmpActivitySteps = null;
              } else {
                _tmpActivitySteps = _cursor.getInt(_cursorIndexOfActivitySteps);
              }
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              final long _tmpUpdatedAt;
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
              _tmpEntry = new EntryEntity(_tmpId,_tmpTitle,_tmpText,_tmpDateMillis,_tmpTimezone,_tmpAddress,_tmpLatitude,_tmpLongitude,_tmpWeatherTempC,_tmpWeatherDescription,_tmpWeatherIcon,_tmpWeatherPlace,_tmpSentiment,_tmpActivitySteps,_tmpCreatedAt,_tmpUpdatedAt);
              final ArrayList<AttachmentEntity> _tmpAttachmentsCollection;
              final String _tmpKey_2;
              _tmpKey_2 = _cursor.getString(_cursorIndexOfId);
              _tmpAttachmentsCollection = _collectionAttachments.get(_tmpKey_2);
              final ArrayList<TagEntity> _tmpTagsCollection;
              final String _tmpKey_3;
              _tmpKey_3 = _cursor.getString(_cursorIndexOfId);
              _tmpTagsCollection = _collectionTags.get(_tmpKey_3);
              _item = new EntryWithDetails(_tmpEntry,_tmpAttachmentsCollection,_tmpTagsCollection);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<EntryWithDetails>> getEntriesByMood(final double mood) {
    final String _sql = "SELECT * FROM entries WHERE sentiment = ? ORDER BY dateMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindDouble(_argIndex, mood);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"attachments", "entry_tags", "tags",
        "entries"}, new Callable<List<EntryWithDetails>>() {
      @Override
      @NonNull
      public List<EntryWithDetails> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
            final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
            final int _cursorIndexOfDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "dateMillis");
            final int _cursorIndexOfTimezone = CursorUtil.getColumnIndexOrThrow(_cursor, "timezone");
            final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
            final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
            final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
            final int _cursorIndexOfWeatherTempC = CursorUtil.getColumnIndexOrThrow(_cursor, "weatherTempC");
            final int _cursorIndexOfWeatherDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "weatherDescription");
            final int _cursorIndexOfWeatherIcon = CursorUtil.getColumnIndexOrThrow(_cursor, "weatherIcon");
            final int _cursorIndexOfWeatherPlace = CursorUtil.getColumnIndexOrThrow(_cursor, "weatherPlace");
            final int _cursorIndexOfSentiment = CursorUtil.getColumnIndexOrThrow(_cursor, "sentiment");
            final int _cursorIndexOfActivitySteps = CursorUtil.getColumnIndexOrThrow(_cursor, "activitySteps");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
            final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
            final ArrayMap<String, ArrayList<AttachmentEntity>> _collectionAttachments = new ArrayMap<String, ArrayList<AttachmentEntity>>();
            final ArrayMap<String, ArrayList<TagEntity>> _collectionTags = new ArrayMap<String, ArrayList<TagEntity>>();
            while (_cursor.moveToNext()) {
              final String _tmpKey;
              _tmpKey = _cursor.getString(_cursorIndexOfId);
              if (!_collectionAttachments.containsKey(_tmpKey)) {
                _collectionAttachments.put(_tmpKey, new ArrayList<AttachmentEntity>());
              }
              final String _tmpKey_1;
              _tmpKey_1 = _cursor.getString(_cursorIndexOfId);
              if (!_collectionTags.containsKey(_tmpKey_1)) {
                _collectionTags.put(_tmpKey_1, new ArrayList<TagEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipattachmentsAscomJourneyReplicaDataAttachmentEntity(_collectionAttachments);
            __fetchRelationshiptagsAscomJourneyReplicaDataTagEntity(_collectionTags);
            final List<EntryWithDetails> _result = new ArrayList<EntryWithDetails>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final EntryWithDetails _item;
              final EntryEntity _tmpEntry;
              final String _tmpId;
              _tmpId = _cursor.getString(_cursorIndexOfId);
              final String _tmpTitle;
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
              final String _tmpText;
              _tmpText = _cursor.getString(_cursorIndexOfText);
              final long _tmpDateMillis;
              _tmpDateMillis = _cursor.getLong(_cursorIndexOfDateMillis);
              final String _tmpTimezone;
              _tmpTimezone = _cursor.getString(_cursorIndexOfTimezone);
              final String _tmpAddress;
              if (_cursor.isNull(_cursorIndexOfAddress)) {
                _tmpAddress = null;
              } else {
                _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
              }
              final Double _tmpLatitude;
              if (_cursor.isNull(_cursorIndexOfLatitude)) {
                _tmpLatitude = null;
              } else {
                _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
              }
              final Double _tmpLongitude;
              if (_cursor.isNull(_cursorIndexOfLongitude)) {
                _tmpLongitude = null;
              } else {
                _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
              }
              final Double _tmpWeatherTempC;
              if (_cursor.isNull(_cursorIndexOfWeatherTempC)) {
                _tmpWeatherTempC = null;
              } else {
                _tmpWeatherTempC = _cursor.getDouble(_cursorIndexOfWeatherTempC);
              }
              final String _tmpWeatherDescription;
              if (_cursor.isNull(_cursorIndexOfWeatherDescription)) {
                _tmpWeatherDescription = null;
              } else {
                _tmpWeatherDescription = _cursor.getString(_cursorIndexOfWeatherDescription);
              }
              final String _tmpWeatherIcon;
              if (_cursor.isNull(_cursorIndexOfWeatherIcon)) {
                _tmpWeatherIcon = null;
              } else {
                _tmpWeatherIcon = _cursor.getString(_cursorIndexOfWeatherIcon);
              }
              final String _tmpWeatherPlace;
              if (_cursor.isNull(_cursorIndexOfWeatherPlace)) {
                _tmpWeatherPlace = null;
              } else {
                _tmpWeatherPlace = _cursor.getString(_cursorIndexOfWeatherPlace);
              }
              final Double _tmpSentiment;
              if (_cursor.isNull(_cursorIndexOfSentiment)) {
                _tmpSentiment = null;
              } else {
                _tmpSentiment = _cursor.getDouble(_cursorIndexOfSentiment);
              }
              final Integer _tmpActivitySteps;
              if (_cursor.isNull(_cursorIndexOfActivitySteps)) {
                _tmpActivitySteps = null;
              } else {
                _tmpActivitySteps = _cursor.getInt(_cursorIndexOfActivitySteps);
              }
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              final long _tmpUpdatedAt;
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
              _tmpEntry = new EntryEntity(_tmpId,_tmpTitle,_tmpText,_tmpDateMillis,_tmpTimezone,_tmpAddress,_tmpLatitude,_tmpLongitude,_tmpWeatherTempC,_tmpWeatherDescription,_tmpWeatherIcon,_tmpWeatherPlace,_tmpSentiment,_tmpActivitySteps,_tmpCreatedAt,_tmpUpdatedAt);
              final ArrayList<AttachmentEntity> _tmpAttachmentsCollection;
              final String _tmpKey_2;
              _tmpKey_2 = _cursor.getString(_cursorIndexOfId);
              _tmpAttachmentsCollection = _collectionAttachments.get(_tmpKey_2);
              final ArrayList<TagEntity> _tmpTagsCollection;
              final String _tmpKey_3;
              _tmpKey_3 = _cursor.getString(_cursorIndexOfId);
              _tmpTagsCollection = _collectionTags.get(_tmpKey_3);
              _item = new EntryWithDetails(_tmpEntry,_tmpAttachmentsCollection,_tmpTagsCollection);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<TagEntity>> getAllTags() {
    final String _sql = "SELECT * FROM tags ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tags"}, new Callable<List<TagEntity>>() {
      @Override
      @NonNull
      public List<TagEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final List<TagEntity> _result = new ArrayList<TagEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TagEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            _item = new TagEntity(_tmpId,_tmpName);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getTagByName(final String name, final Continuation<? super TagEntity> $completion) {
    final String _sql = "SELECT * FROM tags WHERE name = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, name);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TagEntity>() {
      @Override
      @Nullable
      public TagEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final TagEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            _result = new TagEntity(_tmpId,_tmpName);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshipattachmentsAscomJourneyReplicaDataAttachmentEntity(
      @NonNull final ArrayMap<String, ArrayList<AttachmentEntity>> _map) {
    final Set<String> __mapKeySet = _map.keySet();
    if (__mapKeySet.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchArrayMap(_map, true, (map) -> {
        __fetchRelationshipattachmentsAscomJourneyReplicaDataAttachmentEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`entryId`,`uri`,`mimeType`,`displayName`,`createdAt` FROM `attachments` WHERE `entryId` IN (");
    final int _inputSize = __mapKeySet.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (String _item : __mapKeySet) {
      _stmt.bindString(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "entryId");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfEntryId = 1;
      final int _cursorIndexOfUri = 2;
      final int _cursorIndexOfMimeType = 3;
      final int _cursorIndexOfDisplayName = 4;
      final int _cursorIndexOfCreatedAt = 5;
      while (_cursor.moveToNext()) {
        final String _tmpKey;
        _tmpKey = _cursor.getString(_itemKeyIndex);
        final ArrayList<AttachmentEntity> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final AttachmentEntity _item_1;
          final String _tmpId;
          _tmpId = _cursor.getString(_cursorIndexOfId);
          final String _tmpEntryId;
          _tmpEntryId = _cursor.getString(_cursorIndexOfEntryId);
          final String _tmpUri;
          _tmpUri = _cursor.getString(_cursorIndexOfUri);
          final String _tmpMimeType;
          _tmpMimeType = _cursor.getString(_cursorIndexOfMimeType);
          final String _tmpDisplayName;
          if (_cursor.isNull(_cursorIndexOfDisplayName)) {
            _tmpDisplayName = null;
          } else {
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
          _item_1 = new AttachmentEntity(_tmpId,_tmpEntryId,_tmpUri,_tmpMimeType,_tmpDisplayName,_tmpCreatedAt);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }

  private void __fetchRelationshiptagsAscomJourneyReplicaDataTagEntity(
      @NonNull final ArrayMap<String, ArrayList<TagEntity>> _map) {
    final Set<String> __mapKeySet = _map.keySet();
    if (__mapKeySet.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchArrayMap(_map, true, (map) -> {
        __fetchRelationshiptagsAscomJourneyReplicaDataTagEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `tags`.`id` AS `id`,`tags`.`name` AS `name`,_junction.`entryId` FROM `entry_tags` AS _junction INNER JOIN `tags` ON (_junction.`tagId` = `tags`.`id`) WHERE _junction.`entryId` IN (");
    final int _inputSize = __mapKeySet.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (String _item : __mapKeySet) {
      _stmt.bindString(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      // _junction.entryId;
      final int _itemKeyIndex = 2;
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfName = 1;
      while (_cursor.moveToNext()) {
        final String _tmpKey;
        _tmpKey = _cursor.getString(_itemKeyIndex);
        final ArrayList<TagEntity> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final TagEntity _item_1;
          final String _tmpId;
          _tmpId = _cursor.getString(_cursorIndexOfId);
          final String _tmpName;
          _tmpName = _cursor.getString(_cursorIndexOfName);
          _item_1 = new TagEntity(_tmpId,_tmpName);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
