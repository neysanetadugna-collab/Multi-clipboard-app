package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.StayCurrentPortrait
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.RedmiOptimizerUtil

@Composable
fun SettingsDialog(
    themeMode: String,
    onThemeModeChange: (String) -> Unit,
    curvedEdgePadding: Boolean,
    onCurvedEdgePaddingChange: (Boolean) -> Unit,
    autoMonitor: Boolean,
    onAutoMonitorChange: (Boolean) -> Unit,
    notificationEnabled: Boolean,
    onNotificationEnabledChange: (Boolean) -> Unit,
    floatingBubble: Boolean,
    onFloatingBubbleChange: (Boolean) -> Unit,
    hapticFeedback: Boolean,
    onHapticFeedbackChange: (Boolean) -> Unit,
    onOpenRedmiHub: () -> Unit,
    onExportBackup: ((String) -> Unit) -> Unit,
    onImportBackup: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isImportDialogOpen by remember { mutableStateOf(false) }
    var importJsonText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Multi Clipboard Settings",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Theme Selection
                Text("Display & Themes", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = themeMode == "amoled",
                        onClick = { onThemeModeChange("amoled") },
                        label = { Text("AMOLED Pure Black", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = themeMode == "dark",
                        onClick = { onThemeModeChange("dark") },
                        label = { Text("Dark", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = themeMode == "light",
                        onClick = { onThemeModeChange("light") },
                        label = { Text("Light", fontSize = 11.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider()
                Spacer(modifier = Modifier.height(14.dp))

                // Redmi Device Hub Shortcut
                Card(
                    onClick = {
                        onDismiss()
                        onOpenRedmiHub()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.StayCurrentPortrait, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Redmi Note 13 Pro+ Optimizer", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("HyperOS autostart, battery & curved edge calibration", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider()
                Spacer(modifier = Modifier.height(14.dp))

                // Service & Functional Toggles
                Text("Services & Behaviors", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))

                SettingsToggleRow(
                    title = "Auto-Monitor Clipboard",
                    subtitle = "Capture copied text automatically",
                    icon = Icons.Default.TouchApp,
                    checked = autoMonitor,
                    onCheckedChange = onAutoMonitorChange
                )

                SettingsToggleRow(
                    title = "Foreground Notification",
                    subtitle = "Quick copy last clip from status bar",
                    icon = Icons.Default.Notifications,
                    checked = notificationEnabled,
                    onCheckedChange = { enabled ->
                        if (enabled && !RedmiOptimizerUtil.isNotificationPermissionGranted(context)) {
                            RedmiOptimizerUtil.openNotificationSettings(context)
                        }
                        onNotificationEnabledChange(enabled)
                    }
                )

                SettingsToggleRow(
                    title = "Floating Bubble Quick Widget",
                    subtitle = "Overlay icon to access clipboard over any app",
                    icon = Icons.Default.Widgets,
                    checked = floatingBubble,
                    onCheckedChange = { enabled ->
                        if (enabled && !RedmiOptimizerUtil.isOverlayPermissionGranted(context)) {
                            RedmiOptimizerUtil.openOverlaySettings(context)
                        }
                        onFloatingBubbleChange(enabled)
                    }
                )

                SettingsToggleRow(
                    title = "Curved Edge Safety Margin",
                    subtitle = "16dp side padding for 3D curved screens",
                    icon = Icons.Default.StayCurrentPortrait,
                    checked = curvedEdgePadding,
                    onCheckedChange = onCurvedEdgePaddingChange
                )

                SettingsToggleRow(
                    title = "Haptic Vibration Feedback",
                    subtitle = "Gentle click vibration on copy & actions",
                    icon = Icons.Default.Vibration,
                    checked = hapticFeedback,
                    onCheckedChange = onHapticFeedbackChange
                )

                Spacer(modifier = Modifier.height(14.dp))
                Divider()
                Spacer(modifier = Modifier.height(14.dp))

                // Backup & Restore
                Text("Backup & Data Management", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onExportBackup { json ->
                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                val clip = ClipData.newPlainText("Multi Clipboard Backup JSON", json)
                                cm?.setPrimaryClip(clip)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export JSON", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { isImportDialogOpen = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Import Backup", fontSize = 11.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Done")
            }
        }
    )

    if (isImportDialogOpen) {
        AlertDialog(
            onDismissRequest = { isImportDialogOpen = false },
            title = { Text("Import JSON Backup") },
            text = {
                Column {
                    Text("Paste previously exported JSON content:", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importJsonText,
                        onValueChange = { importJsonText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (importJsonText.isNotBlank()) {
                            onImportBackup(importJsonText.trim())
                            isImportDialogOpen = false
                        }
                    },
                    enabled = importJsonText.isNotBlank()
                ) {
                    Text("Import")
                }
            },
            dismissButton = {
                TextButton(onClick = { isImportDialogOpen = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
