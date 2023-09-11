import java.io.File
import androidx.compose.ui.window.*
import kotlin.collections.ArrayList
import ui.RenderWindows
import ui.WindowController

    
fun App() = application {
    val windowController = WindowController()

    Window(onCloseRequest = ::exitApplication, title = "Ixma") {
        InitialScreen(windowController)
    }
    RenderWindows(windowController)
}

fun main() {
    App()
}