package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.MultiClipboardApp
import com.example.service.ClipboardMonitorService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED || intent?.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            val prefs = MultiClipboardApp.instance.preferenceManager
            if (prefs.autoMonitor.value && prefs.notificationEnabled.value) {
                ClipboardMonitorService.start(context)
            }
        }
    }
}
