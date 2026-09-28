package com.example.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.MultiClipboardApp
import com.example.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ClipboardMonitorService : Service() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private var clipboardManager: ClipboardManager? = null
    private var lastRecordedClip: String = ""

    private val clipChangedListener = ClipboardManager.OnPrimaryClipChangedListener {
        checkAndRecordClipboard()
    }

    override fun onCreate() {
        super.onCreate()
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboardManager?.addPrimaryClipChangedListener(clipChangedListener)

        // Observe latest clip to update notification
        val repository = MultiClipboardApp.instance.repository
        serviceScope.launch {
            repository.latestClip.collectLatest { clip ->
                if (clip != null) {
                    lastRecordedClip = clip.content
                    updateNotification(clip.content)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_COPY_LAST && lastRecordedClip.isNotEmpty()) {
            copyTextToClipboard(lastRecordedClip)
        } else if (action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(NOTIFICATION_ID, buildNotification("Active • Monitoring clipboard copies"))
        checkAndRecordClipboard()

        return START_STICKY
    }

    private fun checkAndRecordClipboard() {
        val cm = clipboardManager ?: return
        try {
            val clipData = cm.primaryClip
            if (clipData != null && clipData.itemCount > 0) {
                val text = clipData.getItemAt(0)?.text?.toString() ?: ""
                if (text.isNotBlank() && text != lastRecordedClip) {
                    lastRecordedClip = text
                    serviceScope.launch(Dispatchers.IO) {
                        MultiClipboardApp.instance.repository.saveClipFromClipboard(text)
                    }
                }
            }
        } catch (_: Exception) {
            // In Android 10+, background access might be blocked without focus or accessibility
        }
    }

    private fun copyTextToClipboard(text: String) {
        val cm = clipboardManager ?: return
        try {
            val clip = ClipData.newPlainText("Multi Clipboard", text)
            cm.setPrimaryClip(clip)
        } catch (_: Exception) {}
    }

    private fun updateNotification(latestText: String) {
        val notification = buildNotification(latestText.take(60) + if (latestText.length > 60) "..." else "")
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? android.app.NotificationManager
        manager?.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(contentText: String): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val copyLastIntent = Intent(this, ClipboardMonitorService::class.java).apply {
            action = ACTION_COPY_LAST
        }
        val copyLastPendingIntent = PendingIntent.getService(
            this,
            1,
            copyLastIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, MultiClipboardApp.CHANNEL_ID_MONITOR)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(contentText)
            .setContentIntent(openAppPendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(0, getString(R.string.notification_quick_copy), copyLastPendingIntent)
            .addAction(0, getString(R.string.notification_open_app), openAppPendingIntent)
            .build()
    }

    override fun onDestroy() {
        clipboardManager?.removePrimaryClipChangedListener(clipChangedListener)
        serviceJob.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val NOTIFICATION_ID = 1001
        const val ACTION_COPY_LAST = "com.example.ACTION_COPY_LAST"
        const val ACTION_STOP = "com.example.ACTION_STOP"

        fun start(context: Context) {
            val intent = Intent(context, ClipboardMonitorService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, ClipboardMonitorService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }
}
