package com.izonesie.simplenotes.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.izonesie.simplenotes.R
import com.izonesie.simplenotes.screens.viewmodel.EditNoteViewModel

private val requestOptions: RequestOptions =
    RequestOptions().transform(CenterCrop(), RoundedCorners(16))

@Composable
fun EditNoteScreen(
    viewModel: EditNoteViewModel,
    onReturn: () -> Unit,
) {
    val note = viewModel.noteState.value

    EditNoteContent(
        text = note.text,
        onTextChange = viewModel::setNoteText,
        imageUri = note.imageUri,
        requestImage = viewModel::addPhoto,
        clearImage = viewModel::removePhoto,
        cancelEditing = onReturn,
        save = {
            viewModel.saveNote()
            onReturn()
        }
    )
}


@Composable
fun EditNoteContent(
    text: String,
    onTextChange: (String) -> Unit,
    imageUri: String,
    requestImage: () -> Unit,
    clearImage: () -> Unit,
    cancelEditing: () -> Unit,
    save: () -> Unit,
) {
    Scaffold(topBar = { },
        bottomBar = {
            NoteEditorBottomBar(cancelEditing, requestImage, save)
        }, content = { padding ->
            NoteEditor(padding, text, onTextChange, imageUri, clearImage)
        })
}

@Composable
private fun NoteEditorBottomBar(
    cancelEditing: () -> Unit,
    requestImage: () -> Unit,
    save: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        FloatingActionButton(
            onClick = cancelEditing,
        ) {
            Icon(Icons.Filled.Close, stringResource(R.string.cancel_note_editing_desc))
        }

        ExtendedFloatingActionButton(
            onClick = requestImage,
            icon = {
                Icon(Icons.Filled.Add, stringResource(R.string.add_photo_button_desc))
            },
            text = { Text(text = stringResource(R.string.add_photo_text)) }
        )

        FloatingActionButton(
            onClick = save,
        ) {
            Icon(Icons.Filled.Check, stringResource(R.string.save_button_desc))
        }
    }
}

@Composable
@OptIn(ExperimentalGlideComposeApi::class)
private fun NoteEditor(
    padding: PaddingValues,
    text: String,
    onTextChange: (String) -> Unit,
    imageUri: String,
    clearImage: () -> Unit
) {
    val emptyRect = placeholder(R.drawable.empty_placeholder);
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(padding)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(Alignment.Top)
                .background(colorResource(R.color.item_first))
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp)
                    .padding(16.dp),
                value = text,
                textStyle = TextStyle(fontSize = 16.sp),
                onValueChange = onTextChange,
                colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.Transparent),
                singleLine = false
            )
            if (imageUri != "") {
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
                        model = imageUri,
                        contentDescription = stringResource(R.string.user_image_desc),
                        loading = emptyRect,
                        failure = emptyRect
                    ){
                        it.apply(requestOptions)
                    }

                    Image(
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)
                            .align(Alignment.TopEnd)
                            .clickable(onClick = clearImage),
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = stringResource(R.string.remove_button_desc),
                    )
                }
            }
        }
    }
}

@Preview(device = "id:pixel_5")
@Composable
fun AddNoteContentPreview() {
    val uri = "https://developer.android.com/static/images/jetpack/compose/components/fab.png";
    var text by remember { mutableStateOf("Elise had breakfast") }
    var imageUri by remember { mutableStateOf(uri) }

    EditNoteContent(
        text = text,
        onTextChange = { text = it },
        imageUri = imageUri,
        requestImage = { imageUri = uri },
        clearImage = { imageUri = "" },
        cancelEditing = {},
        save = {}
    )
}
