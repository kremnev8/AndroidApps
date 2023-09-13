@file:OptIn(
    ExperimentalGlideComposeApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)

package com.izonesie.simplenotes.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.izonesie.simplenotes.Dependencies
import com.izonesie.simplenotes.R
import com.izonesie.simplenotes.common.pullrefresh.pullRefresh
import com.izonesie.simplenotes.common.pullrefresh.rememberPullRefreshState
import com.izonesie.simplenotes.model.note.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.time.Duration.Companion.milliseconds

private val requestOptions: RequestOptions =
    RequestOptions().transform(CenterCrop(), RoundedCorners(16))

@Composable
fun ViewNotesScreen(
    onAddNote: () -> Unit,
    onEditNote: (Int) -> Unit
) {
    val data = Dependencies.noteRepository.getAllNotes().collectAsState(listOf(), Dispatchers.IO)
    val coroutineScope = rememberCoroutineScope()

    ViewNotesContent(
        data.value,
        onAddNote,
        onEditNote,
        onDeleteNote = {
            coroutineScope.launch {
                Dependencies.noteRepository.deleteNote(it)
            }
        }
    )
}

@Composable
//@Preview
fun ViewNotesPreview() {
    val data = listOf(
        Note(0, "Liza was here!", "", Date(), Date()),
        Note(1, "Max was here!", "", Date(), Date()),
        Note(2, "Slava wasn't here!", "", Date(), Date())
    )
    ViewNotesContent(
        data,
        onAddNote = {},
        onEditNote = {},
        onDeleteNote = {}
    )
}

@Composable
fun ViewNotesContent(
    notes: List<Note>,
    onAddNote: () -> Unit,
    onEditNote: (Int) -> Unit,
    onDeleteNote: (Int) -> Unit
) {
    Scaffold(topBar = { },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNote) {
                Icon(Icons.Filled.Add, stringResource(R.string.add_note_button_desc))
            }
        },
        content = { padding ->
            val isPulling by remember { mutableStateOf(false) }
            val pullState = rememberPullRefreshState(isPulling, onAddNote)

            val openDialog = NoteEditDialog(onEditNote, onDeleteNote)

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(padding)
                    .pullRefresh(pullState)
            ) {
                items(notes, { it.noteId }) {
                    NoteViewItem(it, onEditNote = openDialog)
                }
            }
        })
}

@Composable
@Preview
fun NoteEditDialogPreview() {
    Scaffold(topBar = { },
        content = { padding ->

            val openDialog = NoteEditDialog({}, {})

            Column(modifier = Modifier.padding(padding)) {

            }
        })
}

@Composable
private fun NoteEditDialog(
    onEditNote: (Int) -> Unit,
    onDeleteNote: (Int) -> Unit
): (Int) -> Unit {
    val openDialog = remember { mutableStateOf(false) }
    val selectedNoteId = remember { mutableIntStateOf(0) }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                openDialog.value = false
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .padding(start = 32.dp, end = 32.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        text = "What to do?",
                        textAlign = TextAlign.Center
                    )
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = RoundedCornerShape(20),
                        onClick = {
                            onEditNote(selectedNoteId.intValue)
                            openDialog.value = false
                        }) {
                        Text(text = "Edit Note")
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = RoundedCornerShape(10),
                        onClick = {
                            onDeleteNote(selectedNoteId.intValue)
                            openDialog.value = false
                        }) {
                        Text(text = "Delete Note")
                    }
                }
            }
        }
    }

    fun openDialog(noteId: Int) {
        selectedNoteId.intValue = noteId
        openDialog.value = true
    }

    return ::openDialog
}

@Composable
fun NoteViewItem(
    note: Note,
    onEditNote: (Int) -> Unit
) {
    val emptyRect = placeholder(R.drawable.empty_placeholder);

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Top)
            .background(colorResource(R.color.item_first))
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    onEditNote(note.noteId)
                }
            )
    ) {

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = note.text,
            style = TextStyle(fontSize = 16.sp)
        )
        if (note.imageUri != "") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .aspectRatio(1f, false),
            ) {
                GlideImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    model = note.imageUri,
                    contentDescription = stringResource(R.string.user_image_desc),
                    loading = emptyRect,
                    failure = emptyRect
                ) {
                    it.apply(requestOptions)
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 100.dp, end = 100.dp)
                    .height(1.dp)
                    .background(colorResource(R.color.gray))
                    .align(Alignment.Center)
            )
        }
    }
}