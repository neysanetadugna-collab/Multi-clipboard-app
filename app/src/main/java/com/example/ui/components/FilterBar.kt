package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TagItem
import com.example.ui.ClipFilter

@Composable
fun FilterBar(
    currentFilter: ClipFilter,
    selectedTag: String?,
    tags: List<TagItem>,
    onFilterSelected: (ClipFilter, String?) -> Unit,
    onAddTagClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .horizontalScroll(scrollState)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterItem(
            label = "All",
            icon = Icons.Default.FormatListBulleted,
            isSelected = currentFilter == ClipFilter.ALL,
            onClick = { onFilterSelected(ClipFilter.ALL, null) }
        )

        FilterItem(
            label = "Starred",
            icon = Icons.Default.Star,
            isSelected = currentFilter == ClipFilter.STARRED,
            onClick = { onFilterSelected(ClipFilter.STARRED, null) }
        )

        FilterItem(
            label = "Notes",
            icon = Icons.Default.Description,
            isSelected = currentFilter == ClipFilter.NOTES,
            onClick = { onFilterSelected(ClipFilter.NOTES, null) }
        )

        FilterItem(
            label = "Links",
            icon = Icons.Default.Link,
            isSelected = currentFilter == ClipFilter.LINKS,
            onClick = { onFilterSelected(ClipFilter.LINKS, null) }
        )

        FilterItem(
            label = "Contacts",
            icon = Icons.Default.ContactPhone,
            isSelected = currentFilter == ClipFilter.CONTACTS,
            onClick = { onFilterSelected(ClipFilter.CONTACTS, null) }
        )

        // Custom tags
        tags.forEach { tag ->
            val isTagSelected = currentFilter == ClipFilter.TAG && selectedTag == tag.name
            val parsedColor = try {
                Color(android.graphics.Color.parseColor(tag.colorHex))
            } catch (_: Exception) {
                MaterialTheme.colorScheme.primary
            }

            FilterChip(
                selected = isTagSelected,
                onClick = { onFilterSelected(ClipFilter.TAG, tag.name) },
                label = { Text(tag.name, fontSize = 12.sp) },
                leadingIcon = {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(parsedColor)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }

        // Add Tag Button
        FilterChip(
            selected = false,
            onClick = onAddTagClicked,
            label = { Text("+ Tag", fontSize = 12.sp) },
            leadingIcon = {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "New Tag",
                    modifier = Modifier.size(14.dp)
                )
            }
        )
    }
}

@Composable
private fun FilterItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label, fontSize = 12.sp) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(15.dp),
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}
