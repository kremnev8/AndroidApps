package com.izonesie.simplenotes.model.note

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM note ORDER BY createdTime DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE noteId = :id")
    fun getNote(id: Int): Note

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllNotes(vararg users: Note)

    @Query("DELETE FROM note WHERE noteId = :id")
    fun deleteNote(id: Int)
}
