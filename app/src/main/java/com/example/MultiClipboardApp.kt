package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.data.local.AppDatabase
import com.example.data.preferences.PreferenceManager
import com.example.data.repository.ClipboardRepository

class MultiClipboardApp : Application() {

    lateinit var database: AppDatabase
        private set
    lateinit var preferenceManager: PreferenceManager
        private set
    lateinit var repository: ClipboardRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AppDatabase.getInstance(this)
        preferenceManager = PreferenceManager(this)
        repository = ClipboardRepository(database.clipboardDao(), preferenceManager)

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_MONITOR,
                getString(R.string.clipboard_notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.clipboard_notification_channel_desc)
                setShowBadge(false)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID_MONITOR = "multi_clipboard_service_channel"
        lateinit var instance: MultiClipboardApp
            private set
    }
}
