package com.example.ui.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SettingsAccessibility
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.StayCurrentPortrait
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.RedmiOptimizerUtil

@Composable
fun RedmiOptimizationDialog(
    curvedEdgePaddingEnabled: Boolean,
    onToggleCurvedEdgePadding: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val deviceInfo = remember { RedmiOptimizerUtil.getDeviceInfo(context) }

    var hasNotificationPerm by remember { mutableStateOf(RedmiOptimizerUtil.isNotificationPermissionGranted(context)) }
    var hasOverlayPerm by remember { mutableStateOf(RedmiOptimizerUtil.isOverlayPermissionGranted(context)) }
    var hasAccessibility by remember { mutableStateOf(RedmiOptimizerUtil.isAccessibilityServiceEnabled(context)) }
    var hasBatteryIgnored by remember { mutableStateOf(RedmiOptimizerUtil.isBatteryOptimizationIgnored(context)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.StayCurrentPortrait,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column {
                    Text(
                        "Redmi Note 13 Pro+ Optimizer",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        "HyperOS & 1.5K AMOLED Calibration",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Device Hardware Info Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Device: ${deviceInfo.manufacturer} ${deviceInfo.model}",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    "Android ${deviceInfo.androidVersion} (API ${deviceInfo.apiLevel})",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ResolutionBadge("1.5K AMOLED", deviceInfo.displayResolution)
                            ResolutionBadge("Density", "${deviceInfo.densityDpi} DPI")
                            ResolutionBadge("Refresh", "${deviceInfo.refreshRateHz} Hz")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Curved Edge Rejection Section
                Text(
                    "3D Curved Screen Optimization",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "The Redmi Note 13 Pro+ features hyperbolic curved glass edges. Adding 16dp horizontal safety padding prevents accidental touch rejection issues when gripping the phone.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Curved Edge Safe Margins",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                if (curvedEdgePaddingEnabled) "Active • 16dp padding on curved edges" else "Disabled • Full edge to edge",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (curvedEdgePaddingEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = curvedEdgePaddingEnabled,
                            onCheckedChange = onToggleCurvedEdgePadding
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Permissions & Xiaomi HyperOS Optimization Section
                Text(
                    "Xiaomi HyperOS / MIUI Permissions",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Xiaomi's aggressive background cleaner and Android 13/14 restrictions require these specific permissions for 100% reliable background clipboard capture:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                // 1. Accessibility Service (Crucial for background clipboard on Android 10+ / HyperOS)
                PermissionCheckRow(
                    title = "Background Clipboard Service",
                    subtitle = "Allows capturing copies across all apps in background on HyperOS",
                    isGranted = hasAccessibility,
                    icon = Icons.Default.SettingsAccessibility,
                    buttonText = if (hasAccessibility) "Enabled" else "Turn On",
                    onClick = {
                        RedmiOptimizerUtil.openAccessibilitySettings(context)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 2. Notification Permission (Android 13+)
                PermissionCheckRow(
                    title = "Foreground Notification",
                    subtitle = "Required on Android 13+ to keep clipboard monitor service active",
                    isGranted = hasNotificationPerm,
                    icon = Icons.Default.Notifications,
                    buttonText = if (hasNotificationPerm) "Granted" else "Grant",
                    onClick = {
                        RedmiOptimizerUtil.openNotificationSettings(context)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 3. Floating Overlay
                PermissionCheckRow(
                    title = "Floating Quick Bubble",
                    subtitle = "Allows floating clipboard icon over apps on screen",
                    isGranted = hasOverlayPerm,
                    icon = Icons.Default.Widgets,
                    buttonText = if (hasOverlayPerm) "Granted" else "Enable",
                    onClick = {
                        RedmiOptimizerUtil.openOverlaySettings(context)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 4. Battery Saver
                PermissionCheckRow(
                    title = "No Battery Restrictions",
                    subtitle = "Prevents Xiaomi cleaner from killing clipboard service",
                    isGranted = hasBatteryIgnored,
                    icon = Icons.Default.BatteryAlert,
                    buttonText = if (hasBatteryIgnored) "Unrestricted" else "Configure",
                    onClick = {
                        RedmiOptimizerUtil.requestIgnoreBatteryOptimization(context)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 5. Xiaomi Autostart
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Xiaomi Autostart Settings", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "In MIUI Security, enable 'Autostart' for Multi Clipboard so it starts after restarting your phone.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedButton(
                            onClick = { RedmiOptimizerUtil.openXiaomiAutostart(context) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Open MIUI Autostart Center", fontSize = 12.sp)
                        }
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
}

@Composable
private fun ResolutionBadge(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun PermissionCheckRow(
    title: String,
    subtitle: String,
    isGranted: Boolean,
    icon: ImageVector,
    buttonText: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isGranted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (isGranted) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Granted",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(buttonText, fontSize = 11.sp)
                }
            }
        }
    }
}
