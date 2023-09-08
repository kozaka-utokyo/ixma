import java.io.File
import androidx.compose.ui.window.*
import kotlin.collections.ArrayList
import ui.RenderWindows
import ui.WindowController


fun App() = application {
    val windowController = WindowController()

    Window(onCloseRequest = ::exitApplication) {
        InitialScreen(windowController)
    }
    RenderWindows(windowController)
}

fun getRawData(filePath: String): ArrayList<List<String>> {
    var rawData = arrayListOf<List<String>>()
    try {
        val data = readCsv(File(filePath))
        rawData.add((List(data[0].size) { it }.map { it.toString() }))
        rawData.add(data[0].keys.toList())
        for (row in data) {
            rawData.add(row.values.toList())
        }
    } catch (e: Exception) {
        println("ERROR")
        println(filePath)
    }
    return rawData
}


fun main() {
    App()
//    testFindAllLinks()
}