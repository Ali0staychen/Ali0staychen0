package com.journey.replica.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.journey.replica.data.*
import com.journey.replica.reminders.ReminderScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(private val repo: AppRepository, app: Application) : AndroidViewModel(app) {
    val entries = repo.entries.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val tags = repo.tags.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val reminders = repo.reminders.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val mapEntries = repo.entriesWithLocation().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _monthOffset = MutableStateFlow(0)
    val monthOffset: StateFlow<Int> = _monthOffset
    val monthEntries = _monthOffset.flatMapLatest { offset ->
        val cal = Calendar.getInstance().apply { add(Calendar.MONTH, offset) }
        repo.entriesInMonth(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _moodFilter = MutableStateFlow<Double?>(null)
    val moodFilter = _moodFilter.asStateFlow()
    private val _tagFilter = MutableStateFlow<String?>(null)
    val tagFilter = _tagFilter.asStateFlow()
    val filteredEntries = combine(entries, _moodFilter, _tagFilter) { list, mood, tag ->
        list.filter { e ->
            (mood == null || e.entry.sentiment == mood) &&
            (tag.isNullOrBlank() || e.tags.any { it.name.equals(tag, ignoreCase = true) })
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setMonthOffset(offset: Int) { _monthOffset.value = offset }
    fun setMoodFilter(mood: Double?) { _moodFilter.value = mood }
    fun setTagFilter(tag: String?) { _tagFilter.value = tag }

    fun entry(id: String) = repo.entry(id)

    fun saveEntry(entry: EntryEntity, attachments: List<AttachmentEntity>, tags: List<TagEntity>) {
        viewModelScope.launch { repo.saveEntry(entry, attachments, tags) }
    }

    fun deleteEntry(entry: EntryEntity) { viewModelScope.launch { repo.deleteEntry(entry) } }

    fun saveReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            val id = repo.upsertReminder(reminder)
            val saved = reminder.copy(id = if (reminder.id == 0L) id else reminder.id)
            val ctx = getApplication<Application>()
            if (saved.enabled) ReminderScheduler.schedule(ctx, saved.id, saved.hour, saved.minute, saved.label, saved.daysOfWeek)
            else ReminderScheduler.cancel(ctx, saved.id)
        }
    }

    fun deleteReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            ReminderScheduler.cancel(getApplication(), reminder.id)
            repo.deleteReminder(reminder)
        }
    }
}

class MainViewModelFactory(private val repo: AppRepository, private val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MainViewModel(repo, app) as T
    }
}
