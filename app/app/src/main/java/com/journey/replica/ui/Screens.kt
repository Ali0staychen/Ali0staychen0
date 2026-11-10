package com.journey.replica.ui

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.journey.replica.data.*
import com.journey.replica.util.AudioRecorder
import com.journey.replica.util.LocationUtils
import com.journey.replica.util.WeatherService
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.*
import java.util.UUID

fun openAttachment(context: Context, uri: Uri, mime: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mime.ifBlank { "*/*" })
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Open attachment"))
    } catch (_: Exception) { }
}

@Composable
fun TimelineScreen(vm: MainViewModel, onOpen: (String) -> Unit) {
    val entries by vm.filteredEntries.collectAsState()
    val mood by vm.moodFilter.collectAsState()
    val tagFilter by vm.tagFilter.collectAsState()
    val allTags by vm.tags.collectAsState()
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Mood:", modifier = Modifier.align(Alignment.CenterVertically))
            FilterChip(selected = mood == null, onClick = { vm.setMoodFilter(null) }, label = { Text("All") })
            listOf(1.0,2.0,3.0,4.0,5.0).forEach { m ->
                FilterChip(selected = mood == m, onClick = { vm.setMoodFilter(m) }, label = { Text(moodLabel(m)) })
            }
        }
        if (allTags.isNotEmpty()) {
            LazyRow(Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item { FilterChip(selected = tagFilter == null, onClick = { vm.setTagFilter(null) }, label = { Text("All tags") }) }
                items(allTags, key = { it.id }) { t ->
                    FilterChip(selected = tagFilter == t.name, onClick = { vm.setTagFilter(if (tagFilter == t.name) null else t.name) }, label = { Text("#${t.name}") })
                }
            }
        }
        if (entries.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No entries yet. Tap + to add one.") }
        } else {
            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(entries, key = { it.entry.id }) { e -> EntryCard(e, onClick = { onOpen(e.entry.id) }) }
            }
        }
    }
}

@Composable
fun EntryCard(e: EntryWithDetails, onClick: () -> Unit) {
    val df = remember { SimpleDateFormat("EEE, MMM d yyyy • HH:mm", Locale.getDefault()) }
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(e.entry.title.ifBlank { "Journal entry" }, fontWeight = FontWeight.Bold)
            Text(df.format(Date(e.entry.dateMillis)), style = MaterialTheme.typography.bodySmall)
            if (e.entry.text.isNotBlank()) Text(e.entry.text.take(120), maxLines = 3)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                e.entry.sentiment?.let { AssistChip(onClick = {}, label = { Text("Mood ${moodLabel(it)}") }) }
                if (e.entry.latitude != null) AssistChip(onClick = {}, label = { Text("Location") })
                e.entry.weatherDescription?.let { desc -> AssistChip(onClick = {}, label = { Text(desc) }) }
                if (e.attachments.isNotEmpty()) AssistChip(onClick = {}, label = { Text("${e.attachments.size} media") })
            }
            if (e.tags.isNotEmpty()) {
                Text("Tags: " + e.tags.joinToString { "#${it.name}" }, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(vm: MainViewModel, onOpen: (String) -> Unit) {
    val monthEntries by vm.monthEntries.collectAsState()
    val offset by vm.monthOffset.collectAsState()
    val cal = remember(offset) { Calendar.getInstance().apply { add(Calendar.MONTH, offset); set(Calendar.DAY_OF_MONTH, 1) } }
    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH)
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDow = cal.get(Calendar.DAY_OF_WEEK) - 1
    val byDay = monthEntries.groupBy {
        Calendar.getInstance().apply { timeInMillis = it.entry.dateMillis }.get(Calendar.DAY_OF_MONTH)
    }
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { vm.setMonthOffset(offset - 1) }) { Icon(Icons.Default.ChevronLeft, null) }
            Text(SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time), fontWeight = FontWeight.Bold)
            IconButton(onClick = { vm.setMonthOffset(offset + 1) }) { Icon(Icons.Default.ChevronRight, null) }
        }
        Row(Modifier.fillMaxWidth()) {
            listOf("S","M","T","W","T","F","S").forEach { Text(it, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall) }
        }
        for (week in 0 until 6) {
            Row(Modifier.fillMaxWidth()) {
                for (dow in 0 until 7) {
                    val idx = week * 7 + dow
                    val dayNum = idx - firstDow + 1
                    val valid = dayNum in 1..daysInMonth
                    Box(Modifier.weight(1f).height(40.dp).padding(2.dp).clip(RoundedCornerShape(6.dp))
                        .background(if (valid && byDay.containsKey(dayNum)) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                        .clickable(enabled = valid && byDay.containsKey(dayNum)) {
                            byDay[dayNum]?.firstOrNull()?.let { onOpen(it.entry.id) }
                        }, contentAlignment = Alignment.Center) {
                        if (valid) {
                            Text(dayNum.toString(), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            if ((week + 1) * 7 - firstDow + 1 > daysInMonth) break
        }
        Spacer(Modifier.height(8.dp))
        Text("Entries this month", fontWeight = FontWeight.SemiBold)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(monthEntries) { e -> EntryCard(e, onClick = { onOpen(e.entry.id) }) }
        }
    }
}

@Composable
fun MapScreen(vm: MainViewModel, onOpen: (String) -> Unit) {
    val entries by vm.mapEntries.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(Unit) { Configuration.getInstance().userAgentValue = context.packageName }
    Column(Modifier.fillMaxSize()) {
        Text("Map view: entries with recorded locations", modifier = Modifier.padding(8.dp), fontWeight = FontWeight.SemiBold)
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(4.0)
                    controller.setCenter(GeoPoint(20.0, 0.0))
                }
            },
            update = { map ->
                map.overlays.clear()
                var first: GeoPoint? = null
                entries.forEach { e ->
                    val lat = e.entry.latitude ?: return@forEach
                    val lng = e.entry.longitude ?: return@forEach
                    val pt = GeoPoint(lat, lng)
                    if (first == null) first = pt
                    val m = Marker(map).apply {
                        position = pt
                        title = e.entry.title.ifBlank { "Entry" }
                        subDescription = e.entry.address ?: e.entry.weatherDescription ?: ""
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        setOnMarkerClickListener { _, _ -> onOpen(e.entry.id); true }
                    }
                    map.overlays.add(m)
                }
                first?.let { map.controller.setCenter(it); map.controller.setZoom(10.0) }
                map.invalidate()
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(vm: MainViewModel, entryId: String?, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val existing by (entryId?.let { vm.entry(it) } ?: flowOf(null)).collectAsState(initial = null)

    var title by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var dateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var mood by remember { mutableStateOf<Double?>(null) }
    var tagsText by remember { mutableStateOf("") }
    var address by remember { mutableStateOf<String?>(null) }
    var lat by remember { mutableStateOf<Double?>(null) }
    var lng by remember { mutableStateOf<Double?>(null) }
    var weatherTemp by remember { mutableStateOf<Double?>(null) }
    var weatherDesc by remember { mutableStateOf<String?>(null) }
    var weatherIcon by remember { mutableStateOf<String?>(null) }
    var weatherPlace by remember { mutableStateOf<String?>(null) }
    var steps by remember { mutableStateOf<Int?>(null) }
    var attachments by remember { mutableStateOf(listOf<AttachmentEntity>()) }
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(existing?.entry?.id, entryId) {
        val e = existing
        if (!loaded && e != null) {
            title = e.entry.title; text = e.entry.text; dateMillis = e.entry.dateMillis
            mood = e.entry.sentiment; tagsText = e.tags.joinToString(", ") { it.name }
            address = e.entry.address; lat = e.entry.latitude; lng = e.entry.longitude
            weatherTemp = e.entry.weatherTempC; weatherDesc = e.entry.weatherDescription
            weatherIcon = e.entry.weatherIcon; weatherPlace = e.entry.weatherPlace
            steps = e.entry.activitySteps; attachments = e.attachments
            loaded = true
        } else if (!loaded && entryId == null) {
            loaded = true
            // Automatic geolocation tagging (and weather) for new entries when possible
            try {
                val loc = LocationUtils.getCurrentLocation(context)
                if (loc != null) {
                    lat = loc.lat; lng = loc.lng; address = loc.address ?: address
                    val w = WeatherService.fetch(loc.lat, loc.lng)
                    if (w != null) {
                        weatherTemp = w.tempC; weatherDesc = w.description; weatherIcon = w.icon; weatherPlace = w.place ?: address
                    }
                }
            } catch (_: Exception) { }
        }
    }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        uris.forEach { try { context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) {} }
        attachments = attachments + uris.map { AttachmentEntity(entryId = entryId ?: "", uri = it.toString(), mimeType = "image/*", displayName = "Photo") }
    }
    val videoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            try { context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) {}
            attachments = attachments + AttachmentEntity(entryId = entryId ?: "", uri = it.toString(), mimeType = "video/*", displayName = "Video")
        }
    }
    val audioPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            try { context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) {}
            attachments = attachments + AttachmentEntity(entryId = entryId ?: "", uri = it.toString(), mimeType = "audio/*", displayName = "Audio")
        }
    }
    val audioRecorder = remember { AudioRecorder(context) }
    var showSignature by remember { mutableStateOf(false) }

    // Step sensor (best-effort automatic motion/activity)
    DisposableEffect(Unit) {
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (steps == null) steps = event.values.firstOrNull()?.toInt()
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        if (sensor != null) sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
        onDispose { sm.unregisterListener(listener) }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text(if (entryId == null) "New Entry" else "Edit Entry") }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
        }, actions = {
            if (entryId != null && existing != null) {
                IconButton(onClick = { vm.deleteEntry(existing!!.entry); onBack() }) { Icon(Icons.Default.Delete, null) }
            }
            IconButton(onClick = {
                val id = entryId ?: UUID.randomUUID().toString()
                val entry = EntryEntity(
                    id = id, title = title, text = text, dateMillis = dateMillis,
                    address = address, latitude = lat, longitude = lng,
                    weatherTempC = weatherTemp, weatherDescription = weatherDesc, weatherIcon = weatherIcon, weatherPlace = weatherPlace ?: address,
                    sentiment = mood, activitySteps = steps
                )
                val tagList = tagsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }.map { TagEntity(name = it) }
                val atts = attachments.map { it.copy(entryId = id, id = if (it.id.isBlank()) UUID.randomUUID().toString() else it.id) }
                vm.saveEntry(entry, atts, tagList)
                onBack()
            }) { Icon(Icons.Default.Save, null) }
        })
    }) { pad ->
        Column(Modifier.padding(pad).padding(12.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(title, { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(text, { text = it }, label = { Text("Write your journal...") }, modifier = Modifier.fillMaxWidth().heightIn(min = 140.dp))

            Text("Mood", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(1.0 to "😞", 2.0 to "😐", 3.0 to "🙂", 4.0 to "😄", 5.0 to "🤩").forEach { (v, emoji) ->
                    FilterChip(selected = mood == v, onClick = { mood = v }, label = { Text("$emoji ${v.toInt()}") })
                }
            }

            OutlinedTextField(tagsText, { tagsText = it }, label = { Text("Tags (comma separated)") }, modifier = Modifier.fillMaxWidth())

            Text("Location & Weather", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    scope.launch {
                        val loc = LocationUtils.getCurrentLocation(context)
                        if (loc != null) {
                            lat = loc.lat; lng = loc.lng; address = loc.address ?: address
                            val w = WeatherService.fetch(loc.lat, loc.lng)
                            if (w != null) { weatherTemp = w.tempC; weatherDesc = w.description; weatherIcon = w.icon; weatherPlace = w.place ?: address }
                        }
                    }
                }) { Icon(Icons.Default.MyLocation, null); Spacer(Modifier.width(4.dp)); Text("Auto tag") }
                OutlinedButton(onClick = { lat = null; lng = null; address = null }) { Text("Clear") }
            }
            Text("Address: ${address ?: "Not set"}")
            Text("Coords: ${lat?.let { "%.5f".format(it) } ?: "-"}, ${lng?.let { "%.5f".format(it) } ?: "-"}")
            Text("Weather: ${weatherTemp?.let { "%.1f°C".format(it) } ?: "-"} ${weatherDesc ?: ""}")

            Text("Motion / Activity", fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = steps?.toString() ?: "",
                onValueChange = { steps = it.filter { c -> c.isDigit() }.toIntOrNull() },
                label = { Text("Steps / activity count (auto if sensor available)") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Attachments (photos, videos, audio, signatures)", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = { photoPicker.launch(arrayOf("image/*")) }) { Text("Photo") }
                OutlinedButton(onClick = { videoPicker.launch(arrayOf("video/*")) }) { Text("Video") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = { audioPicker.launch(arrayOf("audio/*")) }) { Text("Audio file") }
                OutlinedButton(onClick = {
                    scope.launch {
                        val uri = audioRecorder.toggleRecord()
                        if (uri != null) attachments = attachments + AttachmentEntity(entryId = entryId ?: "", uri = uri.toString(), mimeType = "audio/mp4", displayName = "Recording")
                    }
                }) { Text(if (audioRecorder.isRecording) "Stop rec" else "Record") }
                OutlinedButton(onClick = { showSignature = true }) { Text("Signature") }
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(attachments) { att ->
                    Card(modifier = Modifier.width(150.dp)) {
                        Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (att.mimeType.startsWith("image")) {
                                AsyncImage(model = Uri.parse(att.uri), contentDescription = att.displayName, modifier = Modifier.height(80.dp).fillMaxWidth().clickable { openAttachment(context, Uri.parse(att.uri), att.mimeType) })
                            } else {
                                Box(Modifier.height(80.dp).fillMaxWidth().clickable { openAttachment(context, Uri.parse(att.uri), att.mimeType) }, contentAlignment = Alignment.Center) {
                                    Icon(if (att.mimeType.startsWith("video")) Icons.Default.Videocam else if (att.mimeType.startsWith("audio")) Icons.Default.Mic else Icons.Default.AttachFile, null)
                                }
                            }
                            Text(att.displayName ?: att.mimeType, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                            Row {
                                TextButton(onClick = { openAttachment(context, Uri.parse(att.uri), att.mimeType) }) { Text("Open") }
                                TextButton(onClick = { attachments = attachments.filterNot { it.id == att.id && it.uri == att.uri } }) { Text("Remove") }
                            }
                        }
                    }
                }
            }

            val df = remember { SimpleDateFormat("EEE, MMM d yyyy HH:mm", Locale.getDefault()) }
            Text("Date: ${df.format(Date(dateMillis))}")
            Row { OutlinedButton(onClick = { dateMillis = System.currentTimeMillis() }) { Text("Set to now") } }
        }
    }

    if (showSignature) {
        SignatureDialog(onDismiss = { showSignature = false }, onSaved = { uri ->
            attachments = attachments + AttachmentEntity(entryId = entryId ?: "", uri = uri.toString(), mimeType = "image/png", displayName = "Signature")
            showSignature = false
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(vm: MainViewModel) {
    val reminders by vm.reminders.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var hour by remember { mutableIntStateOf(20) }
    var minute by remember { mutableIntStateOf(0) }
    var selectedDays by remember { mutableStateOf(setOf(1,2,3,4,5,6,7)) } // 1=Sun..7=Sat
    val dayLabels = listOf(1 to "S", 2 to "M", 3 to "T", 4 to "W", 5 to "T", 6 to "F", 7 to "S")
    Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Daily reminders", fontWeight = FontWeight.Bold)
            Button(onClick = { showAdd = true }) { Icon(Icons.Default.Add, null); Text("Add") }
        }
        if (reminders.isEmpty()) Text("No reminders set. Add one to get notified to write.")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(reminders, key = { it.id }) { r ->
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(String.format("%02d:%02d", r.hour, r.minute), fontWeight = FontWeight.Bold)
                            Text(r.label)
                            Text("Days: " + daysSummary(r.daysOfWeek), style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(checked = r.enabled, onCheckedChange = { vm.saveReminder(r.copy(enabled = it)) })
                        IconButton(onClick = { vm.deleteReminder(r) }) { Icon(Icons.Default.Delete, null) }
                    }
                }
            }
        }
    }
    if (showAdd) {
        AlertDialog(onDismissRequest = { showAdd = false }, confirmButton = {
            TextButton(onClick = {
                val days = if (selectedDays.isEmpty()) "1,2,3,4,5,6,7" else selectedDays.sorted().joinToString(",")
                vm.saveReminder(ReminderEntity(hour = hour, minute = minute, enabled = true, daysOfWeek = days))
                showAdd = false
                selectedDays = setOf(1,2,3,4,5,6,7)
            }) { Text("Save") }
        }, dismissButton = { TextButton(onClick = { showAdd = false }) { Text("Cancel") } }, title = { Text("Add reminder") }, text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(hour.toString(), { hour = it.filter(Char::isDigit).toIntOrNull()?.coerceIn(0,23) ?: 0 }, label = { Text("Hour (0-23)") })
                OutlinedTextField(minute.toString(), { minute = it.filter(Char::isDigit).toIntOrNull()?.coerceIn(0,59) ?: 0 }, label = { Text("Minute (0-59)") })
                Text("Days of week")
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    dayLabels.forEach { (v, lbl) ->
                        FilterChip(selected = selectedDays.contains(v), onClick = {
                            selectedDays = if (selectedDays.contains(v)) selectedDays - v else selectedDays + v
                        }, label = { Text(lbl) })
                    }
                }
            }
        })
    }
}

fun daysSummary(csv: String): String {
    val set = csv.split(',').mapNotNull { it.trim().toIntOrNull() }.toSet()
    return if (set.size == 7) "Every day" else set.sorted().joinToString { when(it){1->"Sun";2->"Mon";3->"Tue";4->"Wed";5->"Thu";6->"Fri";7->"Sat";else->it.toString()} }
}

fun moodLabel(m: Double): String = when (m.toInt()) { 1 -> "Awful"; 2 -> "Bad"; 3 -> "Okay"; 4 -> "Good"; 5 -> "Great"; else -> m.toString() }
