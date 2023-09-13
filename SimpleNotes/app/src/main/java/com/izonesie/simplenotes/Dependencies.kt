package com.izonesie.simplenotes

import android.content.Context
import androidx.room.Room
import com.izonesie.simplenotes.model.AppDatabase
import com.izonesie.simplenotes.model.note.NoteRepository

object Dependencies {

    private lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context
    }

    private val appDatabase: AppDatabase by lazy {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "app_database.db")
            //.createFromAsset("room_article.db")
            .build()
    }

    val noteRepository: NoteRepository by lazy { NoteRepository(appDatabase.noteDao()) }
}