package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferenceManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("multi_clipboard_prefs", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(prefs.getString(KEY_THEME_MODE, "amoled") ?: "amoled")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _autoMonitor = MutableStateFlow(prefs.getBoolean(KEY_AUTO_MONITOR, true))
    val autoMonitor: StateFlow<Boolean> = _autoMonitor.asStateFlow()

    private val _notificationEnabled = MutableStateFlow(prefs.getBoolean(KEY_NOTIFICATION, true))
    val notificationEnabled: StateFlow<Boolean> = _notificationEnabled.asStateFlow()

    private val _floatingBubble = MutableStateFlow(prefs.getBoolean(KEY_FLOATING_BUBBLE, false))
    val floatingBubble: StateFlow<Boolean> = _floatingBubble.asStateFlow()

    private val _curvedEdgePadding = MutableStateFlow(prefs.getBoolean(KEY_CURVED_EDGE_PADDING, true))
    val curvedEdgePadding: StateFlow<Boolean> = _curvedEdgePadding.asStateFlow()

    private val _hapticFeedback = MutableStateFlow(prefs.getBoolean(KEY_HAPTIC, true))
    val hapticFeedback: StateFlow<Boolean> = _hapticFeedback.asStateFlow()

    private val _maxClips = MutableStateFlow(prefs.getInt(KEY_MAX_CLIPS, 250))
    val maxClips: StateFlow<Int> = _maxClips.asStateFlow()

    private val _deduplicate = MutableStateFlow(prefs.getBoolean(KEY_DEDUPLICATE, true))
    val deduplicate: StateFlow<Boolean> = _deduplicate.asStateFlow()

    fun setThemeMode(mode: String) {
        prefs.edit().putString(KEY_THEME_MODE, mode).apply()
        _themeMode.value = mode
    }

    fun setAutoMonitor(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_MONITOR, enabled).apply()
        _autoMonitor.value = enabled
    }

    fun setNotificationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATION, enabled).apply()
        _notificationEnabled.value = enabled
    }

    fun setFloatingBubble(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_FLOATING_BUBBLE, enabled).apply()
        _floatingBubble.value = enabled
    }

    fun setCurvedEdgePadding(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_CURVED_EDGE_PADDING, enabled).apply()
        _curvedEdgePadding.value = enabled
    }

    fun setHapticFeedback(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTIC, enabled).apply()
        _hapticFeedback.value = enabled
    }

    fun setMaxClips(limit: Int) {
        prefs.edit().putInt(KEY_MAX_CLIPS, limit).apply()
        _maxClips.value = limit
    }

    fun setDeduplicate(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DEDUPLICATE, enabled).apply()
        _deduplicate.value = enabled
    }

    companion object {
        private const val KEY_THEME_MODE = "pref_theme_mode"
        private const val KEY_AUTO_MONITOR = "pref_auto_monitor"
        private const val KEY_NOTIFICATION = "pref_notification"
        private const val KEY_FLOATING_BUBBLE = "pref_floating_bubble"
        private const val KEY_CURVED_EDGE_PADDING = "pref_curved_edge_padding"
        private const val KEY_HAPTIC = "pref_haptic_feedback"
        private const val KEY_MAX_CLIPS = "pref_max_clips"
        private const val KEY_DEDUPLICATE = "pref_deduplicate"
    }
}
