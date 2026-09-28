package com.example.ui

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StayCurrentPortrait
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ClipCard
import com.example.ui.components.ClipDetailDialog
import com.example.ui.components.CreateEditClipDialog
import com.example.ui.components.FilterBar
import com.example.ui.components.QuickAddCard
import com.example.ui.components.RedmiOptimizationDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.components.TagsManagementDialog
import com.example.ui.components.TrashDialog
import com.example.util.RedmiOptimizerUtil
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val clips by viewModel.filteredClips.collectAsStateWithLifecycle()
    val tags by viewModel.allTags.collectAsStateWithLifecycle()
    val trashClips by viewModel.trashClips.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val selectedTag by viewModel.selectedTag.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val curvedEdgePadding by viewModel.curvedEdgePadding.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val autoMonitor by viewModel.autoMonitor.collectAsStateWithLifecycle()
    val notificationEnabled by viewModel.notificationEnabled.collectAsStateWithLifecycle()
    val floatingBubble by viewModel.floatingBubble.collectAsStateWithLifecycle()
    val hapticFeedback by viewModel.hapticFeedback.collectAsStateWithLifecycle()

    val selectedClipForDetail by viewModel.selectedClipForDetail.collectAsStateWithLifecycle()
    val isCreateDialogOpen by viewModel.isCreateDialogOpen.collectAsStateWithLifecycle()
    val isTagsDialogOpen by viewModel.isTagsDialogOpen.collectAsStateWithLifecycle()
    val isRedmiHubOpen by viewModel.isRedmiHubOpen.collectAsStateWithLifecycle()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsStateWithLifecycle()
    val isTrashOpen by viewModel.isTrashOpen.collectAsStateWithLifecycle()

    var isSearchExpanded by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Check device optimization status
    val hasAccService = remember { RedmiOptimizerUtil.isAccessibilityServiceEnabled(context) }
    val hasNotif = remember { RedmiOptimizerUtil.isNotificationPermissionGranted(context) }
    val needsOptimizationNotice = remember { !hasAccService || !hasNotif }

    LaunchedEffect(Unit) {
        viewModel.snackBarEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Side safe padding for Redmi Note 13 Pro+ curved edges (16dp if enabled, 8dp standard)
    val horizontalSidePadding = if (curvedEdgePadding) 16.dp else 10.dp

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                "Multi Clipboard",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF00E5FF))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "1.5K AMOLED • Redmi 13 Pro+",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Search toggle button
                    IconButton(
                        onClick = {
                            isSearchExpanded = !isSearchExpanded
                            if (!isSearchExpanded) viewModel.setSearchQuery("")
                        },
                        modifier = Modifier.testTag("search_toggle_button")
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (searchQuery.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Redmi Calibration Hub shortcut with attention indicator badge
                    IconButton(
                        onClick = { viewModel.setRedmiHubOpen(true) },
                        modifier = Modifier.testTag("redmi_hub_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (needsOptimizationNotice) {
                                    Badge(containerColor = Color(0xFFFBBF24))
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.StayCurrentPortrait,
                                contentDescription = "Redmi Optimizer",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Trash button with badge
                    IconButton(
                        onClick = { viewModel.setTrashOpen(true) },
                        modifier = Modifier.testTag("trash_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (trashClips.isNotEmpty()) {
                                    Badge { Text("${trashClips.size}") }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Trash")
                        }
                    }

                    // Settings button
                    IconButton(
                        onClick = { viewModel.setSettingsOpen(true) },
                        modifier = Modifier.testTag("settings_button")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.setCreateDialogOpen(true) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .testTag("create_clip_fab")
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Clip or Note")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = horizontalSidePadding)
        ) {
            // Search Bar (expandable)
            AnimatedVisibility(visible = isSearchExpanded) {
                Column {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Search text, tags, or links...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .testTag("search_text_field"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear search")
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                }
            }

            // Redmi Note 13 Pro+ Quick Status Banner (shows if accessibility background service is needed)
            if (needsOptimizationNotice) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable { viewModel.setRedmiHubOpen(true) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Redmi Note 13 Pro+ Setup Recommended",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                "Enable background clip capture & Xiaomi battery whitelist",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                "Setup",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // Quick Add Input Card
            QuickAddCard(
                tags = tags,
                onSave = { content, isNote, tag ->
                    viewModel.addManualClip(content, isNote, tag, isStarred = false, colorHex = "")
                },
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Horizontal Filter bar
            FilterBar(
                currentFilter = selectedFilter,
                selectedTag = selectedTag,
                tags = tags,
                onFilterSelected = { filter, tag ->
                    viewModel.setFilter(filter, tag)
                },
                onAddTagClicked = { viewModel.setTagsDialogOpen(true) },
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Clips List or Empty state
            if (clips.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No clips matching '$searchQuery'"
                            else if (selectedFilter == ClipFilter.STARRED) "No starred items yet"
                            else if (selectedFilter == ClipFilter.NOTES) "No notes yet"
                            else "Your clipboard is ready!",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Copy text in any app, or tap '+' to save notes and templates here.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {
                    items(clips, key = { it.id }) { clip ->
                        ClipCard(
                            clip = clip,
                            onCopy = { viewModel.copyClip(clip) },
                            onToggleStar = { viewModel.toggleStar(clip) },
                            onDelete = { viewModel.moveToTrash(clip.id) },
                            onClick = { viewModel.openDetail(clip) }
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    selectedClipForDetail?.let { clip ->
        ClipDetailDialog(
            clip = clip,
            tags = tags,
            onDismiss = { viewModel.closeDetail() },
            onSaveUpdate = { updated -> viewModel.updateClip(updated) },
            onCopy = { viewModel.copyClip(clip) },
            onDelete = {
                viewModel.moveToTrash(clip.id)
                viewModel.closeDetail()
            },
            onToggleStar = { viewModel.toggleStar(clip) }
        )
    }

    if (isCreateDialogOpen) {
        CreateEditClipDialog(
            tags = tags,
            onDismiss = { viewModel.setCreateDialogOpen(false) },
            onSave = { content, isNote, tag, isStarred, colorHex ->
                viewModel.addManualClip(content, isNote, tag, isStarred, colorHex)
            }
        )
    }

    if (isRedmiHubOpen) {
        RedmiOptimizationDialog(
            curvedEdgePaddingEnabled = curvedEdgePadding,
            onToggleCurvedEdgePadding = { viewModel.setCurvedEdgePadding(it) },
            onDismiss = { viewModel.setRedmiHubOpen(false) }
        )
    }

    if (isTagsDialogOpen) {
        TagsManagementDialog(
            tags = tags,
            onAddTag = { name, color -> viewModel.addTag(name, color) },
            onDeleteTag = { name -> viewModel.deleteTag(name) },
            onDismiss = { viewModel.setTagsDialogOpen(false) }
        )
    }

    if (isTrashOpen) {
        TrashDialog(
            trashClips = trashClips,
            onRestore = { viewModel.restoreFromTrash(it) },
            onDeletePermanently = { viewModel.deletePermanently(it) },
            onEmptyTrash = { viewModel.emptyTrash() },
            onDismiss = { viewModel.setTrashOpen(false) }
        )
    }

    if (isSettingsOpen) {
        SettingsDialog(
            themeMode = themeMode,
            onThemeModeChange = { viewModel.setThemeMode(it) },
            curvedEdgePadding = curvedEdgePadding,
            onCurvedEdgePaddingChange = { viewModel.setCurvedEdgePadding(it) },
            autoMonitor = autoMonitor,
            onAutoMonitorChange = { viewModel.setAutoMonitor(it) },
            notificationEnabled = notificationEnabled,
            onNotificationEnabledChange = { viewModel.setNotificationEnabled(it) },
            floatingBubble = floatingBubble,
            onFloatingBubbleChange = { viewModel.setFloatingBubble(it) },
            hapticFeedback = hapticFeedback,
            onHapticFeedbackChange = { viewModel.setHapticFeedback(it) },
            onOpenRedmiHub = { viewModel.setRedmiHubOpen(true) },
            onExportBackup = { callback -> viewModel.exportBackup(callback) },
            onImportBackup = { json -> viewModel.importBackup(json) },
            onDismiss = { viewModel.setSettingsOpen(false) }
        )
    }
}
