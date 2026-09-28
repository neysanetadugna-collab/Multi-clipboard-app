package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ClipboardItem
import com.example.data.model.TagItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [ClipboardItem::class, TagItem::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clipboardDao(): ClipboardDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "multi_clipboard.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate default starter tags and welcome note
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = getInstance(context).clipboardDao()
                            dao.insertTag(TagItem("Work", "#38BDF8"))
                            dao.insertTag(TagItem("Personal", "#34D399"))
                            dao.insertTag(TagItem("Links", "#22D3EE"))
                            dao.insertTag(TagItem("Code", "#A78BFA"))
                            dao.insertTag(TagItem("Important", "#FBBF24"))

                            // Initial clip welcoming user
                            dao.insertClip(
                                ClipboardItem(
                                    content = "Welcome to Multi Clipboard! Fully optimized for Redmi Note 13 Pro+ with 1.5K AMOLED display, curved edge protection, and background clipboard monitoring.",
                                    tag = "Important",
                                    colorHex = "#00E5FF",
                                    isStarred = true
                                )
                            )
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
