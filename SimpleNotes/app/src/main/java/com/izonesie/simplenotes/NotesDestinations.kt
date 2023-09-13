package com.izonesie.simplenotes

sealed class Screen(val route: String) {
    data object ViewNotes : Screen("view")
    data object EditNote : Screen("editNote")
}