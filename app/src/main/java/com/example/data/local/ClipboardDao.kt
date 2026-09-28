package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ClipboardItem
import com.example.data.model.TagItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ClipboardDao {
    @Query("SELECT * FROM clipboard_items WHERE isTrash = 0 ORDER BY timestamp DESC")
    fun getActiveClips(): Flow<List<ClipboardItem>>

    @Query("SELECT * FROM clipboard_items WHERE isTrash = 0 AND isStarred = 1 ORDER BY timestamp DESC")
    fun getStarredClips(): Flow<List<ClipboardItem>>

    @Query("SELECT * FROM clipboard_items WHERE isTrash = 0 AND isNote = 1 ORDER BY timestamp DESC")
    fun getNotes(): Flow<List<ClipboardItem>>

    @Query("SELECT * FROM clipboard_items WHERE isTrash = 1 ORDER BY timestamp DESC")
    fun getTrashClips(): Flow<List<ClipboardItem>>

    @Query("SELECT * FROM clipboard_items WHERE isTrash = 0 AND tag = :tag ORDER BY timestamp DESC")
    fun getClipsByTag(tag: String): Flow<List<ClipboardItem>>

    @Query("SELECT * FROM clipboard_items WHERE isTrash = 0 AND content LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchClips(query: String): Flow<List<ClipboardItem>>

    @Query("SELECT * FROM clipboard_items WHERE content = :content LIMIT 1")
    suspend fun getClipByContent(content: String): ClipboardItem?

    @Query("SELECT * FROM clipboard_items WHERE isTrash = 0 ORDER BY timestamp DESC LIMIT 1")
    fun getLatestClip(): Flow<ClipboardItem?>

    @Query("SELECT * FROM clipboard_items WHERE id = :id LIMIT 1")
    fun getClipById(id: Long): Flow<ClipboardItem?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClip(item: ClipboardItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClips(items: List<ClipboardItem>)

    @Update
    suspend fun updateClip(item: ClipboardItem)

    @Query("UPDATE clipboard_items SET isTrash = 1 WHERE id = :id")
    suspend fun moveToTrash(id: Long)

    @Query("UPDATE clipboard_items SET isTrash = 0 WHERE id = :id")
    suspend fun restoreFromTrash(id: Long)

    @Query("DELETE FROM clipboard_items WHERE id = :id")
    suspend fun deletePermanently(id: Long)

    @Query("DELETE FROM clipboard_items WHERE isTrash = 1")
    suspend fun emptyTrash()

    @Query("SELECT COUNT(*) FROM clipboard_items WHERE isTrash = 0")
    suspend fun getActiveCount(): Int

    // Tags
    @Query("SELECT * FROM tag_items ORDER BY name ASC")
    fun getAllTags(): Flow<List<TagItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: TagItem)

    @Query("DELETE FROM tag_items WHERE name = :name")
    suspend fun deleteTag(name: String)
}
