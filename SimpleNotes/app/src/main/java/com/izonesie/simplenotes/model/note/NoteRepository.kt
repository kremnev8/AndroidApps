package com.izonesie.simplenotes.model.note

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class NoteRepository(private val noteDao: NoteDao) {

    fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes()
    }

    fun getNote(id: Int): Flow<Note> {
        return flow {
            emit(noteDao.getNote(id))
        }
    }

    suspend fun insertAllNotes(vararg users: Note) {
        withContext(Dispatchers.IO) {
            noteDao.insertAllNotes(*users)
        }
    }

    suspend fun deleteNote(id: Int) {
        withContext(Dispatchers.IO) {
            noteDao.deleteNote(id)
        }
    }
}