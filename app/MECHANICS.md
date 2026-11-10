# Mechanics (decompiled/read from original base.apk in this session)

**Original target opened:** `C:\Users\ali\Downloads\base.apk`  
`com.journey.app` 5.6.2-play (versionCode 758, minSdk 26, targetSdk 35)  
SHA256: `c2029c5ebdf3c6383b4e15e6af7b72bf5cc754d14b6873e661a50ea07aa3f7bf`  
Craft target id: `t_c2029c5ebdf3` (opened via `core_target_open`)

**Decompilation method note:** The original is an Android APK (DEX bytecode), not native machine code. `native_strings_find` / `native_function_pseudocode` are not applicable to APK targets in this environment, so the equivalent DEX-level decompilation/read was performed with `android_classes_find`, `android_class_show`, and `android_method_show` (smali-like Dalvik bytecode listings) on the opened original. Each result was awaited and recorded as evidence with DEX method/class addresses (`L...;->...`), dex file, and evidence IDs below.

## Reminder mechanics (observed)

### `com.journey.app.ReminderReceiver` (classes3.dex)
Class evidence context from search/open: `ev_83851f05718a10854ac65d2bafa44766b3c06882e6635ef2ee7950dc194a7b27`.

| Function address (DEX) | Evidence | Real values / formulas / rules (from decompilation) |
|---|---|---|
| `Lcom/journey/app/ReminderReceiver;->onReceive(Landroid/content/Context;Landroid/content/Intent;)V` | `ev_82b1cd3acc2b087bbc1dea6c55504a88505415c575d6decca0a894d5e7c26b8b` (47 insns) | Gets `Calendar.getInstance()` and reads `Calendar.get(7)` = `DAY_OF_WEEK` (1=Sun..7=Sat), converts to `String.valueOf(day)`. Loads allowed days via `Ls8/T;->z0(context)` (`TreeSet`). Logs `"Alarm: <day> | <joined days>"` with tag `"JourneyReminderReceiver"` using `TextUtils.join(", ", set)` and `Log.d`. If `allowedSet.contains(todayString)` then calls private `e(context)`; otherwise returns. |
| `Lcom/journey/app/ReminderReceiver;->e(Landroid/content/Context;)V` | `ev_ea931a121d9766560deccd9bdca715c91e66367b16a02b65ca38d6989eca9e66` (189 insns) | Gets `NotificationManager` (`"notification"`). If `Ls8/T;->n1()` true and `!areNotificationsEnabled()` logs `"Reminder: Notification blocked"` (tag `"JourneyReminderReceiver"`) and returns. If `Ls8/T;->p1()` creates `NotificationChannel("com.journey.app.reminder", getString(res LN7/X4;->Wa), 4 /*IMPORTANCE_HIGH*/)` and calls `createNotificationChannel`. Builds `androidx.core.app` notification with content intents from `c(ctx)`/`d(ctx)`, title `getString(LN7/X4;->Wa)`, text `getString(LN7/X4;->K5)`, `setAutoCancel(true)`, color `getColor(LN7/O4;->a)`, small icon `LN7/Q4;->T3`, action icon `LN7/Q4;->a4` + text `LN7/X4;->I5`, priority/visibility `2`, BigTextStyle text `LN7/X4;->K5`, wearable/extra action via `b(ctx)` and optional `a(ctx, pendingIntent)` when `Ls8/T;->o1()`. Posts with `notify(notificationId = LN7/X4;->D, notification)`. |

### `s8.a` reminder scheduler (classes3.dex)

| Function address (DEX) | Evidence | Real values / formulas / rules (from decompilation) |
|---|---|---|
| `Ls8/a;->j(Landroid/content/Context;Ljava/lang/Boolean;)V` | `ev_dcaab626f885748f9bfead56774b10d37fd13e79f993543e12a35e682d35bde2` (105 insns, 1 try block) | Gets `AlarmManager` via `getSystemService("alarm")`; null => return. Creates `Intent(context, Lcom/journey/app/ReminderReceiver;)` and `PendingIntent` via `Ls8/q0;->b(context, 0, intent, 0, 0)`. **Enabled (`Boolean` true):** `Calendar.getInstance()`; `set(11 /*HOUR_OF_DAY*/, Ls8/T;->A0(context))`; `set(12 /*MINUTE*/, Ls8/T;->B0(context))`; `set(13 /*SECOND*/, 0)`. If `triggerAtMillis < System.currentTimeMillis()` add `0x5265c00L` (86,400,000 ms = 1 day). Calls `AlarmManager.setRepeating(1 /*RTC_WAKEUP*/, triggerAtMillis, 0x5265c00L, pendingIntent)` inside try/catch (`Throwable.printStackTrace()` on failure). Logs `"Reminder Set HR  <hour> MIN <minute>"` with tag `"AlarmHelper"`. **Disabled:** `alarmManager.cancel(pendingIntent)` then `pendingIntent.cancel()`. |

### `s8.T` reminder preference helpers (classes3.dex)

| Function address (DEX) | Evidence | Real values / formulas / rules (from decompilation) |
|---|---|---|
| `Ls8/T;->A0(Landroid/content/Context;)I` | `ev_4ec0d5779a4f26f2d8b2d456e81621b18bc86681ba194b31015b535d533bcf7f` (12 insns) | `PreferenceManager.getDefaultSharedPreferences(context).getInt("reminder_hour_2", 0x14)` → **default hour = 20**. |
| `Ls8/T;->B0(Landroid/content/Context;)I` | `ev_c65f928f09988e3efcb8d4b23427cb2db741ab76dc13c650ddc4fc57c90d7de6` (12 insns) | `getInt("reminder_min_2", 0)` → **default minute = 0**. |
| `Ls8/T;->z0(Landroid/content/Context;)Ljava/util/TreeSet;` | `ev_04af18f60bc3e3e8d2d88a62ee2229f968509c31041043e25acffb77ef8f26ec` (33 insns) | Builds default `TreeSet<String>` with loop `i = 1..7` adding `String.valueOf(i)` (`"1"`..`"7"`, `Calendar.DAY_OF_WEEK`: Sun..Sat). Reads stored set `getStringSet("reminder_day_2", defaultSet)` and returns `new TreeSet(storedSet)` (sorted). |

**Observed reminder constants:** channel id `"com.journey.app.reminder"`, importance `4` (IMPORTANCE_HIGH), alarm type `1` (RTC_WAKEUP), repeat interval/day roll-forward `86,400,000 ms` (`0x5265c00`), `DAY_OF_WEEK` numeric string gating, default `20:00`, default days `1-7`.

## Entry / journal data model (observed)

### `com.journey.app.mvvm.models.entity.JournalV2` (classes3.dex, 26 fields)
Evidence: `ev_e1c2caf9c92b07052fcf2fd1f22c5e0a702c00cdfa2cf67e8ba99a8b78d38ad1`  
Key fields/types: `JId:long`, `LinkedAccountId/String`, `ExternalId/String`, `Alias/String`, `DateOfJournal:String`, `DateModified:String`, `Timezone:String`, `FormatType:String`, `Synced:Integer`, `Version:Long`, `Text:String`, `PreviewText:String`, `Address:String`, `Lat:Double`, `Lng:Double`, `Activity:Integer`, `Sentiment:Double`, `WeatherIcon:String`, `WeatherDescription:String`, `WeatherPlace:String`, `WeatherId:Integer`, `WeatherDegreeC:Double`, `Favourite:Boolean`, `MusicTitle:String`, `MusicArtist:String`.

### `com.journey.app.mvvm.models.entity.Journal` (classes3.dex, 27 fields)
Evidence: `ev_5941d45d4aa11b0f59806799f6056a4bcea7d00d8f41c624e7da2b8a061856f6`  
Key fields/types include: `JId:String`, `LinkedAccountId:String`, `DateOfJournal:Long`, `DateModified:Long`, `Synced:Integer`, `Type:String`, `Text:String`, `PreviewText:String`, `Label:String`, `Lat:Double`, `Lon:Double` (note field name), `Address:String`, `Mood:Integer`, `WeatherId:Integer`, `Sentiment:Double`, `WeatherIcon/Description/Place:String`, `MusicTitle/MusicArtist:String`, `WeatherDegreeC:Double`, `Favourite:Boolean`, `Folder:String`, `Timezone:String`.

## Sync/API response schemas (observed, DEX addresses/classes)

- `com.journey.app.mvvm.service.SyncApiGson$PartialEntryResponseGson` (classes3.dex) — evidence `ev_39fe2411df96d18a30d6daf56b0560ba9803c51649f053f6dd8c08528fbb37d5`  
  Fields include: `id`, `activity:Integer`, `address:String`, `dateOfJournal:String`, `encrypted:Boolean`, `favourite:Boolean`, `files:List`, `location:EntryLocationResponseGson`, `music`, `previewText:String`, `sentiment:Double`, `tags:List`, `text:String`, `timezone:String`, `type:String`, `updatedAt:String`, `version:Long`, `weather:EntryWeatherResponseGson`.
- `com.journey.app.mvvm.service.SyncApiGson$EntryLocationResponseGson` (classes3.dex) — evidence `ev_90e15cf7bba32e5bd8300d9aca9185d0510d5b02cddf2b9e2f4ecfc9f3954c1e`  
  Fields: `lat:Double`, `lng:Double`.
- `com.journey.app.mvvm.service.SyncApiGson$EntryWeatherResponseGson` (classes3.dex) — evidence `ev_c86e612ea0627c66e07e88ca24e28c4161a885a1a888dea1991249455e2aa0a3`  
  Fields: `degreeC:Double`, `description:String`, `icon:String`, `id:Integer`, `place:String`.
- `com.journey.app.mvvm.service.SyncApiGson$EntryFileResponseGson` (classes3.dex) — evidence `ev_dde58e5ad64d540db9a729d52c194222f9dc976f710c0329052125893f1c44f1`  
  Fields: `id`, `alias`, `ext`, `mimeType`, `createdAt`, `fileMetadataDate`.
- Tags/attachments present in model graph: `tags:List` and `files:List` on partial entry response; Journal V2 relation classes for tag word bags/media files exist in app package (`JournalWithMediaFilesAndTagWordBagsV2`, etc., observed in class search).

## Replica implementation alignment (`app/`, this project)

- **Reminders** (`reminders/ReminderScheduler.kt`, `reminders/ReminderReceiver.kt`, `JourneyReplicaApp.kt`): uses observed real values/rules — `DAY_INTERVAL_MS=86_400_000L`, `RTC_WAKEUP` with `setRepeating(...)`, `HOUR_OF_DAY`/`MINUTE`/`SECOND=0`, roll-forward by one day when trigger ≤ now, cancel + pendingIntent.cancel on disable, channel id `com.journey.app.reminder` with `IMPORTANCE_HIGH` (4), day gating by `Calendar.DAY_OF_WEEK` string values `1..7` (CSV), default hour/minute `20:00`, default days `1-7`. (Replica extension: multiple per-reminder records instead of single global SharedPreferences keys.)
- **Entry storage fields** (`data/Models.kt`): stores `latitude/longitude: Double?`, `address`, `weatherTempC/weatherDescription/weatherIcon/weatherPlace`, `sentiment: Double?` (mood), `activitySteps: Int?` (activity/motion), date/timezone, and attachment/tag relations — aligned to observed types in Journal/JournalV2 and SyncApiGson schemas.
- **Weather** (`util/WeatherService.kt`): fetches current weather from Open-Meteo (`current=temperature_2m,weather_code`) and maps to `tempC`, text description, and icon/code fields; stored on entries (real observed schema uses `degreeC/description/icon/id/place`).
- **Location** (`util/LocationUtils.kt`): fused location + reverse geocoding to capture coordinates/address for entries (aligned to observed `lat/lng`/`address` fields).
- **Mood/sentiment, activity, tags, calendar, rich attachments (photos/videos/audio/recordings/signatures)**: implemented in app UI/data flow per project behavior (see `ui/`, `data/`, `util/AudioRecorder.kt`, `ui/SignatureDialog.kt`). Map browsing uses entries with recorded coordinates.

## Unknowns / estimates (not fully recovered from original)

- Exact in-app mood UI scale mapping/semantics (original stores `Sentiment`/`Mood` as numeric fields; replica uses discrete 1.0–5.0 sentiment choices) — **estimated**.
- Exact semantic meaning/range of `Activity` integer beyond generic activity/motion (replica treats as step count) — **estimated**.
- Exact original weather provider/API and icon-id mapping logic in runtime fetch code (schemas observed; network implementation not fully recovered here) — **partially observed**.
- Signature drawing/storage format details and specific notification resource contents referenced by resource IDs — **estimated/partially observed**.
- Full behavior of other `s8.T`/`s8.a` helpers beyond methods read above — **partially observed**.
- No native-function pseudocode available because target is DEX/APK; analysis is bytecode/smali-level for original classes/methods.
