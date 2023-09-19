package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import data.PageRepository
import model.*

@Composable
fun PageEditViewScreen(link: String, windowController: WindowController = WindowController()) {
    var page by remember { mutableStateOf(PageRepository.findByLink(link)) }
    Column (){
        Text(page.link, fontSize = 16.sp, fontWeight = FontWeight.Bold,)
        Row(
            modifier = Modifier.fillMaxSize().background(Color.White)
        ) {
            Column(Modifier.weight(1f)) {
                PageViewScreen(page, modifier = Modifier.weight(1f).background(Color.White), windowController)
                RelatedMemosScreen(link, windowController)
            }

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
}

@Composable
private fun PageEditScreen(
    onValueChange: (String) -> Unit = {},
    onTitleChange: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    initText: String = ""
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
        modifier = modifier.then(
            Modifier.padding(3.dp).fillMaxHeight().background(Color.White)
        )
    )
}

@Composable
fun PageViewScreen(
    page: Page,
    modifier: Modifier = Modifier,
    windowController: WindowController = WindowController()
) {
    Column(modifier = modifier.background(Color.White).padding(16.dp)) {
        page.getLines().forEach { it ->
            LineUiComponent(it, windowController)
        }
    }
}

@Composable
private fun LineUiComponent(line: Line, windowController: WindowController) {
    val subStrings: List<SubString> = line.dividedStringsByLinks()
    Row {
        subStrings.forEach {
            if (it.isLinkText)
                Text(it.text,
                    color = Color.Blue,
                    fontWeight = FontWeight.Bold, // 太字
                    textDecoration = TextDecoration.Underline, // アンダーライン
                    modifier = Modifier.clickable { windowController.openNewPageWindow(it.text) })
            else
                Text(it.text)
        }
    }
}

@Composable
fun RelatedMemosScreen(link: String, windowController: WindowController) {
    val pages = PageRepository.findPagesBySubString(link)
    Column(modifier = Modifier) {
        for (page in pages) {
            val root = buildTree(page.plainValue().split("\n"))
            val nodes = findNodesContainingWord(root, link)
            Text(
                "from:${page.link}", modifier = Modifier.clickable { windowController.openNewPageWindow(page.link) },
                color = Color.Blue
            )
            for (node in nodes) {
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    elevation = 8.dp
                ) {
                    Column {
                        node.concatenateLines().forEach {
                            LineUiComponent(it, windowController)
                        }
                        Spacer(Modifier.padding(16.dp))
                    }

                }
            }

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
        //PageEditViewScreen("firstMemo")
        //RelatedMemosScreen("うどん")
    }
}