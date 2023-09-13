package com.izonesie.simplenotes.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.izonesie.simplenotes.model.note.Note
import com.izonesie.simplenotes.model.note.NoteDao
import com.izonesie.simplenotes.util.Converters

@Database(entities = [Note::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
