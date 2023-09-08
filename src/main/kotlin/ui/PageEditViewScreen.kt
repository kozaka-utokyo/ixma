package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import data.PageRepository
import model.Line
import model.Page
import model.SubString

@Composable
fun PageEditViewScreen(link: String, windowController: WindowController = WindowController()) {
    var page by remember { mutableStateOf(PageRepository.findByLink(link)) }

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        PageViewScreen(page, modifier = Modifier.weight(1f),windowController)
        PageEditScreen(
            onValueChange = {
                run {
                    page = page.editAllLineByEntireString(it)
                    println(page)
                }
            },
            onTitleChange = { page = page.editTitle(it) },
            modifier = Modifier.weight(1f),
            initText = page.plainValue()
        )
    }
}

@Composable
private fun PageEditScreen(
    onValueChange: (String) -> Unit = {},
    onTitleChange: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    initText:String = ""
) {
    var textFieldString by remember {
        mutableStateOf(initText)
    }
    TextField(
        value = textFieldString,
        placeholder = { Text(text = "textbox") },
        onValueChange = {
            textFieldString = it
            onValueChange(it)
        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            autoCorrect = true,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        modifier = modifier.then(Modifier.border(1.dp, Color.Black).padding(3.dp).fillMaxHeight()) // ここを修正
    )
}

@Composable
fun PageViewScreen(
    page: Page,
    modifier: Modifier = Modifier,
    windowController: WindowController = WindowController()
) {
    Column(modifier = modifier) { // ここでmodifierを追加
        page.getLines().forEach { it ->
            Line(it, windowController)
        }
    }
}

@Composable
private fun Line(line: Line, windowController: WindowController) {
    val subStrings: List<SubString> = line.dividedStringsByLinks()
    Row {
        subStrings.forEach {
            if (it.isLinkText)
                Text(it.text, modifier = Modifier.clickable { windowController.openNewPageWindow(it.text) })
            else
                Text(it.text)
        }
    }
}


@Composable
@Preview
fun PageEditViewScreenPreviewPreview() {
    PageEditViewScreen("hoge")
}

@Composable
@Preview
fun PageEditScreenPreview() {

}


fun main() {
    singleWindowApplication(title = "testPageEditViewScreen", state = WindowState(width = 1000.dp, height = 1000.dp)) {
        PageEditViewScreen("firstMemo")
    }
}