package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.ActionMode
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.MultiClipboardTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Handled permission result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request notification permission on Android 13+ for foreground service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Handle shared text from other apps
        handleIncomingShare(intent)

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val systemDark = isSystemInDarkTheme()

            val isDark = when (themeMode) {
                "amoled", "dark" -> true
                "light" -> false
                else -> systemDark
            }
            val isAmoled = themeMode == "amoled"

            // Wrap TextToolbar to safely guard against floating action mode lifecycle detachment race conditions
            val currentToolbar = LocalTextToolbar.current
            val safeToolbar = remember(currentToolbar) {
                object : TextToolbar {
                    override val status: TextToolbarStatus
                        get() = try {
                            currentToolbar.status
                        } catch (_: Throwable) {
                            TextToolbarStatus.Hidden
                        }

                    override fun hide() {
                        try {
                            if (currentToolbar.status == TextToolbarStatus.Shown) {
                                currentToolbar.hide()
                            }
                        } catch (_: Throwable) {}
                    }

                    override fun showMenu(
                        rect: Rect,
                        onCopyRequested: (() -> Unit)?,
                        onPasteRequested: (() -> Unit)?,
                        onCutRequested: (() -> Unit)?,
                        onSelectAllRequested: (() -> Unit)?
                    ) {
                        try {
                            currentToolbar.showMenu(rect, onCopyRequested, onPasteRequested, onCutRequested, onSelectAllRequested)
                        } catch (_: Throwable) {}
                    }
                }
            }

            CompositionLocalProvider(LocalTextToolbar provides safeToolbar) {
                MultiClipboardTheme(
                    darkTheme = isDark,
                    isAmoledMode = isAmoled
                ) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }

    override fun onActionModeFinished(mode: ActionMode?) {
        try {
            super.onActionModeFinished(mode)
        } catch (_: Throwable) {}
    }

    override fun onActionModeStarted(mode: ActionMode?) {
        try {
            super.onActionModeStarted(mode)
        } catch (_: Throwable) {}
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingShare(intent)
    }

    private fun handleIncomingShare(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (!sharedText.isNullOrBlank()) {
                viewModel.addManualClip(
                    content = sharedText,
                    isNote = false,
                    tag = "Shared",
                    isStarred = false,
                    colorHex = ""
                )
            }
        }
    }
}
