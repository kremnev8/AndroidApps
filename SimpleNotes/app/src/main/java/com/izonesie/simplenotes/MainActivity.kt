package com.izonesie.simplenotes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.izonesie.simplenotes.screens.EditNoteScreen
import com.izonesie.simplenotes.screens.ViewNotesScreen
import com.izonesie.simplenotes.screens.viewmodel.EditNoteViewModel


class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var instance: MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Dependencies.init(applicationContext)
        PhotoProvider.init(this)
        super.onCreate(savedInstanceState)
        instance = this

        setContentView(
            ComposeView(this).apply {
                setContent {
                    SimpleNotesApp()
                }
            })
    }

    @Composable
    private fun SimpleNotesApp() {
        val navController = rememberNavController()
        NotesNavHost(navController)
    }

    @Composable
    fun NotesNavHost(
        navController: NavHostController,
        modifier: Modifier = Modifier
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.ViewNotes.route,
            modifier = modifier
        ) {
            composable(route = Screen.ViewNotes.route) {
                ViewNotesScreen(
                    onAddNote = {
                        navController.navigate(Screen.EditNote.route)
                    },
                    onEditNote = {
                        navController.navigate(Screen.EditNote.route.plus("?noteId=").plus(it))
                    }
                )
            }
            composable(
                route = Screen.EditNote.route.plus("?noteId={noteId}"),
                arguments = listOf(navArgument("noteId") { defaultValue = "0" }),
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(700)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(700)
                    )
                }
            )
            { backStackEntry ->
                EditNoteScreen(
                    onReturn = {
                        navController.navigate(Screen.ViewNotes.route)
                    },
                    viewModel = EditNoteViewModel(
                        backStackEntry.arguments,
                        Dependencies.noteRepository
                    )
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PhotoProvider.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}