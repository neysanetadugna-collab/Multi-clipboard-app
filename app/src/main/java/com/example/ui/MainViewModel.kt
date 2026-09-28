package com.example.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MultiClipboardApp
import com.example.data.model.ClipboardItem
import com.example.data.model.TagItem
import com.example.service.ClipboardMonitorService
import com.example.service.FloatingBubbleService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ClipFilter {
    ALL,
    STARRED,
    NOTES,
    LINKS,
    CONTACTS,
    TAG
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as MultiClipboardApp).repository
    private val prefs = (application as MultiClipboardApp).preferenceManager
    private val clipboardManager = application.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager

    val themeMode = prefs.themeMode
    val curvedEdgePadding = prefs.curvedEdgePadding
    val autoMonitor = prefs.autoMonitor
    val notificationEnabled = prefs.notificationEnabled
    val floatingBubble = prefs.floatingBubble
    val hapticFeedback = prefs.hapticFeedback

    val allTags: StateFlow<List<TagItem>> = repository.allTags
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trashClips: StateFlow<List<ClipboardItem>> = repository.trashClips
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedFilter = MutableStateFlow(ClipFilter.ALL)
    val selectedFilter: StateFlow<ClipFilter> = _selectedFilter.asStateFlow()

    private val _selectedTag = MutableStateFlow<String?>(null)
    val selectedTag: StateFlow<String?> = _selectedTag.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedClipForDetail = MutableStateFlow<ClipboardItem?>(null)
    val selectedClipForDetail: StateFlow<ClipboardItem?> = _selectedClipForDetail.asStateFlow()

    private val _isCreateDialogOpen = MutableStateFlow(false)
    val isCreateDialogOpen: StateFlow<Boolean> = _isCreateDialogOpen.asStateFlow()

    private val _isTagsDialogOpen = MutableStateFlow(false)
    val isTagsDialogOpen: StateFlow<Boolean> = _isTagsDialogOpen.asStateFlow()

    private val _isRedmiHubOpen = MutableStateFlow(false)
    val isRedmiHubOpen: StateFlow<Boolean> = _isRedmiHubOpen.asStateFlow()

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    private val _isTrashOpen = MutableStateFlow(false)
    val isTrashOpen: StateFlow<Boolean> = _isTrashOpen.asStateFlow()

    private val _snackBarEvent = MutableSharedFlow<String>()
    val snackBarEvent = _snackBarEvent.asSharedFlow()

    // Filtered and searched clips
    val filteredClips: StateFlow<List<ClipboardItem>> = combine(
        repository.activeClips,
        _selectedFilter,
        _selectedTag,
        _searchQuery
    ) { allActive, filter, tag, query ->
        var list = allActive

        // Filter category
        list = when (filter) {
            ClipFilter.ALL -> list
            ClipFilter.STARRED -> list.filter { it.isStarred }
            ClipFilter.NOTES -> list.filter { it.isNote }
            ClipFilter.LINKS -> list.filter { it.hasUrl }
            ClipFilter.CONTACTS -> list.filter { it.hasEmail || it.hasPhone }
            ClipFilter.TAG -> if (tag != null) list.filter { it.tag.equals(tag, ignoreCase = true) } else list
        }

        // Search query
        if (query.isNotBlank()) {
            list = list.filter { it.content.contains(query, ignoreCase = true) || it.tag.contains(query, ignoreCase = true) }
        }

        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Start background service if enabled
        if (autoMonitor.value && notificationEnabled.value) {
            ClipboardMonitorService.start(getApplication())
        }
        if (floatingBubble.value) {
            val intent = android.content.Intent(getApplication(), FloatingBubbleService::class.java)
            getApplication<Application>().startService(intent)
        }
    }

    fun setFilter(filter: ClipFilter, tag: String? = null) {
        _selectedFilter.value = filter
        _selectedTag.value = tag
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun copyClip(item: ClipboardItem) {
        val cm = clipboardManager ?: return
        try {
            val clip = ClipData.newPlainText("Multi Clipboard", item.content)
            cm.setPrimaryClip(clip)
            performHapticFeedback()
            viewModelScope.launch {
                _snackBarEvent.emit("Copied to clipboard!")
            }
        } catch (e: Exception) {
            viewModelScope.launch {
                _snackBarEvent.emit("Failed to copy: ${e.message}")
            }
        }
    }

    private fun performHapticFeedback() {
        if (!hapticFeedback.value) return
        val app = getApplication<Application>()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = app.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = app.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(30)
                }
            }
        } catch (_: Exception) {}
    }

    fun toggleStar(clip: ClipboardItem) {
        viewModelScope.launch {
            repository.toggleStar(clip)
        }
    }

    fun moveToTrash(id: Long) {
        viewModelScope.launch {
            repository.moveToTrash(id)
            _snackBarEvent.emit("Moved to Trash")
        }
    }

    fun restoreFromTrash(id: Long) {
        viewModelScope.launch {
            repository.restoreFromTrash(id)
            _snackBarEvent.emit("Clip restored")
        }
    }

    fun deletePermanently(id: Long) {
        viewModelScope.launch {
            repository.deletePermanently(id)
            _snackBarEvent.emit("Permanently deleted")
        }
    }

    fun emptyTrash() {
        viewModelScope.launch {
            repository.emptyTrash()
            _snackBarEvent.emit("Trash emptied")
        }
    }

    fun addManualClip(content: String, isNote: Boolean, tag: String, isStarred: Boolean, colorHex: String) {
        viewModelScope.launch {
            repository.addClipManually(content, isNote, tag, isStarred, colorHex)
            _isCreateDialogOpen.value = false
            _snackBarEvent.emit(if (isNote) "Note created" else "Clip saved")
        }
    }

    fun updateClip(clip: ClipboardItem) {
        viewModelScope.launch {
            repository.updateClip(clip)
            _selectedClipForDetail.value = null
            _snackBarEvent.emit("Clip updated")
        }
    }

    fun mergeClips(clips: List<ClipboardItem>, separator: String = "\n\n") {
        if (clips.size < 2) return
        viewModelScope.launch {
            val mergedText = clips.joinToString(separator) { it.content }
            repository.addClipManually(
                content = mergedText,
                isNote = true,
                tag = "Merged",
                isStarred = false,
                colorHex = "#00E5FF"
            )
            _snackBarEvent.emit("Merged ${clips.size} clips into a note!")
        }
    }

    fun addTag(name: String, colorHex: String) {
        viewModelScope.launch {
            repository.addTag(name, colorHex)
            _snackBarEvent.emit("Tag '$name' added")
        }
    }

    fun deleteTag(name: String) {
        viewModelScope.launch {
            repository.deleteTag(name)
            if (_selectedTag.value == name) {
                _selectedTag.value = null
                _selectedFilter.value = ClipFilter.ALL
            }
            _snackBarEvent.emit("Tag '$name' deleted")
        }
    }

    fun openDetail(clip: ClipboardItem) {
        _selectedClipForDetail.value = clip
    }

    fun closeDetail() {
        _selectedClipForDetail.value = null
    }

    fun setCreateDialogOpen(open: Boolean) {
        _isCreateDialogOpen.value = open
    }

    fun setTagsDialogOpen(open: Boolean) {
        _isTagsDialogOpen.value = open
    }

    fun setRedmiHubOpen(open: Boolean) {
        _isRedmiHubOpen.value = open
    }

    fun setSettingsOpen(open: Boolean) {
        _isSettingsOpen.value = open
    }

    fun setTrashOpen(open: Boolean) {
        _isTrashOpen.value = open
    }

    // Preferences
    fun setThemeMode(mode: String) {
        prefs.setThemeMode(mode)
    }

    fun setCurvedEdgePadding(enabled: Boolean) {
        prefs.setCurvedEdgePadding(enabled)
    }

    fun setHapticFeedback(enabled: Boolean) {
        prefs.setHapticFeedback(enabled)
    }

    fun setAutoMonitor(enabled: Boolean) {
        prefs.setAutoMonitor(enabled)
        if (enabled && notificationEnabled.value) {
            ClipboardMonitorService.start(getApplication())
        } else if (!enabled) {
            ClipboardMonitorService.stop(getApplication())
        }
    }

    fun setNotificationEnabled(enabled: Boolean) {
        prefs.setNotificationEnabled(enabled)
        if (enabled && autoMonitor.value) {
            ClipboardMonitorService.start(getApplication())
        } else {
            ClipboardMonitorService.stop(getApplication())
        }
    }

    fun setFloatingBubble(enabled: Boolean) {
        prefs.setFloatingBubble(enabled)
        val intent = android.content.Intent(getApplication(), FloatingBubbleService::class.java)
        if (enabled) {
            getApplication<Application>().startService(intent)
        } else {
            getApplication<Application>().stopService(intent)
        }
    }

    fun setMaxClips(limit: Int) {
        prefs.setMaxClips(limit)
    }

    fun setDeduplicate(enabled: Boolean) {
        prefs.setDeduplicate(enabled)
    }

    fun exportBackup(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val clips = filteredClips.value
            val json = repository.exportToJson(clips)
            onResult(json)
        }
    }

    fun importBackup(jsonStr: String) {
        viewModelScope.launch {
            val count = repository.importFromJson(jsonStr)
            _snackBarEvent.emit("Successfully imported $count clips!")
        }
    }
}
