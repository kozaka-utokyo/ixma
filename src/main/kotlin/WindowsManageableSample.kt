import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ui.RenderWindows
import ui.WindowController


fun main() = application {
    val windowController = WindowController()

    Window(onCloseRequest = ::exitApplication) {
        Button(onClick = { windowController.openNewPageWindow("firstMemo") }) {
            Text("Open FirstMemo")
        }
    }
    RenderWindows(windowController)
}
