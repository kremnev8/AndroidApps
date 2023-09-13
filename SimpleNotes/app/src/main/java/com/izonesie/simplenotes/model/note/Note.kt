package com.izonesie.simplenotes.model.note

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) var noteId: Int,
    @ColumnInfo(name = "text") var text: String,
    @ColumnInfo(name = "imageUri") var imageUri: String,
    @ColumnInfo(name = "createdTime") var createdTime: Date,
    @ColumnInfo(name = "modifiedTime") var modifiedTime: Date
){
    companion object {
        fun create(): Note {
            return Note(0, "", "", Date(0), Date(0))
        }
    }
}

