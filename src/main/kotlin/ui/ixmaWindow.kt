package ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.window.Window

class WindowController {
    val subWindowPageLinks = mutableStateListOf<String>()

    fun openNewPageWindow(link: String) {
        subWindowPageLinks += link
    }

    fun closePageWindow(link: String) {
        subWindowPageLinks.remove(link)
    }
}

@Composable
fun RenderWindows(windowController: WindowController) {
    windowController.subWindowPageLinks.forEach { linkString ->
        Window(onCloseRequest = { windowController.closePageWindow(linkString) }, title = linkString) {
            PageEditViewScreen(linkString, windowController)
        }
    }
}