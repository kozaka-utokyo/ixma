import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetAdapter
import java.awt.dnd.DropTargetDropEvent
import java.io.File
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import kotlin.collections.ArrayList

fun App() = singleWindowApplication(
    title = "ixma", state = WindowState(width = 1000.dp, height = 1000.dp)
) {
    // ウィンドウの状態管理変数
    var isOpenDataWindow: Boolean by remember { mutableStateOf(false) }
    var isOpenEditWindow by remember { mutableStateOf(false) }
    // データパス
    var filePath by remember { mutableStateOf("") }
    var rawData by remember { mutableStateOf(arrayListOf<List<String>>()) }
    var weight by remember { mutableStateOf(3) }

    var text by remember { mutableStateOf("") }
    var textFieldString by remember {
        mutableStateOf(text)
    }
    TextField(
        value = textFieldString,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.Black,
            backgroundColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        placeholder = { Text(text = "textbox") },
        onValueChange = {
            textFieldString = it
                        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            autoCorrect = true,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        modifier = Modifier.border(1.dp, Color.Black).padding(3.dp).height(800.dp).fillMaxWidth(),
    )

//    if (isOpenDataWindow) {
//        EditWindow(rawData, filePath)
//    }
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

