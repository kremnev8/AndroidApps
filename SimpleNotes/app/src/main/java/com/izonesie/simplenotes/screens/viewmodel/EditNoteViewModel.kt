package com.izonesie.simplenotes.screens.viewmodel

import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.izonesie.simplenotes.PhotoProvider
import com.izonesie.simplenotes.common.ObservableViewModel
import com.izonesie.simplenotes.model.note.Note
import com.izonesie.simplenotes.model.note.NoteRepository
import com.izonesie.simplenotes.util.IMediaRequestCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.Date

private const val NOTE_ID = "noteId"

class EditNoteViewModel(
    arguments: Bundle?,
    private val noteRepository: NoteRepository
) : ObservableViewModel(), IMediaRequestCallback {

    private var _noteState = mutableStateOf(Note.create())
    val noteState: State<Note> = _noteState

    init {
        parseArguments(arguments)
    }

    private fun parseArguments(arguments: Bundle?) {
        if (arguments == null) return

        val id = checkNotNull(arguments.getString(NOTE_ID)).toInt();
        if (id == 0) return

        viewModelScope.launch {
            noteRepository.getNote(id)
                .flowOn(Dispatchers.IO)
                .catch { e -> Log.e("Note Edit", e.message!!) }
                .collect {
                    _noteState.value = it
                }
        }
    }

    fun setNoteText(text: String) {
        _noteState.value = _noteState.value.copy(
            text = text
        )
    }

    fun addPhoto() {
        PhotoProvider.requestMedia(this)
    }

    fun removePhoto() {
        _noteState.value = _noteState.value.copy(
            imageUri = ""
        )
    }

    fun saveNote() {
        val now = Date()
        val note = _noteState.value;

        if (note.noteId == 0) {
            note.createdTime = now
        }
        note.modifiedTime = now

        _noteState.value = note;
        viewModelScope.launch {
            noteRepository.insertAllNotes(note)
        }
    }

    override fun onMediaSelected(imageUri: String) {
        _noteState.value = _noteState.value.copy(
            imageUri = imageUri
        )
    }
}