package com.example.service

import android.accessibilityservice.AccessibilityService
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.accessibility.AccessibilityEvent
import com.example.MultiClipboardApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MultiClipboardAccessibilityService : AccessibilityService() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private var clipboardManager: ClipboardManager? = null
    private var lastRecordedText = ""

    override fun onServiceConnected() {
        super.onServiceConnected()
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        isRunning = true
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Accessibility events allow background access to clipboard on Android 10+ & HyperOS/MIUI
        if (event == null) return
        val eventType = event.eventType
        if (eventType == AccessibilityEvent.TYPE_VIEW_CLICKED ||
            eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED ||
            eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        ) {
            checkClipboard()
        }
    }

    private fun checkClipboard() {
        val cm = clipboardManager ?: return
        try {
            val clip: ClipData? = cm.primaryClip
            if (clip != null && clip.itemCount > 0) {
                val text = clip.getItemAt(0)?.text?.toString() ?: ""
                if (text.isNotBlank() && text != lastRecordedText) {
                    lastRecordedText = text
                    scope.launch {
                        MultiClipboardApp.instance.repository.saveClipFromClipboard(text)
                    }
                }
            }
        } catch (_: Exception) {}
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        isRunning = false
        job.cancel()
        super.onDestroy()
    }

    companion object {
        var isRunning = false
            private set
    }
}
