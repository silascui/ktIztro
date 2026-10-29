package io.github.silascui.ktIztro

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    val windowState = rememberWindowState(width = 440.dp, height = 900.dp)
    Window(
        onCloseRequest = ::exitApplication,
        title = "紫微天盘 · ktIztro",
        state = windowState
    ) {
        App()
    }
}
