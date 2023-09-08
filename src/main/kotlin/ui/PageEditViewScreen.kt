package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
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
private fun PageEditViewScreen(link:String) {
    var page by remember { mutableStateOf(PageRepository.findByLink(link)) }

    Row{
        PageViewScreen(page)
        PageEditScreen(
            onValueChange = {
                run {
                    page = page.editAllLineByEntireString(it)
                    println(page)
                }
            },
            onTitleChange = {page = page.editTitle(it)}
        )

    }
}



@Composable
private fun PageEditScreen(
    onValueChange: (String) -> Unit = {},
    onTitleChange:(String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var textFieldString by remember {
        mutableStateOf("")
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
        modifier = Modifier.border(1.dp, Color.Black).padding(3.dp).height(800.dp).fillMaxWidth(),
    )
}

@Composable
fun PageViewScreen(page:Page,modifier: Modifier=Modifier) {
    Column {
        page.getLines().forEach{
                it -> Line(it, onLinkClick = {})
        }
    }
}
@Composable
private fun Line(line:Line,onLinkClick:(link:String)->Unit){
    val subStrings:List<SubString> = line.dividedStringsByLinks()
    Row{
        subStrings.forEach {
            if (it.isLinkText)
                Text(it.text, modifier = Modifier.clickable { println(it.text) })
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



fun main(){
    singleWindowApplication(title = "testPageEditViewScreen", state = WindowState(width = 1000.dp, height = 1000.dp)) {
        PageEditViewScreen("hoge")
    }
}