package com.example.data.repository

import com.example.data.local.ClipboardDao
import com.example.data.model.ClipboardItem
import com.example.data.model.TagItem
import com.example.data.preferences.PreferenceManager
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

class ClipboardRepository(
    private val clipboardDao: ClipboardDao,
    private val preferenceManager: PreferenceManager
) {
    val activeClips: Flow<List<ClipboardItem>> = clipboardDao.getActiveClips()
    val starredClips: Flow<List<ClipboardItem>> = clipboardDao.getStarredClips()
    val notes: Flow<List<ClipboardItem>> = clipboardDao.getNotes()
    val trashClips: Flow<List<ClipboardItem>> = clipboardDao.getTrashClips()
    val allTags: Flow<List<TagItem>> = clipboardDao.getAllTags()
    val latestClip: Flow<ClipboardItem?> = clipboardDao.getLatestClip()

    fun searchClips(query: String): Flow<List<ClipboardItem>> {
        return clipboardDao.searchClips(query)
    }

    fun getClipsByTag(tag: String): Flow<List<ClipboardItem>> {
        return clipboardDao.getClipsByTag(tag)
    }

    fun getClipById(id: Long): Flow<ClipboardItem?> {
        return clipboardDao.getClipById(id)
    }

    suspend fun saveClipFromClipboard(text: String, isNote: Boolean = false, tag: String = ""): Boolean {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return false

        val existing = clipboardDao.getClipByContent(trimmed)
        if (existing != null) {
            if (preferenceManager.deduplicate.value) {
                // Move existing clip to top with updated timestamp and undelete if was in trash
                val updated = existing.copy(
                    timestamp = System.currentTimeMillis(),
                    isTrash = false,
                    tag = if (tag.isNotEmpty()) tag else existing.tag
                )
                clipboardDao.updateClip(updated)
                return true
            } else {
                return false
            }
        }

        val item = ClipboardItem(
            content = trimmed,
            timestamp = System.currentTimeMillis(),
            isNote = isNote,
            tag = tag
        )
        clipboardDao.insertClip(item)
        return true
    }

    suspend fun addClipManually(content: String, isNote: Boolean, tag: String, isStarred: Boolean, colorHex: String) {
        val item = ClipboardItem(
            content = content.trim(),
            timestamp = System.currentTimeMillis(),
            isNote = isNote,
            isStarred = isStarred,
            tag = tag,
            colorHex = colorHex
        )
        clipboardDao.insertClip(item)
    }

    suspend fun updateClip(clip: ClipboardItem) {
        clipboardDao.updateClip(clip)
    }

    suspend fun toggleStar(clip: ClipboardItem) {
        clipboardDao.updateClip(clip.copy(isStarred = !clip.isStarred))
    }

    suspend fun moveToTrash(id: Long) {
        clipboardDao.moveToTrash(id)
    }

    suspend fun restoreFromTrash(id: Long) {
        clipboardDao.restoreFromTrash(id)
    }

    suspend fun deletePermanently(id: Long) {
        clipboardDao.deletePermanently(id)
    }

    suspend fun emptyTrash() {
        clipboardDao.emptyTrash()
    }

    suspend fun addTag(name: String, colorHex: String) {
        clipboardDao.insertTag(TagItem(name = name.trim(), colorHex = colorHex))
    }

    suspend fun deleteTag(name: String) {
        clipboardDao.deleteTag(name)
    }

    // Export clips to JSON string for backup
    suspend fun exportToJson(clips: List<ClipboardItem>): String {
        val jsonArray = JSONArray()
        for (c in clips) {
            val obj = JSONObject()
            obj.put("content", c.content)
            obj.put("timestamp", c.timestamp)
            obj.put("isStarred", c.isStarred)
            obj.put("isNote", c.isNote)
            obj.put("tag", c.tag)
            obj.put("colorHex", c.colorHex)
            jsonArray.put(obj)
        }
        return jsonArray.toString(2)
    }

    // Import from JSON string
    suspend fun importFromJson(jsonStr: String): Int {
        var count = 0
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val content = obj.optString("content", "")
                if (content.isNotBlank()) {
                    val item = ClipboardItem(
                        content = content,
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        isStarred = obj.optBoolean("isStarred", false),
                        isNote = obj.optBoolean("isNote", false),
                        tag = obj.optString("tag", ""),
                        colorHex = obj.optString("colorHex", "")
                    )
                    clipboardDao.insertClip(item)
                    count++
                }
            }
        } catch (_: Exception) {}
        return count
    }
}
